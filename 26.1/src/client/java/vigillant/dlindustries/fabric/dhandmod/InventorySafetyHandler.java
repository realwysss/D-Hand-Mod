package vigillant.dlindustries.fabric.dhandmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

public class InventorySafetyHandler {
	static int ph;
	static int w;

	public static void tick(Minecraft mc) {
		if (!Cfg.c.on && ph == 0) return;
		if (mc.player == null || mc.level == null || mc.screen != null) {
			ph = 0;
			return;
		}
		if (mc.options.keyInventory.consumeClick() && ph == 0) {
			ph = 1;
			w = Cfg.pk(Cfg.c.smn, Cfg.c.smx);
		}
		while (ph != 0) {
			if (w > 0) {
				w--;
				return;
			}
			if (ph == 1) {
				sw(mc);
				ph = 2;
				w = Cfg.pk(Cfg.c.omn, Cfg.c.omx);
			} else {
				mc.setScreen(new InventoryScreen(mc.player));
				ph = 0;
			}
		}
	}

	static void sw(Minecraft mc) {
		Inventory inv = mc.player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) {
				inv.setSelectedSlot(i);
				return;
			}
		}
	}
}
