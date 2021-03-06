package com.wacai.sdk.jtr;

import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Main {

    static final String INCLUDE_JAR_PATTERN = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    static final String JSP_PATTERN         = ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/[^/]*taglibs.*\\.jar$";

    static {
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args.length != 2) {
            System.err.println("Arguments  : <template-dir> <json-dir>");
            System.err.println("Properties : \n  -Dserver.port=8080 \n  -Dcontext.descriptor=/path/to/web.xml\n  -Durl.rem=/path/to/file");
            System.exit(1);
        }

        final File templateDir = getDir(args, 0, "./");
        final File jsonDir = getDir(args, 1, "./");
        final int port = Integer.getInteger(Props.SERVER_PORT, 8080);
        final String descriptor = System.getProperty(Props.CONTEXT_DESCRIPTOR);
        final String urlRemappingFile = System.getProperty(Props.URL_MAPPING);
        final String whitelistFile = System.getProperty(Props.WHITE_LIST, "white.list");

        final Server server = new Server(port);

        Collection<String> whitelist = Files.readAllLines(new File(whitelistFile).toPath(), Charset.forName("UTF-8"));
        final Handler handler = new Main().handler(templateDir, descriptor, new JsonModel(jsonDir), whitelist);

        server.setHandler(urlRemappingFile == null ? handler : wrapUrlMapping(handler, new File(urlRemappingFile)));

        server.start();
        server.join();
    }

    Handler handler(File templateDir, String descriptor, JsonModel jsonModel, Collection<String> whitelist) throws Exception {
        final Route route = new Route(templateDir.getAbsolutePath(), whitelist);
        injectModel("/jsp", templateDir, descriptor, route, jsonModel);

        final ContextHandler context = new ContextHandler("/vm");
        context.setHandler(new VelocityHandler(jsonModel, templateDir));
        route.register(context);

        return requestLog(route.asHandler());
    }

    private RequestLogHandler requestLog(Handler handler) {
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(new Slf4jRequestLog());
        requestLogHandler.setHandler(handler);
        return requestLogHandler;
    }

    ContextHandler jsp(String ctx, File templateDir, String descriptor) {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File scratchDir = new File(tempDir.toString(), "jtr");
        if (!scratchDir.exists()) {
            if (!scratchDir.mkdirs()) {
                throw new RuntimeException("Unable to create scratch directory: " + scratchDir);
            }
        }
        final WebAppContext context = new WebAppContext(templateDir.getAbsolutePath(), ctx);
        final URL url = getClass().getProtectionDomain().getCodeSource().getLocation();

        if (url != null) {
            // tld could be scanned under the META-INF/ after add shade jar
            context.getMetaData().addWebInfJar(Resource.newResource(url));
            descriptor = descriptor == null ? "jar:" + url + "!/empty-web.xml" : descriptor;
            context.setDescriptor(descriptor);
        }
        context.setAttribute(INCLUDE_JAR_PATTERN, JSP_PATTERN);
        context.setAttribute("javax.servlet.context.tempdir", scratchDir);
        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        context.addBean(new ServletContainerInitializersStarter(context), true);
        context.setClassLoader(new URLClassLoader(new URL[0], getClass().getClassLoader()));
        return context;
    }

    void injectModel(String ctx, File templateDir, String descriptor, Route route, JsonModel jsonModel) {
        final String jspContextPath = "/jsp/r/";
        final ServletContextHandler context = new ServletContextHandler(null, ctx, false, false);

        context.addServlet(new ServletHolder(new JSPServlet(jspContextPath, jsonModel)), "/");
        route.register(context);
        route.register(jsp(jspContextPath, templateDir, descriptor));
    }

    static Handler wrapUrlMapping(Handler handler, File file) {
        final HandlerWrapper wrapper = new UrlRemapping(file);
        wrapper.setHandler(handler);
        return wrapper;
    }

    static List<ContainerInitializer> jspInitializers() {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        return Collections.singletonList(initializer);
    }

    static File getDir(String[] args, int index, String defaultValue) {
        final File file = args.length == 0 ? new File(defaultValue) : new File(args[index]);
        if (!file.exists() || !file.isDirectory()) throw new IllegalArgumentException(file + " is not a existed dir.");
        return file;
    }

    interface Props {
        String SERVER_PORT        = "server.port";
        String CONTEXT_DESCRIPTOR = "context.descriptor";
        String URL_MAPPING        = "url.rem";
        String WHITE_LIST         = "white.list";
    }

    Main() { }
}
