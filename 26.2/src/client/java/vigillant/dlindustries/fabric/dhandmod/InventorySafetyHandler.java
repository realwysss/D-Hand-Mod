package vigillant.dlindustries.fabric.dhandmod;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

public class InventorySafetyHandler {
	static boolean busy;
	static final Random rnd = new Random();
	static final ScheduledExecutorService sch = Executors.newSingleThreadScheduledExecutor(r -> {
		Thread t = new Thread(r);
		t.setDaemon(true);
		return t;
	});

	public static void onInventoryKeyPressed() {
		if (busy) return;
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.level == null) return;
		Inventory inv = mc.player.getInventory();
		for (int i = 0; i < 9; i++) {
			if (inv.getItem(i).is(Items.TOTEM_OF_UNDYING)) {
				inv.setSelectedSlot(i);
				break;
			}
		}
		busy = true;
		sch.schedule(() -> mc.execute(() -> {
			try {
				if (mc.player != null && mc.gui.screen() == null) mc.gui.setScreen(new InventoryScreen(mc.player));
			} finally {
				busy = false;
			}
		}), 15 + rnd.nextInt(25), TimeUnit.MILLISECONDS);
	}
}
