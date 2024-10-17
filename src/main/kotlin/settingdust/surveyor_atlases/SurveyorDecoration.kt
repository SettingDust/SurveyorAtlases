package settingdust.surveyor_atlases

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import folk.sisby.surveyor.landmark.Landmark
import net.mehvahdjukaar.moonlight.api.map.CustomMapDecoration
import net.mehvahdjukaar.moonlight.api.map.client.DecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.markers.MapBlockMarker
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import settingdust.surveyor_atlases.mixin.DecorationRendererAccessor

class SurveyorDecoration : CustomMapDecoration {
    var texture: ResourceLocation? = null
        private set

    constructor(
        x: Byte,
        y: Byte,
        name: Component? = null,
        texture: ResourceLocation? = null
    ) : super(SurveyorAtlases.MapDecorationTypes.SURVEYOR, x, y, 0, name) {
        this.texture = texture
    }

    constructor(type: MapDecorationType<*, *>, buf: FriendlyByteBuf) : super(type, buf)
}

class SurveyorMarker(
    pos: BlockPos = BlockPos.ZERO,
    name: Component? = null,
    val texture: ResourceLocation? = null
) :
    MapBlockMarker<SurveyorDecoration>(SurveyorAtlases.MapDecorationTypes.SURVEYOR) {
    constructor(type: MapDecorationType<*, *>) : this()

    constructor(mark: Landmark<*>) : this(mark.pos(), mark.name(), mark.texture())

    init {
        setPersistent(false)
        setPos(pos)
        setName(name)
    }

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte) = SurveyorDecoration(mapX, mapY, name, texture)
}

class SurveyorDecorationRenderer(texture: ResourceLocation?) : DecorationRenderer<SurveyorDecoration>(texture) {
    private var originalTexture: ResourceLocation? = texture

    override fun render(
        decoration: SurveyorDecoration?,
        matrixStack: PoseStack?,
        vertexBuilder: VertexConsumer?,
        buffer: MultiBufferSource?,
        mapData: MapItemSavedData?,
        isOnFrame: Boolean,
        light: Int,
        index: Int,
        rendersText: Boolean
    ): Boolean {
        @Suppress("CAST_NEVER_SUCCEEDS")
        decoration?.texture?.let {
            (this as DecorationRendererAccessor).setTextureId(it)
        } ?: (this as DecorationRendererAccessor).setTextureId(originalTexture)
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
}