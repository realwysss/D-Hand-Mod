package vigillant.dlindustries.fabric.dhandmod;

import java.util.function.IntConsumer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CfgScr {
	static Component t(String s) {
		return Component.literal(s);
	}

	static void sl(SubCategoryBuilder g, ConfigEntryBuilder e, String n, int v, int d, IntConsumer s) {
		g.add(e.startIntSlider(t(n), v, 0, 10).setDefaultValue(d).setTooltip(t("In ticks, 20 ticks are 1 second")).setSaveConsumer(s::accept).build());
	}

	public static Screen mk(Screen p) {
		ConfigBuilder b = ConfigBuilder.create().setParentScreen(p).setTitle(t("D-Hand Mod")).setSavingRunnable(Cfg::sv);
		ConfigCategory cc = b.getOrCreateCategory(t("Settings"));
		ConfigEntryBuilder e = b.entryBuilder();
		Cfg c = Cfg.c;
		cc.addEntry(e.startBooleanToggle(t("Mod On"), c.on).setDefaultValue(true).setSaveConsumer(v -> c.on = v).build());
		cc.addEntry(e.startTextDescription(t("Every delay picks a random number of ticks between its start and end.")).build());
		cc.addEntry(e.startTextDescription(t("Warning: 0 - 0 means no delay at all and might get you banned.").copy().withStyle(ChatFormatting.RED)).build());
		SubCategoryBuilder g = e.startSubCategory(t("Switch To Totem Delay")).setExpanded(true);
		sl(g, e, "Start", c.smn, 1, v -> c.smn = v);
		sl(g, e, "End", c.smx, 3, v -> c.smx = v);
		cc.addEntry(g.build());
		SubCategoryBuilder h = e.startSubCategory(t("Open Inventory Delay")).setExpanded(true);
		sl(h, e, "Start", c.omn, 1, v -> c.omn = v);
		sl(h, e, "End", c.omx, 3, v -> c.omx = v);
		cc.addEntry(h.build());
		return b.build();
	}
}
