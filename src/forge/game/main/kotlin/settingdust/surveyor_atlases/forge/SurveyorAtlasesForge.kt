package settingdust.surveyor_atlases.forge

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import settingdust.surveyor_atlases.SurveyorAtlases
import settingdust.surveyor_atlases.util.Entrypoint
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(SurveyorAtlases.ID)
object SurveyorAtlasesForge {
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