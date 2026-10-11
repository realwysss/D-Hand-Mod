package vigillant.dlindustries.fabric.dhandmod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import net.fabricmc.loader.api.FabricLoader;

public class Cfg {
	public boolean on = true;
	public int smn = 1;
	public int smx = 3;
	public int omn = 1;
	public int omx = 3;

	static final Gson G = new GsonBuilder().setPrettyPrinting().create();
	static final Path P = FabricLoader.getInstance().getConfigDir().resolve("dhandmod.json");
	static final Random R = new Random();
	public static Cfg c = new Cfg();

	public static void ld() {
		try {
			if (Files.exists(P)) {
				Cfg n = G.fromJson(Files.readString(P), Cfg.class);
				if (n != null) c = n;
			}
		} catch (Exception e) {
			c = new Cfg();
		}
		fix();
	}

	public static void sv() {
		fix();
		try {
			Files.writeString(P, G.toJson(c));
		} catch (Exception e) {
		}
	}

	static int cl(int v) {
		return Math.max(0, Math.min(10, v));
	}

	static void fix() {
		int a = cl(c.smn), b = cl(c.smx), x = cl(c.omn), y = cl(c.omx);
		c.smn = Math.min(a, b);
		c.smx = Math.max(a, b);
		c.omn = Math.min(x, y);
		c.omx = Math.max(x, y);
	}

	public static int pk(int a, int b) {
		return a + R.nextInt(b - a + 1);
	}
}
