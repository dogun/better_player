package com.my66.better_player;

import com.mojang.logging.LogUtils;
import com.my66.better_player.client.packet.ClientPacketHandler;
import com.my66.better_player.packet.*;
import com.my66.better_player.server.packet.ServerPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(BetterPlayerMod.MODID)
public class BetterPlayerMod
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "better_player";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public BetterPlayerMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModPacketHandler.register();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientPacketHandler::register);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> ServerPacketHandler::register);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info("{}{}", Config.magicNumberIntroduction, Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static void setPlayerDataLocal(Player player, BetterPlayer bPlayer, boolean reset) {
        if (player == null) {
            System.out.println("BetterPlayMod.setPlayerDataLocal: player is null");
            //return;
        }
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(bPlayer.getConstitution());
        }

        //本地设置
        if (!reset) {
            player.setHealth(Math.min(bPlayer.getConstitution(), player.getHealth()));
            player.getFoodData().setFoodLevel(Math.min(bPlayer.getEndurance(), player.getFoodData().getFoodLevel()));
            player.setAirSupply(Math.min(bPlayer.getAirSupply(), player.getAirSupply()));
        }else {
            player.setHealth(bPlayer.getConstitution());
            player.getFoodData().setFoodLevel(bPlayer.getEndurance());
            player.setAirSupply(bPlayer.getAirSupply());
            bPlayer.setThirst(bPlayer.getWater());
        }
        System.out.println("local set health: " + player.getHealth() + ", set foodLevel: " + player.getFoodData().getFoodLevel() + ", airSupply: " + player.getAirSupply());
    }

    public static void setPlayerDataToServer(BetterPlayer bPlayer, boolean reset) {
        ModPacketHandler.INSTANCE.sendToServer(new SaveDataPacket(reset, bPlayer.getData()));
    }

    public static void saveSSData(ServerPlayer player, String data) {
        if (player != null) {
            try {
                Path path = player.getServer().getWorldPath(LevelResource.PLAYER_DATA_DIR);
                String uuid = player.getStringUUID();
                Path wfile = path.resolve(uuid + ".ss");
                OutputStream out = Files.newOutputStream(wfile);
                out.write(data.getBytes(StandardCharsets.UTF_8));
                out.close();
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String loadSSData(ServerPlayer player) {
        String data = "";
        if (player != null) {
            try {
                Path path = player.getServer().getWorldPath(LevelResource.PLAYER_DATA_DIR);
                String uuid = player.getStringUUID();
                Path wfile = path.resolve(uuid + ".ss");
                if (Files.exists(wfile)) {
                    InputStream is = Files.newInputStream(wfile);
                    data = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    is.close();
                }
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return data;
    }

    private static FileTime lastModified;
    public static String loadConfig(ServerPlayer player, boolean updated) {
        String data = null;
        if (player != null) {
            try {
                Path path = player.getServer().getWorldPath(LevelResource.PLAYER_DATA_DIR);
                Path wfile = path.resolve("config.ss");
                if (Files.exists(wfile)) {
                    FileTime fileTime = Files.getLastModifiedTime(wfile);
                    if (lastModified == null) {
                        lastModified = fileTime;
                    }
                    if (!updated || lastModified.compareTo(fileTime) != 0) {
                        InputStream is = Files.newInputStream(wfile);
                        data = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        is.close();
                    }
                }
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return data;
    }
}
