package settingdust.surveyor_atlases.v1_21.marker

import folk.sisby.surveyor.SurveyorExploration
import folk.sisby.surveyor.WorldSummary
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.decoration.MLSpecialMapDecorationType
import net.mehvahdjukaar.moonlight.api.misc.HolderReference
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper
import net.minecraft.server.level.ServerPlayer
import settingdust.surveyor_atlases.SurveyorAtlases

object SurveyorAtlasesMarkers {
    val SURVEYOR_LANDMARK =
        HolderReference.of(SurveyorAtlases.SURVEYOR_LANDMARK_ID, MapDataRegistry.MAP_DECORATION_REGISTRY_KEY)!!
    val SURVEYOR_STRUCTURE =
        HolderReference.of(SurveyorAtlases.SURVEYOR_STRUCTURE_ID, MapDataRegistry.MAP_DECORATION_REGISTRY_KEY)!!

    init {
        MapDataRegistry.registerSpecialMapDecorationTypeFactory(SurveyorAtlases.SURVEYOR_LANDMARK_ID) {
            MLSpecialMapDecorationType.standaloneCustomMarker<SurveyorLandmarkDecoration, SurveyorLandmarkMarker>(
                SurveyorLandmarkMarker.MAP_CODEC,
                SurveyorLandmarkDecoration.STREAM_CODEC
            )
        }
        MapDataRegistry.registerSpecialMapDecorationTypeFactory(SurveyorAtlases.SURVEYOR_STRUCTURE_ID) {
            MLSpecialMapDecorationType.standaloneCustomMarker<SurveyorStructureDecoration, SurveyorStructureMarker>(
                SurveyorStructureMarker.MAP_CODEC,
                SurveyorStructureDecoration.STREAM_CODEC
            )
        }

        PlatHelper.getPhysicalSide().ifClient {
            MapDecorationClientManager.registerCustomRenderer(
                SurveyorAtlases.SURVEYOR_LANDMARK_ID,
                ::SurveyorLandmarkDecorationRenderer
            )
            MapDecorationClientManager.registerCustomRenderer(
                SurveyorAtlases.SURVEYOR_STRUCTURE_ID,
                ::SurveyorStructureDecorationRenderer
            )
        }

        MapDataRegistry.addDynamicServerMarkersEvent { player, _, data ->
            val level = player.level()
            if (data.dimension != level.dimension()) return@addDynamicServerMarkersEvent setOf()
            val worldSummary = WorldSummary.of(level)
            val exploration = SurveyorExploration.of(player as ServerPlayer)

            buildSet {
                worldSummary.landmarks()?.asMap(exploration)?.forEach { (_, marks) ->
                    addAll(marks.values.map { SurveyorLandmarkMarker(it, SURVEYOR_LANDMARK.getHolder(player)) })
                }

                worldSummary.structures()?.asMap(exploration)?.forEach { (key, structures) ->
                    addAll(structures.entries.map {
                        SurveyorStructureMarker(key, SURVEYOR_STRUCTURE.getHolder(player))
                    })
                }
            }
        }
    }
}