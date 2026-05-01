package settingdust.surveyor_atlases.v20_1.marker

import folk.sisby.surveyor.SurveyorExploration
import folk.sisby.surveyor.WorldSummary
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.type.CustomDecorationType
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.v20_1.util.toNative

object SurveyorAtlasesMarkers {
    val SURVEYOR_LANDMARK = CustomDecorationType.simple(::SurveyorLandmarkMarker, ::SurveyorLandmarkDecoration)!!
    val SURVEYOR_STRUCTURE = CustomDecorationType.simple(::SurveyorStructureMarker, ::SurveyorStructureDecoration)!!

    val SURVEYOR_LANDMARK_ID = SurveyorAtlases.SURVEYOR_LANDMARK_ID.toNative()
    val SURVEYOR_STRUCTURE_ID = SurveyorAtlases.SURVEYOR_STRUCTURE_ID.toNative()

    init {
        MapDataRegistry.registerCustomType(SURVEYOR_STRUCTURE_ID) { SURVEYOR_STRUCTURE }
        MapDataRegistry.registerCustomType(SURVEYOR_LANDMARK_ID) { SURVEYOR_LANDMARK }

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
            if (data.dimension != level.dimension()) return@addDynamicServerMarkersEvent setOf()
            val worldSummary = WorldSummary.of(level)
            val exploration = SurveyorExploration.of(player as ServerPlayer)

            val registryAccess = level.registryAccess()
            val structureRegistry = registryAccess.lookupOrThrow(Registries.STRUCTURE)
            buildSet {
                worldSummary.landmarks()?.asMap(exploration)?.values()?.forEach { add(SurveyorLandmarkMarker(it)) }

                worldSummary.structures()?.asMap(exploration)?.forEach { (key, structures) ->
                    addAll(structures.values.map { summary ->
                        SurveyorStructureMarker(
                            summary.boundingBox.center.atY(summary.boundingBox.maxY()),
                            structureRegistry.getOrThrow(key)
                        )
                    })
                }
            }
        }
    }
}