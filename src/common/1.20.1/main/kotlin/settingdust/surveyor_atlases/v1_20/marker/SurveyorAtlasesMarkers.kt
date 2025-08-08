package settingdust.surveyor_atlases.v1_20.marker

import folk.sisby.surveyor.SurveyorExploration
import folk.sisby.surveyor.WorldSummary
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.type.CustomDecorationType
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import settingdust.surveyor_atlases.SurveyorAtlases

object SurveyorAtlasesMarkers {
    val SURVEYOR_LANDMARK = CustomDecorationType.simple(::SurveyorLandmarkMarker, ::SurveyorLandmarkDecoration)!!
    val SURVEYOR_STRUCTURE = CustomDecorationType.simple(::SurveyorStructureMarker, ::SurveyorStructureDecoration)!!


    init {
        MapDataRegistry.registerCustomType(SurveyorAtlases.SURVEYOR_STRUCTURE_ID) { SURVEYOR_STRUCTURE }
        MapDataRegistry.registerCustomType(SurveyorAtlases.SURVEYOR_LANDMARK_ID) { SURVEYOR_LANDMARK }

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

            val registryAccess = level.registryAccess()
            val structureRegistry = registryAccess.lookupOrThrow(Registries.STRUCTURE)
            buildSet {
                worldSummary.landmarks()?.asMap(exploration)?.forEach { (_, marks) ->
                    addAll(marks.values.map { SurveyorLandmarkMarker(it) })
                }

                worldSummary.structures()?.asMap(exploration)?.forEach { (key, structures) ->
                    addAll(structures.entries.map {
                        SurveyorStructureMarker(
                            it.key,
                            it.value,
                            structureRegistry.getOrThrow(key)
                        )
                    })
                }
            }
        }
    }
}