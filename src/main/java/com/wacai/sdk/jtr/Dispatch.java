package com.wacai.sdk.jtr;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class Dispatch extends ResourceHandler {

    private final Collection<String> paths;

    public Dispatch(Collection<String> paths, String base) {
        this.paths = paths;
        setDirectoriesListed(true);
        setResourceBase(base);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String path = path(target);
        if (!paths.contains(path)) {
            super.handle(target, baseRequest, request, response);
        } else {
            baseRequest.getContext().getContext(path).getRequestDispatcher(target).forward(request, response);
        }
    }

    private String path(String target) {
        final StringBuilder sb = new StringBuilder(target);
        final int index = sb.lastIndexOf(".");
        if (index < 0) return null;
        return sb.substring(index).replace('.', '/');
    }
}
