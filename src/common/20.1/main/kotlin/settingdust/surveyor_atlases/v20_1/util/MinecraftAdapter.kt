package settingdust.surveyor_atlases.v20_1.util

import settingdust.surveyor_atlases.util.MinecraftAdapter
import settingdust.surveyor_atlases.util.MinecraftVersion

class MinecraftAdapter : MinecraftAdapter {
    init {
        MinecraftVersion.V1201.requireCurrent()
    }
}