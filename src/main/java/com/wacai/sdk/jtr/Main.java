package com.wacai.sdk.jtr;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Main {

    static final String INCLUDE_JAR_PATTERN = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    static final String JSP_PATTERN         = ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$";
    static final String DEFAULT_DESC        = "com/wacai/sdk/jtr/web.xml";

    static {
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args.length != 2) {
            throw new IllegalArgumentException("Arguments should be : <template-dir> <json-dir>");
        }

        final File templateDir = getDir(args, 0, "./");
        final File jsonDir = getDir(args, 1, "./");
        final int port = Integer.getInteger(Props.SERVER_PORT, 8080);
        final String descriptor = System.getProperty(Props.CONTEXT_DESCRIPTOR, DEFAULT_DESC);

        final Server server = new Server(port);

        server.setHandler(new Main().handler(templateDir, jsonDir, descriptor));

        server.start();
        server.join();
    }

    public Handler handler(File templateDir, File jsonDir, String descriptor) throws Exception {
        final Route route = new Route(resource("/s/", templateDir));
        route.register(".jsp", jsp("/j/", templateDir, descriptor));
        return route.asHandler();
    }

    ContextHandler jsp(String ctx, File templateDir, String descriptor) {
        final WebAppContext context = new WebAppContext(templateDir.getAbsolutePath(), ctx);
        context.setDescriptor(descriptor);
        context.setAttribute(INCLUDE_JAR_PATTERN, JSP_PATTERN);
        context.setAttribute("javax.servlet.context.tempdir", "/tmp/jsp");
        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        context.addBean(new ServletContainerInitializersStarter(context), true);
        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        return context;
    }

    ContextHandler resource(String ctx, File templateDir) {
        final ContextHandler context = new ContextHandler(ctx);

        final ResourceHandler handler = new ResourceHandler();
        handler.setResourceBase(templateDir.getAbsolutePath());
        handler.setDirectoriesListed(true);

        context.setHandler(handler);
        return context;
    }

    private static List<ContainerInitializer> jspInitializers() {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
        initializers.add(initializer);
        return initializers;
    }


    private static File getDir(String[] args, int index, String defaultValue) {
        final File file = args.length == 0 ? new File(defaultValue) : new File(args[index]);
        if (!file.exists() || !file.isDirectory()) throw new IllegalArgumentException(file + " is not a existed dir.");
        return file;
    }

    interface Props {
        String SERVER_PORT        = "server.port";
        String CONTEXT_DESCRIPTOR = "context.descriptor";

    }

    Main() { }
}
