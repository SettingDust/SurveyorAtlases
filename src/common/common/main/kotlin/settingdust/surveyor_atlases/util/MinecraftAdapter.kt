package settingdust.surveyor_atlases.util

interface MinecraftAdapter {
    companion object : MinecraftAdapter by ServiceLoaderUtil.findService()
}