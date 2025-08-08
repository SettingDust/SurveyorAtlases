package settingdust.surveyor_atlases.v1_21.marker

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import folk.sisby.surveyor.WorldSummary
import folk.sisby.surveyor.landmark.Landmark
import folk.sisby.surveyor.landmark.Landmarks
import folk.sisby.surveyor.landmark.NetherPortalLandmark
import folk.sisby.surveyor.landmark.PlayerDeathLandmark
import folk.sisby.surveyor.landmark.SimplePointLandmark
import folk.sisby.surveyor.landmark.SimplePointOfInterestLandmark
import folk.sisby.surveystones.BlaystoneLandmark
import folk.sisby.surveystones.FwaystoneLandmark
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker
import net.mehvahdjukaar.moonlight.api.util.Utils
import net.mehvahdjukaar.supplementaries.Supplementaries
import net.mehvahdjukaar.supplementaries.common.misc.map_markers.ModMapMarkers
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.mixin.map_atlases.MoonlightCompatAccessor
import java.util.*

class SurveyorLandmarkMarker(
    val mark: Landmark<*>? = null,
    type: Holder<MLMapDecorationType<*, *>>,
    pos: BlockPos = mark?.pos() ?: BlockPos.ZERO,
    rotation: Float = 0F,
    name: Optional<Component> = Optional.ofNullable(mark?.name()),
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
        val MAP_CODEC: MapCodec<SurveyorLandmarkMarker> = RecordCodecBuilder.mapCodec<SurveyorLandmarkMarker> { instance ->
            instance.group(Codecs.LANDMARK.fieldOf("landmark").forGetter { it.mark })
                .and(baseCodecGroup(instance))
                .apply(instance, ::SurveyorLandmarkMarker)
        }
    }


    constructor(mark: Landmark<*>): this(mark, SurveyorAtlasesMarkers.SURVEYOR_LANDMARK.holderUnsafe)

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte) =
        SurveyorLandmarkDecoration(type, mapX, mapY, rot, name, mark)
}

class SurveyorLandmarkDecoration(
    type: Holder<MLMapDecorationType<*, *>>,
    x: Byte,
    y: Byte,
    rot: Byte,
    name: Optional<Component>,
    val mark: Landmark<*>?
) : MLMapDecoration(type, x, y, rot, name) {
    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            MLMapDecorationType.STREAM_CODEC, MLMapDecoration::getType,
            ByteBufCodecs.BYTE, MLMapDecoration::getX,
            ByteBufCodecs.BYTE, MLMapDecoration::getY,
            ByteBufCodecs.BYTE, MLMapDecoration::getRot,
            ComponentSerialization.OPTIONAL_STREAM_CODEC, { Optional.ofNullable(it.getDisplayName()) },
            StreamCodecs.nullable(
                StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, { it?.type()?.id() },
                    BlockPos.STREAM_CODEC, { it?.pos() },
                    { id, pos ->
                        WorldSummary.of(Minecraft.getInstance().level).landmarks()!!.get(Landmarks.getType(id), pos)
                    })
            ), SurveyorLandmarkDecoration::mark,
            ::SurveyorLandmarkDecoration
        )
    }
}

class SurveyorLandmarkDecorationRenderer(texture: ResourceLocation) :
    MapDecorationRenderer<SurveyorLandmarkDecoration>(texture) {
    var currentDecoration: SurveyorLandmarkDecoration? = null

    override fun rendersOnFrame(decoration: SurveyorLandmarkDecoration) = false

    override fun getColor(decoration: SurveyorLandmarkDecoration) =
        decoration.mark?.color()?.textureDiffuseColor ?: -1

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
        val mark = currentDecoration!!.mark
        val defaultRenderer by lazy {
            MapDecorationClientManager
                .getRenderer<MLMapDecoration>(MapDataRegistry.GENERIC_STRUCTURE_MARKER.holderUnsafe)
        }
        val decorationRegistry = MapDataRegistry.getMapDecorationRegistry(Utils.hackyGetRegistryAccess())
        val renderer = when (mark) {
            is SimplePointLandmark -> {
                if (SurveyorAtlases.Compats.MAP_ATLASES) {
                    MapDecorationClientManager.getRenderer(
                        decorationRegistry.getHolder(MoonlightCompatAccessor.getPinTypeId()).orElseThrow()
                    )
                } else defaultRenderer
            }

            is PlayerDeathLandmark -> {
                if (SurveyorAtlases.Compats.SUPPLEMENTARIES) {
                    MapDecorationClientManager.getRenderer(
                        decorationRegistry.getHolder(Supplementaries.res("death_marker")).orElseThrow()
                    )
                } else defaultRenderer
            }

            is NetherPortalLandmark -> {
                if (SurveyorAtlases.Compats.SUPPLEMENTARIES) {
                    MapDecorationClientManager.getRenderer(
                        decorationRegistry.getHolder(Supplementaries.res("nether_portal")).orElseThrow()
                    )
                } else defaultRenderer
            }

            is SimplePointOfInterestLandmark -> {
                if (SurveyorAtlases.Compats.SUPPLEMENTARIES) {
                    when (mark.poiType()) {
                        PoiTypes.HOME -> MapDecorationClientManager.getRenderer(
                            decorationRegistry.getHolder(ModMapMarkers.BED_FACTORY_ID).orElseThrow()
                        )

                        PoiTypes.MEETING -> MapDecorationClientManager.getRenderer(
                            decorationRegistry.getHolder(Supplementaries.res("bell")).orElseThrow()
                        )

                        PoiTypes.LODESTONE -> MapDecorationClientManager.getRenderer(
                            decorationRegistry.getHolder(Supplementaries.res("lodestone")).orElseThrow()
                        )

                        else -> defaultRenderer
                    }
                } else defaultRenderer
            }

            else -> {
                if (SurveyorAtlases.Compats.SURCEYSTONES) {
                    when (mark) {
                        is BlaystoneLandmark, is FwaystoneLandmark -> {
                            if (SurveyorAtlases.Compats.SUPPLEMENTARIES) {
                                MapDecorationClientManager.getRenderer(
                                    decorationRegistry.getHolder(ModMapMarkers.WAYSTONE_FACTORY_ID).orElseThrow()
                                )
                            } else defaultRenderer
                        }

                        else -> defaultRenderer
                    }
                } else defaultRenderer
            }
        }
        renderer.renderDecorationSprite(matrixStack, buffer, vertexBuilder, light, index, color, alpha, outline)
        currentDecoration = null
    }
}