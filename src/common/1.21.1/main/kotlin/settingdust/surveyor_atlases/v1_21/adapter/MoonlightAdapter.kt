package settingdust.surveyor_atlases.v1_21.adapter

import settingdust.surveyor_atlases.adapter.MoonlightAdapter
import settingdust.surveyor_atlases.v1_21.marker.SurveyorAtlasesMarkers

class MoonlightAdapter : MoonlightAdapter {
    init {
        requireNotNull(SurveyorAtlasesMarkers)
    }
}