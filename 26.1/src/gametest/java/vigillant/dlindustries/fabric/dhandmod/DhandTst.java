package vigillant.dlindustries.fabric.dhandmod;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DhandTst implements FabricClientGameTest {
	ClientGameTestContext c;

	void ck(boolean b, String s) {
		if (!b) throw new AssertionError(s);
		System.out.println("DHAND OK: " + s);
	}

	void cf(boolean on, int a, int b, int x, int y) {
		c.runOnClient(m -> {
			Cfg.c.on = on;
			Cfg.c.smn = a;
			Cfg.c.smx = b;
			Cfg.c.omn = x;
			Cfg.c.omx = y;
		});
	}

	void bind(int k) {
		c.runOnClient(m -> {
			m.options.keyInventory.setKey(k < 0 ? m.options.keyInventory.getDefaultKey() : InputConstants.Type.KEYSYM.getOrCreate(k));
			KeyMapping.resetMapping();
		});
		c.waitTicks(2);
	}

	boolean inv() {
		return c.computeOnClient(m -> m.screen instanceof InventoryScreen);
	}

	int sel() {
		return c.computeOnClient(m -> m.player.getInventory().getSelectedSlot());
	}

	void slot(int i) {
		c.runOnClient(m -> m.player.getInventory().setSelectedSlot(i));
		c.waitTicks(2);
	}

	void cls() {
		c.runOnClient(m -> m.setScreen(null));
		c.waitTicks(5);
	}

	void key() {
		c.getInput().pressKey(o -> o.keyInventory);
		c.waitTicks(10);
	}

	int[] run() {
		int sw = -1, op = -1;
		c.getInput().pressKey(o -> o.keyInventory);
		for (int t = 1; t <= 40; t++) {
			if (sw < 0 && sel() == 5) sw = t;
			if (op < 0 && inv()) op = t;
			if (op >= 0) break;
			c.waitTicks(1);
		}
		return new int[] { sw, op };
	}

	public void runTest(ClientGameTestContext ctx) {
		c = ctx;
		try (TestSingleplayerContext s = ctx.worldBuilder().create()) {
			ctx.waitTicks(40);
			Cfg d = new Cfg();
			ck(d.on && d.smn == 1 && d.smx == 3 && d.omn == 1 && d.omx == 3, "defaults are on with 1 - 3 for both delays");
			int lo = 99, hi = -1;
			for (int i = 0; i < 2000; i++) {
				int v = Cfg.pk(1, 3);
				lo = Math.min(lo, v);
				hi = Math.max(hi, v);
			}
			ck(lo == 1 && hi == 3, "random pick stays inside 1 - 3 and uses both ends");

			cf(true, 1, 3, 1, 3);
			s.getServer().runCommand("item replace entity @p hotbar.5 with totem_of_undying");
			ctx.waitTicks(5);

			slot(0);
			cf(true, 1, 3, 1, 3);
			key();
			ck(sel() == 5 && inv(), "default key E: slot jumps to the totem and inventory opens");
			ctx.takeScreenshot("inv-totm");
			cls();

			cf(true, 1, 3, 1, 3);
			s.getServer().runCommand("clear @p minecraft:totem_of_undying");
			ctx.waitTicks(3);
			slot(2);
			key();
			ck(sel() == 2 && inv(), "no totem: slot stays and inventory opens");
			cls();
			s.getServer().runCommand("item replace entity @p hotbar.5 with totem_of_undying");
			ctx.waitTicks(5);

			bind(InputConstants.KEY_R);
			slot(3);
			c.getInput().pressKey(InputConstants.KEY_E);
			ctx.waitTicks(10);
			ck(!inv() && sel() == 3, "old key E does nothing after rebinding");
			c.getInput().pressKey(InputConstants.KEY_R);
			ctx.waitTicks(10);
			ck(sel() == 5 && inv(), "rebound key R: slot jumps to the totem and inventory opens");
			cls();
			bind(InputConstants.KEY_G);
			slot(4);
			c.getInput().pressKey(InputConstants.KEY_G);
			ctx.waitTicks(10);
			ck(sel() == 5 && inv(), "rebound key G works too");
			cls();
			bind(-1);

			cf(true, 4, 4, 5, 5);
			slot(1);
			int[] r = run();
			ck(r[0] >= 3 && r[0] <= 8, "switch waits about 4 ticks, got " + r[0]);
			ck(r[1] - r[0] >= 4 && r[1] - r[0] <= 8, "open waits about 5 more ticks, got " + (r[1] - r[0]));
			cls();

			cf(true, 0, 0, 0, 0);
			slot(1);
			r = run();
			ck(r[0] >= 0 && r[0] <= 3 && r[1] <= 3, "0 - 0 does both at once, got " + r[0] + " and " + r[1]);
			cls();

			cf(true, 10, 10, 0, 0);
			slot(1);
			r = run();
			ck(r[0] >= 9 && r[0] <= 14, "10 tick delay works, got " + r[0]);
			cls();

			cf(false, 1, 3, 1, 3);
			slot(1);
			key();
			ck(sel() == 1 && inv(), "mod off: vanilla opens the inventory and the slot stays");
			cls();

			Cfg.c = new Cfg();
			Cfg.c.smn = 8;
			Cfg.c.smx = 2;
			Cfg.c.omn = -3;
			Cfg.c.omx = 99;
			Cfg.sv();
			ck(Cfg.c.smn == 2 && Cfg.c.smx == 8 && Cfg.c.omn == 0 && Cfg.c.omx == 10, "bad values get swapped and clamped to 0 - 10");
			Cfg.c = new Cfg();
			Cfg.c.on = false;
			Cfg.ld();
			ck(Cfg.c.on && Cfg.c.smn == 2 && Cfg.c.omx == 10, "config reloads from the json file");
			Cfg.c = new Cfg();
			Cfg.sv();

			ctx.runOnClient(m -> m.setScreen(CfgScr.mk(null)));
			ctx.waitTicks(10);
			ck(ctx.computeOnClient(m -> m.screen.getClass().getSimpleName().equals("ClothConfigScreen")), "cloth config screen opens");
			ctx.takeScreenshot("cfg-main");
			ctx.getInput().scroll(-10);
			ctx.waitTicks(5);
			ctx.takeScreenshot("cfg-low");
			ctx.getInput().scroll(10);
			ctx.waitTicks(5);
			ctx.getInput().setCursorPos(590, 155);
			ctx.getInput().pressMouse(0);
			ctx.waitTicks(3);
			ctx.takeScreenshot("cfg-off");
			ctx.getInput().setCursorPos(552, 447);
			ctx.getInput().pressMouse(0);
			ctx.waitTicks(5);
			ck(!Cfg.c.on, "clicking the Mod On switch and Save & Quit turns the mod off");
			ck(ctx.computeOnClient(m -> m.screen == null), "save and quit closes the screen");
			Cfg.c = new Cfg();
			Cfg.sv();

			ck(ctx.computeOnClient(m -> {
				ItemStack i = new ItemStack(Items.DIAMOND);
				i.setPopTime(5);
				return i.getPopTime() == 0;
			}), "pop animation time is always 0");
		}
	}
}
