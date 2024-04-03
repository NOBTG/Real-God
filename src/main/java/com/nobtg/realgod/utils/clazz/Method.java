package com.nobtg.realgod.utils.clazz;

public final class Method {
    public final String name;
    public final String clazz;
    public final String desc;

    public Method(String name, String clazz, String desc) {
        this.name = name.replace(".", "/");
        this.clazz = clazz;
        this.desc = desc;
    }
}
