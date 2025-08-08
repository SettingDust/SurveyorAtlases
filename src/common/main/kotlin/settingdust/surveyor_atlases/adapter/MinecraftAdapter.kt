package settingdust.surveyor_atlases.adapter

import net.minecraft.resources.ResourceLocation
import settingdust.surveyor_atlases.ServiceLoaderUtil

interface MinecraftAdapter {
    companion object : MinecraftAdapter by ServiceLoaderUtil.findService()

    fun id(namespace: String, path: String): ResourceLocation
}