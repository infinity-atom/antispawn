package me.lennyd.antispawn;

import me.lennyd.antispawn.command.MainCommandRegister;
import me.lennyd.antispawn.config.ConfigMain;
import me.lennyd.antispawn.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class antispawn implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("antispawn");
    public static ConfigMain CF;

    @Override
    public void onInitialize() {
        LOGGER.info("[antispawn] 1.0 init");

        ConfigManager.load();
        CF = ConfigManager.getConfig();

        LOGGER.info("Config loaded");

        CommandRegistrationCallback.EVENT.register(MainCommandRegister::register);
    }
}
