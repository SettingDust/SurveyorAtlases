package settingdust.surveyor_atlases.v1_21.marker

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import java.util.*

class SurveyorStructureMarker(
    val structure: ResourceKey<Structure>? = null,
    type: Holder<MLMapDecorationType<*, *>>,
    pos: BlockPos = BlockPos.ZERO,
    rotation: Float = 0F,
    name: Optional<Component> = Optional.empty(),
    shouldRefresh: Optional<Boolean> = Optional.empty(),
    shouldSave: Optional<Boolean> = Optional.of(false),
    preventsExtending: Boolean = false,
) : MLMapMarker<SurveyorStructureDecoration>(
    type,
    pos,
    rotation,
    name,
    shouldRefresh,
    shouldSave,
    preventsExtending
) {
    companion object {
        val MAP_CODEC: MapCodec<SurveyorStructureMarker> =
            RecordCodecBuilder.mapCodec<SurveyorStructureMarker> { instance ->
                instance.group(ResourceKey.codec(Registries.STRUCTURE).fieldOf("structure").forGetter { it.structure })
                    .and(baseCodecGroup(instance))
                    .apply(instance, ::SurveyorStructureMarker)
            }
    }

    constructor(structure: ResourceKey<Structure>) : this(
        structure,
        SurveyorAtlasesMarkers.SURVEYOR_STRUCTURE.holderUnsafe
    )

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte) =
        SurveyorStructureDecoration(type, mapX, mapY, rot, name, structure)
}

class SurveyorStructureDecoration(
    type: Holder<MLMapDecorationType<*, *>>,
    x: Byte,
    y: Byte,
    rot: Byte,
    name: Optional<Component>,
    val structure: ResourceKey<Structure>? = null
) : MLMapDecoration(type, x, y, rot, name) {
    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            MLMapDecorationType.STREAM_CODEC, MLMapDecoration::getType,
            ByteBufCodecs.BYTE, MLMapDecoration::getX,
            ByteBufCodecs.BYTE, MLMapDecoration::getY,
            ByteBufCodecs.BYTE, MLMapDecoration::getRot,
            ComponentSerialization.OPTIONAL_STREAM_CODEC, { Optional.ofNullable(it.getDisplayName()) },
            ResourceKey.streamCodec(Registries.STRUCTURE), { it.structure },
            ::SurveyorStructureDecoration
        )
    }
}

class SurveyorStructureDecorationRenderer(texture: ResourceLocation?) :
    MapDecorationRenderer<SurveyorStructureDecoration>(texture) {
    var currentDecoration: SurveyorStructureDecoration? = null

    override fun rendersOnFrame(decoration: SurveyorStructureDecoration) = false

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
        MapDecorationClientManager.getRenderer<MLMapDecoration>(currentDecoration!!.type)
            .renderDecorationSprite(matrixStack, buffer, vertexBuilder, light, index, color, alpha, outline)
        currentDecoration = null
    }
}