package settingdust.surveyor_atlases.util

interface MoonlightAdapter {
    companion object {
        init {
            ServiceLoaderUtil.loadServices<MoonlightAdapter>()
        }
    }
}
