package settingdust.surveyor_atlases.neoforge.v21_1

import dev.nyon.klf.MOD_BUS
import settingdust.surveyor_atlases.util.Entrypoint
import settingdust.surveyor_atlases.util.MinecraftVersion

class SurveyorAtlasesNeoForgeEntrypoint : Entrypoint {
    init {
        MinecraftVersion.V1211.requireCurrent()
        @Suppress("RedundantRequireNotNullCall")
        requireNotNull(MOD_BUS)
    }

    override fun construct() {
    }
}
