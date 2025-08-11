package settingdust.surveyor_atlases.v1_21.adapter

import net.minecraft.resources.ResourceLocation
import settingdust.surveyor_atlases.adapter.MinecraftAdapter

class MinecraftAdapter : MinecraftAdapter {
    override fun id(namespace: String, path: String) = ResourceLocation.fromNamespaceAndPath(namespace, path)
}