package com.wacai.sdk.jtr;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import java.util.HashMap;
import java.util.Map;

class Route {
    private final Map<String, ContextHandler> contexts;
    private final ContextHandler defaultContext;

    public Route(ContextHandler defaultContext) {
        this.defaultContext = defaultContext;
        contexts = new HashMap<>();
    }

    public Route register(String suffix, ContextHandler context) {
        contexts.put(suffix, context);
        return this;
    }

    public Handler asHandler() {
        final Map<String, String> suffix2path = new HashMap<>();

        for (String k : contexts.keySet()) {
            suffix2path.put(k, contexts.get(k).getContextPath());
        }

        final ContextHandlerCollection coll = new ContextHandlerCollection();

        coll.addHandler(dispatcher(suffix2path));
        coll.addHandler(defaultContext);
        for (ContextHandler ctx : contexts.values()) coll.addHandler(ctx);
        return coll;
    }

    private ServletContextHandler dispatcher(Map<String, String> reg2path) {
        final ServletContextHandler context = new ServletContextHandler(null, "/", false, false);
        context.addServlet(new ServletHolder(new DispatchServlet(reg2path, defaultContext.getContextPath())), "/");
        return context;
    }
}
