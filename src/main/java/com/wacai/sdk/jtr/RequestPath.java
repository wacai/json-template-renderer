package com.wacai.sdk.jtr;

public final class RequestPath {

    public static String suffix(String target) {
        final int index = target.lastIndexOf(".");
        if (index < 0) return "";
        return target.substring(index);
    }

    public static String base(String path) {
        final int i = path.lastIndexOf('.');
        if (i < 0) return path;
        return path.substring(0, i);
    }

    private RequestPath() { }
}
