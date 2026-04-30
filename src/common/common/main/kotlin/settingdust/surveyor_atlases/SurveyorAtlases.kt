package settingdust.surveyor_atlases

import org.apache.logging.log4j.LogManager
import settingdust.surveyor_atlases.util.Identifier
import settingdust.surveyor_atlases.util.LoaderAdapter
import settingdust.surveyor_atlases.util.MoonlightAdapter

object SurveyorAtlases {
    const val ID = "surveyor_atlases"

    val LOGGER = LogManager.getLogger()

    val SURVEYOR_LANDMARK_ID = id("surveyor_landmark")
    val SURVEYOR_STRUCTURE_ID = id("surveyor_structure")

    init {
        requireNotNull(MoonlightAdapter)
    }

    fun id(path: String) = Identifier(ID, path)

    object Compats {
        val MAP_ATLASES by lazy { LoaderAdapter.isModLoaded("map_atlases") }
        val SUPPLEMENTARIES by lazy { LoaderAdapter.isModLoaded("supplementaries") }
        val SURCEYSTONES by lazy { LoaderAdapter.isModLoaded("surveystones") }
    }
}