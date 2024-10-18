package settingdust.surveyor_atlases.mixin.map_atlases;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pepjebs.mapatlases.client.screen.AtlasOverviewScreen;
import pepjebs.mapatlases.utils.DecorationHolder;
import settingdust.surveyor_atlases.SurveyorAtlases;

import java.util.Collection;

@Mixin(AtlasOverviewScreen.class)
public class AtlasOverviewScreenMixin {
    @ModifyExpressionValue(
        method = "addDecorationWidgets", at = @At(
        value = "INVOKE",
        target = "Lpepjebs/mapatlases/integration/moonlight/MoonlightCompat;getCustomDecorations(Lpepjebs/mapatlases/utils/MapDataHolder;)Ljava/util/Collection;"
    )
    )
    private Collection<DecorationHolder> surveyor_atlases$removeSurveyors(final Collection<DecorationHolder> original) {
        original.removeIf(holder -> holder.id().startsWith(SurveyorAtlases.ID));
        return original;
    }
}
