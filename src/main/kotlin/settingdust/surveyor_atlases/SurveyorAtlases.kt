package settingdust.surveyor_atlases

import folk.sisby.surveyor.SurveyorExploration
import folk.sisby.surveyor.WorldSummary
import net.fabricmc.loader.api.FabricLoader
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.type.CustomDecorationType
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import org.apache.logging.log4j.LogManager

object SurveyorAtlases {
    const val ID = "surveyor_atlases"
    val LOGGER = LogManager.getLogger()

    val SURVEYOR_LANDMARK_ID = identifier("surveyor_landmark")
    val SURVEYOR_STRUCTURE_ID = identifier("surveyor_structure")

    fun identifier(path: String) = ResourceLocation(ID, path)

    object MapDecorationTypes {
        val SURVEYOR_LANDMARK = CustomDecorationType.simple(::SurveyorLandmarkMarker, ::SurveyorLandmarkDecoration)
        val SURVEYOR_STRUCTURE = CustomDecorationType.simple(::SurveyorStructureMarker, ::SurveyorStructureDecoration)
    }

    object Compats {
        val MAP_ATLASES by lazy {
            FabricLoader.getInstance().isModLoaded("map_atlases")
        }
        val SUPPLEMENTARIES by lazy {
            FabricLoader.getInstance().isModLoaded("supplementaries")
        }
        val SURCEYSTONES by lazy {
            FabricLoader.getInstance().isModLoaded("surveystones")
        }
    }
}

fun init() {
    MapDataRegistry.registerCustomType(SurveyorAtlases.SURVEYOR_STRUCTURE_ID) { SurveyorAtlases.MapDecorationTypes.SURVEYOR_STRUCTURE }
    MapDataRegistry.registerCustomType(SurveyorAtlases.SURVEYOR_LANDMARK_ID) { SurveyorAtlases.MapDecorationTypes.SURVEYOR_LANDMARK }

    WorldSummary.enableLandmarks()
    WorldSummary.enableStructures()

    MapDataRegistry.addDynamicServerMarkersEvent { player, _, data ->
        val level = player.level()
        if (data.dimension != level.dimension()) return@addDynamicServerMarkersEvent setOf()
        val worldSummary = WorldSummary.of(level)
        val exploration = SurveyorExploration.of(player as ServerPlayer)

        val registryAccess = level.registryAccess()
        val structureRegistry = registryAccess.lookupOrThrow(Registries.STRUCTURE)
        buildSet {
            worldSummary.landmarks?.asMap(exploration)?.forEach { (_, marks) ->
                addAll(marks.values.map { SurveyorLandmarkMarker(it) })
            }

            worldSummary.structures?.asMap(exploration)?.forEach { (key, structures) ->
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

fun clientInit() {
    MapDecorationClientManager.registerCustomRenderer(
        SurveyorAtlases.SURVEYOR_STRUCTURE_ID,
        ::SurveyorStructureDecorationRenderer
    )

    MapDecorationClientManager.registerCustomRenderer(
        SurveyorAtlases.SURVEYOR_LANDMARK_ID,
        ::SurveyorLandmarkDecorationRenderer
    )
}