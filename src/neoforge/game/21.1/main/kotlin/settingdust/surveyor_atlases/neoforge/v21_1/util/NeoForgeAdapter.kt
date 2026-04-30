package settingdust.surveyor_atlases.neoforge.v21_1.util

import net.neoforged.fml.loading.FMLLoader
import settingdust.surveyor_atlases.neoforge.util.NeoForgeAdapter
import settingdust.surveyor_atlases.util.MinecraftVersion

class NeoForgeAdapter : NeoForgeAdapter {
    init {
        MinecraftVersion.V1211.requireCurrent()
    }

    override val dist = FMLLoader.getDist()!!
}
