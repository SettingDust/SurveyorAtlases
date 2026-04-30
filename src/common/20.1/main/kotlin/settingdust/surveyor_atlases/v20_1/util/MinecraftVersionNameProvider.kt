package settingdust.surveyor_atlases.v20_1.util

import net.minecraft.SharedConstants
import settingdust.surveyor_atlases.util.MinecraftVersionNameProvider

class MinecraftVersionNameProvider : MinecraftVersionNameProvider {
    override fun currentVersionName(): String = SharedConstants.getCurrentVersion().name
}
