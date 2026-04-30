package settingdust.surveyor_atlases.fabric

import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.util.Entrypoint

object SurveyorAtlasesFabric {
    init {
        requireNotNull(SurveyorAtlases)
        Entrypoint.construct()
    }

    fun init() {
        Entrypoint.init()
    }

    fun clientInit() {
        Entrypoint.clientInit()
    }
}
