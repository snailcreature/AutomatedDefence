package codes.snail;

import codes.snail.screen.AutoTurretLoadingScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import static codes.snail.AutomatedDefence.AUTO_TURRET_LOADING_SCREEN_HANDLER;

public class AutomatedDefenceClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		HandledScreens.register(AUTO_TURRET_LOADING_SCREEN_HANDLER, AutoTurretLoadingScreen::new);
	}
}