package settingdust.surveyor_atlases.v21_1.util

import settingdust.surveyor_atlases.util.MinecraftAdapter
import settingdust.surveyor_atlases.util.MinecraftVersion

class MinecraftAdapter : MinecraftAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }
}