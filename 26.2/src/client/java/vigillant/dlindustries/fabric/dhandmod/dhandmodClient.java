package vigillant.dlindustries.fabric.dhandmod;

import net.fabricmc.api.ClientModInitializer;

public class dhandmodClient implements ClientModInitializer {
	public void onInitializeClient() {
		Cfg.ld();
		System.out.println("[D-Hand-Mod] Version 1.6.0");
	}
}
