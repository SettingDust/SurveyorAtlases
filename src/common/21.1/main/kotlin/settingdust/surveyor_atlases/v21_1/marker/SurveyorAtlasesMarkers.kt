package settingdust.surveyor_atlases.v21_1.marker

import folk.sisby.surveyor.SurveyorExploration
import folk.sisby.surveyor.WorldSummary
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.decoration.MLSpecialMapDecorationType
import net.mehvahdjukaar.moonlight.api.misc.HolderRef
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper
import net.minecraft.server.level.ServerPlayer
import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.v21_1.util.toNative

object SurveyorAtlasesMarkers {
    val SURVEYOR_LANDMARK_ID = SurveyorAtlases.SURVEYOR_LANDMARK_ID.toNative()
    val SURVEYOR_STRUCTURE_ID = SurveyorAtlases.SURVEYOR_STRUCTURE_ID.toNative()

    val SURVEYOR_LANDMARK =
        HolderRef.of(SURVEYOR_LANDMARK_ID, MapDataRegistry.MAP_DECORATION_REGISTRY_KEY)!!
    val SURVEYOR_STRUCTURE =
        HolderRef.of(SURVEYOR_STRUCTURE_ID, MapDataRegistry.MAP_DECORATION_REGISTRY_KEY)!!

    init {
        MapDataRegistry.registerSpecialMapDecorationTypeFactory(SURVEYOR_LANDMARK_ID) {
            MLSpecialMapDecorationType.standaloneCustomMarker(
                SurveyorLandmarkMarker.MAP_CODEC,
                SurveyorLandmarkDecoration.STREAM_CODEC
            )
        }
        MapDataRegistry.registerSpecialMapDecorationTypeFactory(SURVEYOR_STRUCTURE_ID) {
            MLSpecialMapDecorationType.standaloneCustomMarker(
                SurveyorStructureMarker.MAP_CODEC,
                SurveyorStructureDecoration.STREAM_CODEC
            )
        }

        PlatHelper.getPhysicalSide().ifClient {
            MapDecorationClientManager.registerCustomRenderer(
                SURVEYOR_LANDMARK_ID,
                ::SurveyorLandmarkDecorationRenderer
            )
            MapDecorationClientManager.registerCustomRenderer(
                SURVEYOR_STRUCTURE_ID,
                ::SurveyorStructureDecorationRenderer
            )
        }

        MapDataRegistry.addDynamicServerMarkersEvent { player, _, data ->
            val level = player.level()
            if (data.dimension != level.dimension()) return@addDynamicServerMarkersEvent emptySet()
            val worldSummary = WorldSummary.of(level)
            val exploration = SurveyorExploration.of(player as ServerPlayer)

            buildSet {
                worldSummary.landmarks()?.asMap(exploration)?.values()?.forEach { add(SurveyorLandmarkMarker(it)) }

                worldSummary.structures()?.asMap(exploration)?.forEach { (key, structures) ->
                    addAll(structures.values.map { summary ->
                        SurveyorStructureMarker(
                            key,
                            SURVEYOR_STRUCTURE.getHolder(player),
                            summary.boundingBox.center.atY(summary.boundingBox.maxY())
                        )
                    })
                }
            }
        }
    }
}
