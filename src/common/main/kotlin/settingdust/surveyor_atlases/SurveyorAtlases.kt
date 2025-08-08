package settingdust.surveyor_atlases

import folk.sisby.surveyor.WorldSummary
import net.mehvahdjukaar.moonlight.api.platform.PlatHelper
import net.mehvahdjukaar.supplementaries.Supplementaries
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import pepjebs.mapatlases.MapAtlasesMod
import settingdust.surveyor_atlases.adapter.MinecraftAdapter
import settingdust.surveyor_atlases.adapter.MoonlightAdapter

object SurveyorAtlases {
    const val ID = "surveyor_atlases"
    val LOGGER = LogManager.getLogger()

    val SURVEYOR_LANDMARK_ID: ResourceLocation
    val SURVEYOR_STRUCTURE_ID: ResourceLocation

    init {
        ServiceLoaderUtil.defaultLogger = LOGGER

        SURVEYOR_LANDMARK_ID = id("surveyor_landmark")
        SURVEYOR_STRUCTURE_ID = id("surveyor_structure")

        requireNotNull(MoonlightAdapter)
    }

    fun id(path: String) = MinecraftAdapter.id(ID, path)

    object Compats {
        val MAP_ATLASES by lazy {
            PlatHelper.isModLoaded(MapAtlasesMod.MOD_ID)
        }
        val SUPPLEMENTARIES by lazy {
            PlatHelper.isModLoaded(Supplementaries.MOD_ID)
        }
        val SURCEYSTONES by lazy {
            PlatHelper.isModLoaded("surveystones")
        }
    }
}

fun init() {
    WorldSummary.enableLandmarks()
    WorldSummary.enableStructures()
}