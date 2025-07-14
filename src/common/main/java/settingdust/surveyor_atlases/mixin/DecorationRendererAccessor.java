package settingdust.surveyor_atlases.mixin;

import net.mehvahdjukaar.moonlight.api.map.client.DecorationRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DecorationRenderer.class)
public interface DecorationRendererAccessor {
    @Accessor
    @Mutable
    void setTextureId(ResourceLocation textureId);
}
