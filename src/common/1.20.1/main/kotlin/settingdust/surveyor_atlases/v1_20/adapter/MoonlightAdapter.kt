package settingdust.surveyor_atlases.v1_20.adapter

import settingdust.surveyor_atlases.adapter.MoonlightAdapter
import settingdust.surveyor_atlases.v1_20.marker.SurveyorAtlasesMarkers

class MoonlightAdapter : MoonlightAdapter {
    init {
        requireNotNull(SurveyorAtlasesMarkers)
    }
}