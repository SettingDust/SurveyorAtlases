package settingdust.surveyor_atlases.v21_1.util

import folk.sisby.surveyor.landmark.Landmark
import folk.sisby.surveyor.landmark.component.LandmarkComponentMap
import io.netty.buffer.ByteBuf
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation

object StreamCodecs {
    val LANDMARK = StreamCodec.composite(
        UUIDUtil.STREAM_CODEC, { it.owner() },
        ResourceLocation.STREAM_CODEC, { it.id() },
        ByteBufCodecs.fromCodec(LandmarkComponentMap.CODEC), { it.components() },
        ::Landmark
    )

    fun <B : ByteBuf, V> nullable(codec: StreamCodec<B, V>): StreamCodec<B, V?> = StreamCodec.of(
        { buf, value ->
            if (value == null) {
                buf.writeBoolean(false)
            } else {
                buf.writeBoolean(true)
                codec.encode(buf, value)
            }
        }, {
            if (it.readBoolean()) {
                codec.decode(it)
            } else {
                null
            }
        })
}