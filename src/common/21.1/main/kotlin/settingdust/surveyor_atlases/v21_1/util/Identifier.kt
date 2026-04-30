package settingdust.surveyor_atlases.v21_1.util

import net.minecraft.resources.ResourceLocation
import settingdust.surveyor_atlases.util.Identifier

fun Identifier.toNative(): ResourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path)

fun ResourceLocation.toCommon(): Identifier = Identifier(namespace, path)
