package com.wacai.sdk.jtr;

import org.eclipse.jetty.http.HttpMethod;
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
        final String path = path(RequestPath.suffix(target));
        if (!paths.contains(path)) {
            if (!"GET".equals(baseRequest.getMethod()))
                baseRequest.setMethod(HttpMethod.GET, HttpMethod.GET.toString());
            super.handle(target, baseRequest, request, allowCrossOrigin(response));
        } else {
            baseRequest.getContext().getContext(path).getRequestDispatcher(target).forward(request, allowCrossOrigin(response));
        }
    }

    static String path(String suffix) {
        return suffix.replace('.', '/');
    }

    static HttpServletResponse allowCrossOrigin(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        return response;
    }

}
