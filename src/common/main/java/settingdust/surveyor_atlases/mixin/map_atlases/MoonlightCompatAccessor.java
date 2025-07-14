package settingdust.surveyor_atlases.mixin.map_atlases;

import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import pepjebs.mapatlases.integration.moonlight.MoonlightCompat;

@Mixin(MoonlightCompat.class)
public interface MoonlightCompatAccessor {
    @Accessor("PIN_TYPE_ID")
    static ResourceLocation getPinTypeId() {
        throw new AssertionError();
    }
}
