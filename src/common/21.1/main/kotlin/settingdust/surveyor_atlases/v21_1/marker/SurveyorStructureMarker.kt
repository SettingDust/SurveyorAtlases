package settingdust.surveyor_atlases.v21_1.marker

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecoration
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapDecorationType
import net.mehvahdjukaar.moonlight.api.map.decoration.MLMapMarker
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper
import net.mehvahdjukaar.moonlight.api.util.Utils
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
        val MAP_CODEC =
            RecordCodecBuilder.mapCodec<SurveyorStructureMarker> { instance ->
                instance.group(ResourceKey.codec(Registries.STRUCTURE).fieldOf("structure").forGetter { it.structure })
                    .and(baseCodecGroup(instance))
                    .apply(instance, ::SurveyorStructureMarker)
            }
    }

    constructor(structure: ResourceKey<Structure>) : this(
        structure,
        SurveyorAtlasesMarkers.SURVEYOR_STRUCTURE.getHolder(Utils.hackyGetRegistryAccess())
    )

    private var currentStructureDecorationType: Holder<MLMapDecorationType<*, *>>? = null

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte): SurveyorStructureDecoration {
        return SurveyorStructureDecoration(type, mapX, mapY, rot, name, currentStructureDecorationType!!)
    }

    override fun createDecorationFromMarker(data: MapItemSavedData): SurveyorStructureDecoration? {
        val level = PlatHelper.getCurrentServer()?.getLevel(data.dimension) ?: return null
        currentStructureDecorationType = MapDataRegistry.getDecorationFoStructure(
            level,
            level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(structure!!)
        )
        val result = super.createDecorationFromMarker(data)
        currentStructureDecorationType = null
        return result
    }
}

class SurveyorStructureDecoration(
    type: Holder<MLMapDecorationType<*, *>>,
    x: Byte,
    y: Byte,
    rot: Byte,
    name: Optional<Component>,
    val decorationType: Holder<MLMapDecorationType<*, *>>
) : MLMapDecoration(type, x, y, rot, name) {
    companion object {
        val STREAM_CODEC = StreamCodec.composite(
            MLMapDecorationType.STREAM_CODEC, MLMapDecoration::getType,
            ByteBufCodecs.BYTE, MLMapDecoration::getX,
            ByteBufCodecs.BYTE, MLMapDecoration::getY,
            ByteBufCodecs.BYTE, MLMapDecoration::getRot,
            ComponentSerialization.OPTIONAL_STREAM_CODEC, { Optional.ofNullable(it.getDisplayName()) },
            MLMapDecorationType.STREAM_CODEC, { it.decorationType },
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
        MapDecorationClientManager.getRenderer<MLMapDecoration>(currentDecoration!!.decorationType)
            .renderDecorationSprite(matrixStack, buffer, vertexBuilder, light, index, color, alpha, outline)
        currentDecoration = null
    }
}