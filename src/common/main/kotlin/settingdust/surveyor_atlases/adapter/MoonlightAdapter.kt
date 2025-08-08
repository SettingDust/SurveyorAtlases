package settingdust.surveyor_atlases.adapter

import settingdust.surveyor_atlases.ServiceLoaderUtil

interface MoonlightAdapter {
    companion object {
        init {
            ServiceLoaderUtil.loadServices<MoonlightAdapter>()
        }
    }
}