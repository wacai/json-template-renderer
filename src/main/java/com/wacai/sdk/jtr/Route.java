package com.wacai.sdk.jtr;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;

import java.util.ArrayList;
import java.util.Collection;

class Route {
    private final Collection<ContextHandler> contexts;
    private final String                     resourceBase;
    private final Collection<String>         whitelist;

    public Route(String resourceBase, Collection<String> whitelist) {
        this.resourceBase = resourceBase;
        this.whitelist = whitelist;
        contexts = new ArrayList<>();
    }

    public Route register(ContextHandler context) {
        contexts.add(context);
        return this;
    }

    public Handler asHandler() {
        final Collection<String> suffix = new ArrayList<>();

        for (ContextHandler c : contexts) {
            suffix.add(c.getContextPath());
        }

        final HandlerCollection coll = new HandlerCollection();

        coll.addHandler(dispatcher(suffix));
        for (ContextHandler ctx : contexts) coll.addHandler(ctx);
        return coll;
    }


    private ContextHandler dispatcher(Collection<String> suffix) {
        final ContextHandler context = new ContextHandler("/");
        context.setHandler(new Dispatch(suffix, resourceBase, whitelist));
        return context;
    }
}
