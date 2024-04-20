package com.nobtg.realgod;

import com.nobtg.realgod.items.RealGodItem;
import com.nobtg.realgod.utils.client.render.avaritia.AvaritiaShaders;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ModMain.modID)
public class ModMain {
    public static final String modID = "real_god";

    public ModMain() {
        DeferredRegister<Item> items = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.modID);
        items.register("real_god", RealGodItem::new);
        items.register(FMLJavaModLoadingContext.get().getModEventBus());
        AvaritiaShaders.init();
    }
}
