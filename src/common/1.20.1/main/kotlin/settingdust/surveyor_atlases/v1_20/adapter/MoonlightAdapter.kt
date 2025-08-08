package settingdust.surveyor_atlases.v1_20.adapter

import app.softwork.serviceloader.ServiceLoader
import settingdust.surveyor_atlases.v1_20.marker.SurveyorAtlasesMarkers

@ServiceLoader(settingdust.surveyor_atlases.adapter.MoonlightAdapter::class)
class MoonlightAdapter : settingdust.surveyor_atlases.adapter.MoonlightAdapter {
    init {
        requireNotNull(SurveyorAtlasesMarkers)
    }
}