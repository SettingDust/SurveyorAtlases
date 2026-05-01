package settingdust.surveyor_atlases.v20_1.marker

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import folk.sisby.surveyor.landmark.Landmark
import folk.sisby.surveyor.landmark.component.LandmarkComponentTypes
import net.mehvahdjukaar.moonlight.api.map.CustomMapDecoration
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.DecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.markers.MapBlockMarker
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType
import net.mehvahdjukaar.supplementaries.Supplementaries
import net.mehvahdjukaar.supplementaries.common.misc.map_markers.ModMapMarkers
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.nbt.NbtOps
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import settingdust.surveyor_atlases.Markers
import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.mixin.map_atlases.MoonlightCompatAccessor
import settingdust.surveyor_atlases.util.Identifier


class SurveyorLandmarkMarker(
    val mark: Landmark? = null
) :
    MapBlockMarker<SurveyorLandmarkDecoration>(SurveyorAtlasesMarkers.SURVEYOR_LANDMARK) {
    constructor(type: MapDecorationType<*, *>) : this()

    init {
        setPersistent(false)
        pos = mark?.get(LandmarkComponentTypes.POS) ?: BlockPos.ZERO
        name = mark?.get(LandmarkComponentTypes.NAME)
    }

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte) =
        SurveyorLandmarkDecoration(mapX, mapY, mark)
}

class SurveyorLandmarkDecoration : CustomMapDecoration {
    val mark: Landmark?

    constructor(
        x: Byte,
        y: Byte,
        mark: Landmark?
    ) : super(SurveyorAtlasesMarkers.SURVEYOR_LANDMARK, x, y, 0, mark?.components()?.get(LandmarkComponentTypes.NAME)) {
        this.mark = mark
    }

    constructor(type: MapDecorationType<*, *>, buf: FriendlyByteBuf) : super(type, buf) {
        this.mark = if (buf.readBoolean()) {
            buf.readWithCodec(NbtOps.INSTANCE, Landmark.createCodec(buf.readUUID(), buf.readResourceLocation()))
        } else null
    }

    override fun saveToBuffer(buffer: FriendlyByteBuf) {
        super.saveToBuffer(buffer)
        mark?.let {
            buffer.writeBoolean(true)
            buffer.writeUUID(it.owner())
            buffer.writeResourceLocation(it.id())
            buffer.writeWithCodec(NbtOps.INSTANCE, Landmark.createCodec(it.owner(), it.id()), it)
        } ?: buffer.writeBoolean(false)
    }
}

class SurveyorLandmarkDecorationRenderer(texture: ResourceLocation?) :
    DecorationRenderer<SurveyorLandmarkDecoration>(texture) {
    override fun rendersOnFrame(decoration: SurveyorLandmarkDecoration) = false

    override fun getColor(decoration: SurveyorLandmarkDecoration) =
        decoration.mark?.get(LandmarkComponentTypes.COLOR) ?: -1

    var currentDecoration: SurveyorLandmarkDecoration? = null

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
            MapDecorationClientManager.getRenderer(MapDataRegistry.getDefaultType() as MapDecorationType<CustomMapDecoration, MapBlockMarker<CustomMapDecoration>>)
        }

        fun Identifier.match() = mark.id().namespace == namespace && mark.id().path.startsWith(path)
        val renderer = when {
            SurveyorAtlases.Compats.MAP_ATLASES && Markers.BLOCK.match() ->
                MapDecorationClientManager.getRenderer(MapDataRegistry.get(MoonlightCompatAccessor.getPinTypeId()) as MapDecorationType<CustomMapDecoration, MapBlockMarker<CustomMapDecoration>>)

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.DEATH.match() ->
                MapDecorationClientManager.getRenderer(MapDataRegistry.get(Supplementaries.res("death_marker")) as MapDecorationType<CustomMapDecoration, MapBlockMarker<CustomMapDecoration>>)

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_HOME.match() ->
                MapDecorationClientManager.getRenderer(ModMapMarkers.BED_DECORATION_TYPE)

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_MEETING.match() ->
                MapDecorationClientManager.getRenderer(
                    MapDataRegistry.get(Supplementaries.res("bell"))
                            as MapDecorationType<CustomMapDecoration, MapBlockMarker<CustomMapDecoration>>
                )

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_LODESTONE.match() ->
                MapDecorationClientManager.getRenderer(
                    MapDataRegistry.get(Supplementaries.res("lodestone"))
                            as MapDecorationType<CustomMapDecoration, MapBlockMarker<CustomMapDecoration>>
                )

            SurveyorAtlases.Compats.SUPPLEMENTARIES && Markers.POI_NETHER_PORTAL.match() ->
                MapDecorationClientManager.getRenderer(ModMapMarkers.NETHER_PORTAL_DECORATION_TYPE)

            SurveyorAtlases.Compats.SURCEYSTONES && (Markers.SURCEYSTONES_BLAYSTONE.match() || Markers.SURCEYSTONES_FWATSTONES.match()) -> {
                MapDecorationClientManager.getRenderer(ModMapMarkers.WAYSTONE_DECORATION_TYPE)
            }

            else -> defaultRenderer
        }
        renderer.renderDecorationSprite(matrixStack, buffer, vertexBuilder, light, index, color, alpha, outline)
        currentDecoration = null
    }
}