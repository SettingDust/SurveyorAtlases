package settingdust.surveyor_atlases.v1_20.adapter

import app.softwork.serviceloader.ServiceLoader
import net.minecraft.resources.ResourceLocation

@ServiceLoader( settingdust.surveyor_atlases.adapter.MinecraftAdapter::class)
class MinecraftAdapter : settingdust.surveyor_atlases.adapter.MinecraftAdapter {
    override fun id(namespace: String, path: String) = ResourceLocation(namespace, path)
}