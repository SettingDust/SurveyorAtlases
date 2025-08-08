package settingdust.surveyor_atlases.v1_21.marker

import com.mojang.serialization.codecs.RecordCodecBuilder
import folk.sisby.surveyor.WorldSummary
import folk.sisby.surveyor.landmark.Landmark
import folk.sisby.surveyor.landmark.Landmarks
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos

object Codecs {
    val LANDMARK = RecordCodecBuilder.mapCodec<Landmark<*>> { instance ->
        instance.group(
            Landmarks.TYPE_CODEC.fieldOf("type").forGetter { it.type() },
            BlockPos.CODEC.fieldOf("pos").forGetter { it.pos() },
        ).apply(instance) { type, pos -> WorldSummary.of(Minecraft.getInstance().level).landmarks()!!.get(type, pos) }
    }
}