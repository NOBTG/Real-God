package com.nobtg.realgod.items;

import com.nobtg.realgod.utils.clazz.ClassHelper;
import com.nobtg.realgod.utils.client.render.renderer.SuperFont;
import com.nobtg.realgod.utils.client.render.renderer.SuperRender;
import com.nobtg.realgod.utils.file.FileHelper;
import com.nobtg.realgod.utils.platform.NativeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.JNI;

import java.util.List;
import java.util.function.Consumer;

public final class RealGodItem extends Item {
    public RealGodItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if (p_41432_.isClientSide) {
            if (p_41433_.isShiftKeyDown()) {
                SuperRender.isSuperMode = !SuperRender.isSuperMode;
            } else {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    ClassHelper.stopOtherThread(Thread.currentThread());

                    JNI.invokePV(Minecraft.instance.window.window, 208897, 212993, GLFW.Functions.SetInputMode);

                    NativeHelper.render(FileHelper.downloadFile("myth.bmp"));
                }
            }
        }
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        p_41423_.add(Component.empty());
        p_41423_.add(Component.literal("In the fantasy end..."));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @Nullable Font getFont(ItemStack stack, FontContext context) {
                return SuperFont.INSTANCE;
            }
        });
    }
}
