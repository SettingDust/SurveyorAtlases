package settingdust.surveyor_atlases.neoforge.util

import net.neoforged.api.distmarker.Dist
import settingdust.surveyor_atlases.util.ServiceLoaderUtil

interface NeoForgeAdapter {
    companion object : NeoForgeAdapter by ServiceLoaderUtil.findService()

    val dist: Dist
}
