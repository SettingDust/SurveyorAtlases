package settingdust.surveyor_atlases.neoforge.util

import net.neoforged.fml.loading.LoadingModList
import settingdust.surveyor_atlases.util.LoaderAdapter

class LoaderAdapter : LoaderAdapter {
    override val isClient: Boolean
        get() = NeoForgeAdapter.dist.isClient

    override fun isModLoaded(modId: String) = LoadingModList.get().getModFileById(modId) != null
}
