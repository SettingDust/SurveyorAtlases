package settingdust.surveyor_atlases

import folk.sisby.surveyor.WorldSummary
import folk.sisby.surveyor.client.SurveyorClient
import net.mehvahdjukaar.moonlight.api.map.MapDataRegistry
import net.mehvahdjukaar.moonlight.api.map.client.MapDecorationClientManager
import net.mehvahdjukaar.moonlight.api.map.markers.MapBlockMarker
import net.mehvahdjukaar.moonlight.api.map.type.CustomDecorationType
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation

object SurveyorAtlases {
    const val ID = "surveyor_atlases"

    fun identifier(path: String) = ResourceLocation(ID, path)

    object MapDecorationTypes {
        val SURVEYOR = CustomDecorationType.simple(::SurveyorMarker, ::SurveyorDecoration)

        init {
            MapDataRegistry.registerCustomType(identifier("surveyor")) { SURVEYOR }
        }
    }
}

fun clientInit() {
    MapDecorationClientManager.registerCustomRenderer(
        SurveyorAtlases.identifier("surveyor"),
        ::SurveyorDecorationRenderer
    )

    MapDataRegistry.addDynamicClientMarkersEvent { _, data ->
        val level = Minecraft.getInstance().level
        if (data.dimension != level?.dimension()) return@addDynamicClientMarkersEvent setOf()
        buildSet<MapBlockMarker<*>> {
            WorldSummary.of(level).landmarks?.asMap(SurveyorClient.getExploration())?.forEach { (_, marks) ->
                for (mark in marks) {
                    add(SurveyorMarker(mark.value))
                }
            }
        }
    }
}