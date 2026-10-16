package vigillant.dlindustries.fabric.dhandmod;

import dev.wysl.Wl;
import dev.wysl.ui.Frm;
import net.minecraft.client.gui.screens.Screen;

public class Gui {
	public static Screen open(Screen p) {
		Cfg c = Cfg.c;
		Frm f = Frm.of("D-Hand Mod", Cfg::sv)
				.tab("Settings")
				.tgl("Mod on", () -> c.on, v -> c.on = v)
				.note("Every delay picks a random number of ticks between start and end.")
				.note("Warning: 0 - 0 means no delay and might get you banned.")
				.tab("Totem")
				.sld("Start", () -> c.smn, v -> c.smn = v, 0, 10, " ticks")
				.sld("End", () -> c.smx, v -> c.smx = v, 0, 10, " ticks")
				.note("Delay when switching to a totem.")
				.tab("Inventory")
				.sld("Start", () -> c.omn, v -> c.omn = v, 0, 10, " ticks")
				.sld("End", () -> c.omx, v -> c.omx = v, 0, 10, " ticks")
				.note("Delay when opening the inventory.");
		return Wl.screen(p, f, "D-Hand Mod", dhandmodClient.key);
	}
}
