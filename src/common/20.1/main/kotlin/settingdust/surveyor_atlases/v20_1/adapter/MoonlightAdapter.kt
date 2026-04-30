package settingdust.surveyor_atlases.v20_1.adapter

import settingdust.surveyor_atlases.util.MoonlightAdapter
import settingdust.surveyor_atlases.v20_1.marker.SurveyorAtlasesMarkers

class MoonlightAdapter : MoonlightAdapter {
    init {
        requireNotNull(SurveyorAtlasesMarkers)
    }
}