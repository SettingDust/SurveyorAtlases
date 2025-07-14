package settingdust.surveyor_atlases

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import folk.sisby.surveyor.WorldSummary
import folk.sisby.surveyor.landmark.Landmark
import folk.sisby.surveyor.landmark.Landmarks
import folk.sisby.surveyor.landmark.NetherPortalLandmark
import folk.sisby.surveyor.landmark.PlayerDeathLandmark
import folk.sisby.surveyor.landmark.SimplePointLandmark
import folk.sisby.surveyor.landmark.SimplePointOfInterestLandmark
import folk.sisby.surveystones.BlaystoneLandmark
import folk.sisby.surveystones.FwaystoneLandmark
import net.mehvahdjukaar.moonlight.api.map.CustomMapDecoration
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.DecorationRenderer
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.markers.MapBlockMarker
import net.mehvahdjukaar.moonlight.api.map.type.MapDecorationType
import net.mehvahdjukaar.supplementaries.Supplementaries
import net.mehvahdjukaar.supplementaries.common.misc.map_markers.ModMapMarkers
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.FastColor
import net.minecraft.world.entity.ai.village.poi.PoiTypes
import net.minecraft.world.level.saveddata.maps.MapItemSavedData
import settingdust.surveyor_atlases.mixin.map_atlases.MoonlightCompatAccessor


class SurveyorLandmarkMarker(
    val mark: Landmark<*>? = null
) :
    MapBlockMarker<SurveyorLandmarkDecoration>(SurveyorAtlases.MapDecorationTypes.SURVEYOR_LANDMARK) {
    constructor(type: MapDecorationType<*, *>) : this()

    init {
        setPersistent(false)
        pos = mark?.pos() ?: BlockPos.ZERO
        name = mark?.name()
    }

    override fun doCreateDecoration(mapX: Byte, mapY: Byte, rot: Byte) =
        SurveyorLandmarkDecoration(mapX, mapY, mark)
}

class SurveyorLandmarkDecoration : CustomMapDecoration {
    val mark: Landmark<*>?

    constructor(
        x: Byte,
        y: Byte,
        mark: Landmark<*>?
    ) : super(SurveyorAtlases.MapDecorationTypes.SURVEYOR_LANDMARK, x, y, 0, mark?.name()) {
        this.mark = mark
    }

    constructor(type: MapDecorationType<*, *>, buf: FriendlyByteBuf) : super(type, buf) {
        this.mark = if (buf.readBoolean()) {
            WorldSummary.of(Minecraft.getInstance().level).landmarks()!!.get(
                Landmarks.getType(buf.readResourceLocation()),
                buf.readBlockPos()
            )
        } else null
    }

    override fun saveToBuffer(buffer: FriendlyByteBuf) {
        super.saveToBuffer(buffer)
        mark?.let {
            buffer.writeBoolean(true)
            buffer.writeResourceLocation(it.type().id())
            buffer.writeBlockPos(it.pos())
        } ?: buffer.writeBoolean(false)
    }
}

class SurveyorLandmarkDecorationRenderer(texture: ResourceLocation?) :
    DecorationRenderer<SurveyorLandmarkDecoration>(texture) {
    override fun rendersOnFrame(decoration: SurveyorLandmarkDecoration) = false

    override fun getColor(decoration: SurveyorLandmarkDecoration) =
        decoration.mark?.color()?.textureDiffuseColors?.let {
            FastColor.ABGR32.color(255, (it[0] * 255).toInt(), (it[1] * 255).toInt(), (it[2] * 255).toInt())
        } ?: -1

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
        val mark = currentDecoration!!.mark
        val defaultRenderer by lazy {
            MapDecorationClientManager.getRenderer(MapDataRegistry.getDefaultType() as MapDecorationType<CustomMapDecoration, *>)
        }
        val renderer = when (mark) {
            is SimplePointLandmark -> {
                if (SurveyorAtlases.Compats.MAP_ATLASES) {
                    MapDecorationClientManager.getRenderer(MapDataRegistry.get(MoonlightCompatAccessor.getPinTypeId()) as MapDecorationType<CustomMapDecoration, *>)
                } else defaultRenderer
            }

            is PlayerDeathLandmark -> {
                if (SurveyorAtlases.Compats.SUPPLEMENTARIES) {
                    MapDecorationClientManager.getRenderer(MapDataRegistry.get(Supplementaries.res("death_marker")) as MapDecorationType<CustomMapDecoration, *>)
                } else defaultRenderer
            }

            is NetherPortalLandmark -> {
                if (SurveyorAtlases.Compats.SUPPLEMENTARIES) {
                    MapDecorationClientManager.getRenderer(ModMapMarkers.NETHER_PORTAL_DECORATION_TYPE)
                } else defaultRenderer
            }

            is SimplePointOfInterestLandmark -> {
                if (SurveyorAtlases.Compats.SUPPLEMENTARIES) {
                    when (mark.poiType()) {
                        PoiTypes.HOME -> MapDecorationClientManager.getRenderer(ModMapMarkers.BED_DECORATION_TYPE)
                        PoiTypes.MEETING -> MapDecorationClientManager.getRenderer(
                            MapDataRegistry.get(
                                Supplementaries.res(
                                    "bell"
                                )
                            ) as MapDecorationType<CustomMapDecoration, *>
                        )

                        PoiTypes.LODESTONE -> MapDecorationClientManager.getRenderer(
                            MapDataRegistry.get(
                                Supplementaries.res(
                                    "lodestone"
                                )
                            ) as MapDecorationType<CustomMapDecoration, *>
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
                                MapDecorationClientManager.getRenderer(ModMapMarkers.WAYSTONE_DECORATION_TYPE)
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