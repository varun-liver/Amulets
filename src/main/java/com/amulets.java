package com;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
// The value here should match an entry in the META-INF/mods.toml file
@Mod(amulets.MODID)
public class amulets {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "amulets";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "amulets" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "amulets" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold Menus which will all be registered under the "amulets" namespace
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "amulets" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    //----------------------------------BLOCKS----------------------------------
    // Creates a new Block with the id "amulets:example_block", combining the namespace and path
    public static final RegistryObject<Block> Amulet_Maker = BLOCKS.register("amulet_maker", () -> new AmuletWorkbenchBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)));
    // Creates a new BlockItem with the id "amulets:example_block", combining the namespace and path
    public static final RegistryObject<Item> Amulet_Maker_ITEM = ITEMS.register("amulet_maker", () -> new BlockItem(Amulet_Maker.get(), new Item.Properties()));
    public static final RegistryObject<MenuType<AmuletWorkbenchMenu>> Amulet_WORKBENCH_MENU = MENUS.register("amulet_workbench", () -> IForgeMenuType.create(AmuletWorkbenchMenu::new));
    //----------------------------------ITEMS----------------------------------
    // Creates a new food item with the id "amulets:example_id", nutrition 1 and saturation 2
    public static final RegistryObject<Item> STRENGTH_AMULET = ITEMS.register("strength_amulet", StrengthAmulet::new);
    public static final RegistryObject<Item> SPEED_AMULET = ITEMS.register("speed_amulet", SpeedAmulet::new);
    //----------------------------------CHAIN----------------------------------
    public static final RegistryObject<Item> IRON_CHAIN = ITEMS.register("iron_chain", () -> new Chain("iron",new Item.Properties()));
    public static final RegistryObject<Item> GOLD_CHAIN = ITEMS.register("gold_chain", () -> new Chain("gold",new Item.Properties()));
    public static final RegistryObject<Item> COPPER_CHAIN = ITEMS.register("copper_chain", () -> new Chain("copper",new Item.Properties()));
    public static final RegistryObject<Item> DIAMOND_CHAIN = ITEMS.register("diamond_chain", () -> new Chain("diamond",new Item.Properties()));
    public static final RegistryObject<Item> NETHERITE_CHAIN = ITEMS.register("netherite_chain", () -> new Chain("netherite",new Item.Properties()));
    //----------------------------------ORES----------------------------------
    public static final RegistryObject<Item> AZULI = ITEMS.register("azuli", () -> new AmuletOre(new Item.Properties()));
    public static final RegistryObject<Item> PALADIN = ITEMS.register("paladin", () -> new AmuletOre(new Item.Properties()));
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> STRENGTH_AMULET.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(STRENGTH_AMULET.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    public amulets() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so menus get registered
        MENUS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(Amulet_Maker_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide()) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        CuriosApi.getCuriosInventory(serverPlayer).ifPresent(inv -> {
            ICurioStacksHandler handler = inv.getCurios().get("amulet");
            if (handler == null) return;

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStacks().getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (stack.getItem() instanceof AmuletItem amuletItem) {
                    amuletItem.onEquippedCurioTick(serverPlayer, stack);
                }
            }
        });
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            event.enqueueWork(() -> MenuScreens.register(Amulet_WORKBENCH_MENU.get(), AmuletWorkbenchScreen::new));
        }
    }
}
