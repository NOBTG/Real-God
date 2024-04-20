package com.nobtg.realgod.utils.client.render;

import com.nobtg.realgod.utils.ReflectionHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.Buffer;

@OnlyIn(Dist.CLIENT)
public final class BufferHelper {
    private static final VarHandle position;
    private static final VarHandle limit;
    private static final VarHandle capacity;
    private static final VarHandle mark;

    static {
        MethodHandles.Lookup lookup = ReflectionHelper.lookup;
        try {
            position = lookup.findVarHandle(Buffer.class, "position", int.class);
            limit = lookup.findVarHandle(Buffer.class, "limit", int.class);
            capacity = lookup.findVarHandle(Buffer.class, "capacity", int.class);
            mark = lookup.findVarHandle(Buffer.class, "mark", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void clear(Buffer buffer) {
        position.set(buffer, 0);
        limit.set(buffer, capacity.get(buffer));
        mark.set(buffer, -1);
    }

    public static void rewind(Buffer buffer) {
        position.set(buffer, 0);
        mark.set(buffer, -1);
    }

    public static int remaining(Buffer buffer) {
        return Math.max(((int) limit.get(buffer)) - ((int) position.get(buffer)), 0);
    }

    public static int position(Buffer buffer) {
        return (int) position.get(buffer);
    }

    public static void position(Buffer buffer, int newPosition) {
        if (((int) mark.get(buffer)) > newPosition) {
            mark.set(buffer, -1);
        }
        position.set(buffer, newPosition);
    }
}
