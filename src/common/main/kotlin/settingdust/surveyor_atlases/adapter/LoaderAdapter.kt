package settingdust.surveyor_atlases.adapter

import settingdust.surveyor_atlases.ServiceLoaderUtil

interface LoaderAdapter {
    companion object : LoaderAdapter by ServiceLoaderUtil.findService()

    fun isModLoaded(modId: String): Boolean
}