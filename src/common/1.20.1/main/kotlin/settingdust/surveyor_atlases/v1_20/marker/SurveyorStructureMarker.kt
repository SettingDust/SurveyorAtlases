package settingdust.surveyor_atlases.v1_20.marker

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import folk.sisby.surveyor.structure.StructureStartSummary
import net.mehvahdjukaar.moonlight.api.map.CustomMapDecoration
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.DecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.markers.MapBlockMarker
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType
import net.mehvahdjukaar.moonlight.api.util.Utils
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.saveddata.maps.MapItemSavedData

class SurveyorStructureMarker(
    val chunkPos: ChunkPos = ChunkPos.ZERO,
    structureSummary: StructureStartSummary? = null,
    val structure: Holder<Structure>? = null
) :
    MapBlockMarker<SurveyorStructureDecoration>(SurveyorAtlasesMarkers.SURVEYOR_STRUCTURE) {
    constructor(type: MapDecorationType<*, *>) : this()

    init {
        setPersistent(false)
        pos = structureSummary?.boundingBox?.maxY()?.let { chunkPos.getMiddleBlockPosition(it) } ?: BlockPos.ZERO
    }

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte) =
        SurveyorStructureDecoration(mapX, mapY, MapDataRegistry.getAssociatedType(structure))
}

class SurveyorStructureDecoration : CustomMapDecoration {
    val decorationType: MapDecorationType<*, *>

    constructor(
        x: Byte,
        y: Byte,
        decorationType: MapDecorationType<*, *>
    ) : super(SurveyorAtlasesMarkers.SURVEYOR_STRUCTURE, x, y, 0, null) {
        this.decorationType = decorationType
    }

    constructor(type: MapDecorationType<*, *>, buf: FriendlyByteBuf) : super(type, buf) {
        this.decorationType = MapDataRegistry.get(buf.readResourceLocation())
    }

    override fun saveToBuffer(buffer: FriendlyByteBuf) {
        super.saveToBuffer(buffer)
        buffer.writeResourceLocation(Utils.getID(decorationType))
    }
}

class SurveyorStructureDecorationRenderer(texture: ResourceLocation?) :
    DecorationRenderer<SurveyorStructureDecoration>(texture) {
    override fun rendersOnFrame(decoration: SurveyorStructureDecoration) = false

    private var currentDecoration: SurveyorStructureDecoration? = null

    override fun render(
        decoration: SurveyorStructureDecoration,
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
        MapDecorationClientManager.getRenderer(currentDecoration!!.decorationType)
            .renderDecorationSprite(matrixStack, buffer, vertexBuilder, light, index, color, alpha, outline)
        currentDecoration = null
    }
}