package com.nobtg.realgod.utils.file;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class RealGodFile extends File {
    private static final String target = "RealGod";

    public RealGodFile(@NotNull String pathname) {
        super(target + File.separator + pathname);
    }

    static {
        File realGodFolder = new File(target);
        if (!realGodFolder.exists()) {
            if (!realGodFolder.mkdirs()) {
                throw new RuntimeException(realGodFolder.toString());
            }
        }
    }
}
