package settingdust.surveyor_atlases.v1_21.marker

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec

object StreamCodecs {
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