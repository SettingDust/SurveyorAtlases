package settingdust.surveyor_atlases.neoforge

import dev.nyon.klf.MOD_BUS
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.util.Entrypoint

@Mod(SurveyorAtlases.ID)
object SurveyorAtlasesNeoForge {
    init {
        requireNotNull(SurveyorAtlases)
        Entrypoint.construct()
        MOD_BUS.apply {
            addListener<FMLCommonSetupEvent> {
                Entrypoint.init()
            }
            addListener<FMLClientSetupEvent> { Entrypoint.clientInit() }
        }
    }
}
