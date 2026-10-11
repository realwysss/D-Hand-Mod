package vigillant.dlindustries.fabric.dhandmod.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vigillant.dlindustries.fabric.dhandmod.InventorySafetyHandler;

@Mixin(Minecraft.class)
public abstract class InventorySwitchMixin {
	@Inject(method = "handleKeybinds", at = @At("HEAD"))
	private void onkeys(CallbackInfo ci) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.keyInventory.consumeClick() && mc.gui.screen() == null) InventorySafetyHandler.onInventoryKeyPressed();
	}
}
