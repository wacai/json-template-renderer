package com.wacai.sdk.jtr;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeInstance;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class VelocityHandler extends AbstractHandler {

    private final VelocityEngine engine;
    private final JsonModel      jsonModel;

    public VelocityHandler(JsonModel jsonModel, File templateDir) {
        this.jsonModel = jsonModel;
        engine = new VelocityEngine();
        engine.setProperty(RuntimeInstance.RESOURCE_LOADER, "file");
        engine.setProperty(RuntimeInstance.FILE_RESOURCE_LOADER_CACHE, "false");
        engine.setProperty(RuntimeInstance.FILE_RESOURCE_LOADER_PATH, templateDir.getAbsolutePath());
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        final String name = RequestPath.base(target);
        final VelocityContext context = new VelocityContext(jsonModel.load(name, baseRequest.getQueryString()));
        final Template template = engine.getTemplate(target,StringUtil.__UTF8);
        response.setContentType("text/html");
        response.setCharacterEncoding(StringUtil.__UTF8);
        template.merge(context, response.getWriter());
    }
}
