package vigillant.dlindustries.fabric.dhandmod;

import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DhandTst implements FabricClientGameTest {
	ClientGameTestContext c;

	void ck(boolean b, String s) {
		if (!b) throw new AssertionError(s);
		System.out.println("DHAND OK: " + s);
	}

	void key() {
		c.getInput().pressKey(o -> o.keyInventory);
		c.waitTicks(6);
	}

	boolean inv() {
		return c.computeOnClient(m -> m.screen instanceof InventoryScreen);
	}

	int sel() {
		return c.computeOnClient(m -> m.player.getInventory().getSelectedSlot());
	}

	void cls() {
		c.runOnClient(m -> m.setScreen(null));
		c.waitTicks(5);
	}

	public void runTest(ClientGameTestContext ctx) {
		c = ctx;
		try (TestSingleplayerContext s = ctx.worldBuilder().create()) {
			s.getClientWorld().waitForChunksRender();
			ctx.waitTicks(20);

			ctx.runOnClient(m -> m.player.getInventory().setSelectedSlot(0));
			key();
			ck(inv(), "inventory opens with no totem");
			ck(sel() == 0, "slot stays 0 with no totem");
			cls();

			s.getServer().runCommand("item replace entity @p hotbar.5 with totem_of_undying");
			ctx.waitTicks(5);
			ctx.runOnClient(m -> m.player.getInventory().setSelectedSlot(1));
			key();
			ck(sel() == 5, "slot jumps to the totem slot");
			ck(inv(), "inventory opens after the jump");
			ctx.takeScreenshot("inv-totm");
			cls();

			key();
			ck(sel() == 5 && inv(), "works again with the totem already selected");
			cls();

			ck(ctx.computeOnClient(m -> {
				ItemStack i = new ItemStack(Items.DIAMOND);
				i.setPopTime(5);
				return i.getPopTime() == 0;
			}), "pop animation time is always 0");

			ctx.runOnClient(m -> m.player.getInventory().setSelectedSlot(0));
			ctx.getInput().pressKey(o -> o.keyChat);
			ctx.waitTicks(6);
			ctx.getInput().pressKey(o -> o.keyInventory);
			ctx.waitTicks(6);
			ck(!inv(), "no inventory while another screen is open");
			ck(sel() == 0, "slot untouched while another screen is open");
			ctx.runOnClient(m -> m.setScreen(null));
			ctx.waitTicks(5);
		}
	}
}
