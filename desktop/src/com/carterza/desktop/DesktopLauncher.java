package com.carterza.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.carterza.Derelict;

import java.util.Arrays;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		for(int i = 0; i < arg.length; i++) {
			if(arg[i].equals("dev")) {
				TexturePacker.process("unpacked", "packed", "derelict");
				break;
			}
		}

		new LwjglApplication(new Derelict(), config);
	}
}
