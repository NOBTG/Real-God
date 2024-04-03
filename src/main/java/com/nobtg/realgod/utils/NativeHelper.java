package com.nobtg.realgod.utils;

import com.nobtg.realgod.utils.file.FileHelper;

public final class NativeHelper {
    public static native void render(String path);

    static {
        System.load(FileHelper.getPath("NativeHelper.dll"));
    }
}
