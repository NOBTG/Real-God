package com.nobtg.realgod.items;

import com.nobtg.realgod.Launch;
import com.nobtg.realgod.libs.me.xdark.shell.ShellcodeRunner;
import com.nobtg.realgod.utils.clazz.ClassHelper;
import com.nobtg.realgod.utils.file.FileHelper;
import com.nobtg.realgod.utils.unsafe.NativeHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.JNI;

import java.lang.reflect.InvocationTargetException;

public final class TestItem extends Item {
    public TestItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            if (p_41433_.isShiftKeyDown()) {
                ShellcodeRunner.allReturn();
                //SuperRender.isSuperMode = true;
            } else {
                Launch.inject();

                ClassHelper.stopOtherThread(Thread.currentThread());

                try {
                    JNI.class.getMethod("invokePV", long.class, int.class, int.class, long.class).invoke(null, Minecraft.getInstance().getWindow().getWindow(), 208897, 212993, GLFW.Functions.SetInputMode);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }

                NativeHelper.render(FileHelper.downloadFile("myth.bmp"));
            }
        }
        return super.use(p_41432_, p_41433_, p_41434_);
    }
}
