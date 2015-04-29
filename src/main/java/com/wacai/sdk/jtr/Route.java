package com.wacai.sdk.jtr;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;

import java.util.*;

class Route {
    private final Collection<ContextHandler> contexts;
    private final String                     resourceBase;

    public Route(String resourceBase) {
        this.resourceBase = resourceBase;
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

        coll.addHandler(requestLog());
        coll.addHandler(dispatcher(suffix));
        for (ContextHandler ctx : contexts) coll.addHandler(ctx);
        return coll;
    }

    private RequestLogHandler requestLog() {
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(new Slf4jRequestLog());
        return requestLogHandler;
    }

    private ContextHandler dispatcher(Collection<String> suffix) {
        final ContextHandler context = new ContextHandler("/");
        context.setHandler(new Dispatch(suffix, resourceBase));
        return context;
    }
}
