package settingdust.surveyor_atlases.v21_1.marker

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.codecs.RecordCodecBuilder
import folk.sisby.surveyor.landmark.Landmark
import folk.sisby.surveyor.landmark.component.LandmarkComponentTypes
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker
import net.mehvahdjukaar.moonlight.api.util.Utils
import net.mehvahdjukaar.supplementaries.Supplementaries
import net.mehvahdjukaar.supplementaries.common.misc.map_markers.ModMapMarkers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import settingdust.surveyor_atlases.Markers
import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.mixin.map_atlases.MoonlightCompatAccessor
import settingdust.surveyor_atlases.util.Identifier
import settingdust.surveyor_atlases.v21_1.util.Codecs
import settingdust.surveyor_atlases.v21_1.util.StreamCodecs
import java.util.*

class SurveyorLandmarkMarker(
    val mark: Landmark? = null,
    type: Holder<MLMapDecorationType<*, *>>,
    pos: BlockPos = mark?.get(LandmarkComponentTypes.POS) ?: BlockPos.ZERO,
    rotation: Float = 0F,
    name: Optional<Component> = Optional.ofNullable(mark?.get(LandmarkComponentTypes.NAME)),
    shouldRefresh: Optional<Boolean> = Optional.empty(),
    shouldSave: Optional<Boolean> = Optional.of(false),
    preventsExtending: Boolean = false,
) : MLMapMarker<SurveyorLandmarkDecoration>(
    type,
    pos,
    rotation,
    name,
    shouldRefresh,
    shouldSave,
    preventsExtending
) {
    companion object {
        val MAP_CODEC = RecordCodecBuilder.mapCodec<SurveyorLandmarkMarker> { instance ->
            instance.group(Codecs.LANDMARK.fieldOf("landmark").forGetter { it.mark })
                .and(baseCodecGroup(instance))
                .apply(instance, ::SurveyorLandmarkMarker)
        }
    }


    constructor(mark: Landmark) : this(
        mark,
        SurveyorAtlasesMarkers.SURVEYOR_LANDMARK.getHolder(Utils.hackyGetRegistryAccess())
    )

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte) =
        SurveyorLandmarkDecoration(type, mapX, mapY, rot, name, mark)
}

class SurveyorLandmarkDecoration(
    type: Holder<MLMapDecorationType<*, *>>,
    x: Byte,
    y: Byte,
    rot: Byte,
    name: Optional<Component>,
    val mark: Landmark?
) : MLMapDecoration(type, x, y, rot, name) {
    companion object {
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SurveyorLandmarkDecoration> = StreamCodec.composite(
            MLMapDecorationType.STREAM_CODEC, MLMapDecoration::getType,
            ByteBufCodecs.BYTE, MLMapDecoration::getX,
            ByteBufCodecs.BYTE, MLMapDecoration::getY,
            ByteBufCodecs.BYTE, MLMapDecoration::getRot,
            ComponentSerialization.OPTIONAL_STREAM_CODEC, { Optional.ofNullable(it.getDisplayName()) },
            StreamCodecs.nullable(StreamCodecs.LANDMARK), SurveyorLandmarkDecoration::mark,
            ::SurveyorLandmarkDecoration
        )
    }
}

class SurveyorLandmarkDecorationRenderer(texture: ResourceLocation) :
    MapDecorationRenderer<SurveyorLandmarkDecoration>(texture) {
    var currentDecoration: SurveyorLandmarkDecoration? = null

    override fun rendersOnFrame(decoration: SurveyorLandmarkDecoration) = false

    override fun getColor(decoration: SurveyorLandmarkDecoration) =
        decoration.mark?.get(LandmarkComponentTypes.COLOR) ?: -1

    override fun render(
        decoration: SurveyorLandmarkDecoration,
        matrixStack: PoseStack,
        vertexBuilder: VertexConsumer,
        buffer: MultiBufferSource,
        mapData: MapItemSavedData?,
        isOnFrame: Boolean,
        light: Int,
        index: Int,
        rendersText: Boolean
    ): Boolean {
        currentDecoration = decoration
        return super.render(
            decoration,
            matrixStack,
            vertexBuilder,
            buffer,
            mapData,
            isOnFrame,
            light,
            index,
            rendersText
        )
    }

    override fun renderDecorationSprite(
        matrixStack: PoseStack?,
        buffer: MultiBufferSource?,
        vertexBuilder: VertexConsumer?,
        light: Int,
        index: Int,
        color: Int,
        alpha: Int,
        outline: Boolean
    ) {
        val mark = currentDecoration!!.mark ?: return
        val defaultRenderer by lazy {
            MapDecorationClientManager
                .getRenderer<MLMapDecoration>(MapDataRegistry.GENERIC_STRUCTURE_MARKER.getHolder(Utils.hackyGetRegistryAccess()))
        }
        val decorationRegistry = MapDataRegistry.getMapDecorationRegistry(Utils.hackyGetRegistryAccess())
        fun Identifier.match() = mark.id().namespace == namespace && mark.id().path.startsWith(path)
        val renderer = when {
            SurveyorAtlases.Compats.MAP_ATLASES && Markers.BLOCK.match() ->
                MapDecorationClientManager.getRenderer(
                    decorationRegistry.getHolder(MoonlightCompatAccessor.getPinTypeId()).orElseThrow()
                )

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.DEATH.match() ->
                MapDecorationClientManager.getRenderer(
                    decorationRegistry.getHolder(Supplementaries.res("death_marker")).orElseThrow()
                )

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_HOME.match() ->
                MapDecorationClientManager.getRenderer(
                    decorationRegistry.getHolder(ModMapMarkers.BED_FACTORY_ID).orElseThrow()
                )

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_MEETING.match() ->
                MapDecorationClientManager.getRenderer(
                    decorationRegistry.getHolder(Supplementaries.res("bell")).orElseThrow()
                )

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_LODESTONE.match() ->
                MapDecorationClientManager.getRenderer(
                    decorationRegistry.getHolder(Supplementaries.res("lodestone")).orElseThrow()
                )

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_NETHER_PORTAL.match() ->
                MapDecorationClientManager.getRenderer(
                    decorationRegistry.getHolder(Supplementaries.res("nether_portal")).orElseThrow()
                )

            SurveyorAtlases.Compats.SURCEYSTONES && (Markers.SURCEYSTONES_BLAYSTONE.match() || Markers.SURCEYSTONES_FWATSTONES.match()) -> {
                MapDecorationClientManager.getRenderer(
                    decorationRegistry.getHolder(ModMapMarkers.WAYSTONE_FACTORY_ID).orElseThrow()
                )
            }

            else -> defaultRenderer
        }

        renderer.renderDecorationSprite(matrixStack, buffer, vertexBuilder, light, index, color, alpha, outline)
        currentDecoration = null
    }
}