package settingdust.surveyor_atlases.v21_1.util

import com.mojang.serialization.codecs.RecordCodecBuilder
import folk.sisby.surveyor.landmark.Landmark
import folk.sisby.surveyor.landmark.component.LandmarkComponentMap
import net.minecraft.core.UUIDUtil
import net.minecraft.resources.ResourceLocation

object Codecs {
    val LANDMARK = RecordCodecBuilder.mapCodec { instance ->
        instance.group(
            UUIDUtil.CODEC.fieldOf("owner").forGetter { it.owner() },
            ResourceLocation.CODEC.fieldOf("id").forGetter { it.id() },
            LandmarkComponentMap.CODEC.fieldOf("components").forGetter(Landmark::components)
        ).apply(instance, ::Landmark)
    }
}