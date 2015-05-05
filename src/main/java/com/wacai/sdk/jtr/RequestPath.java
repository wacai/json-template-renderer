package com.wacai.sdk.jtr;

public final class RequestPath {

    public static String suffix(String target) {
        final int index = target.lastIndexOf(".");
        if (index < 0) return null;
        return target.substring(index);
    }

    private RequestPath() { }
}
