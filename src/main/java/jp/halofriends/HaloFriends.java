package jp.halofriends;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Three cosmetic helmets whose visible shape is drawn on the client. */
@Mod(HaloFriends.MOD_ID)
public final class HaloFriends {
    public static final String MOD_ID = "halo_friends";

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);

    public static final RegistryObject<Item> SHIROKO = create("shiroko_halo");
    public static final RegistryObject<Item> HINA = create("hina_halo");
    public static final RegistryObject<Item> WAKAMO = create("wakamo_halo");

    // A zero-protection helmet to use Minecraft's standard multiplayer equipment sync.
    private static final ArmorMaterial MATERIAL = new HaloMaterial();

    private static RegistryObject<Item> create(String name) {
        return ITEMS.register(name, () -> new ArmorItem(
                MATERIAL, ArmorItem.Type.HELMET, new Item.Properties()));
    }

    public HaloFriends() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        bus.addListener(this::addToCreativeTabs);
    }

    private void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(SHIROKO.get());
            event.accept(HINA.get());
            event.accept(WAKAMO.get());
        }
    }

    private static final class HaloMaterial implements ArmorMaterial {
        @Override public int getDurabilityForType(ArmorItem.Type type) { return 100; }
        @Override public int getDefenseForType(ArmorItem.Type type) { return 0; }
        @Override public int getEnchantmentValue() { return 0; }
        @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_GOLD; }
        @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
        @Override public String getName() { return "halo_friends:halo"; }
        @Override public float getToughness() { return 0.0f; }
        @Override public float getKnockbackResistance() { return 0.0f; }
    }
}
