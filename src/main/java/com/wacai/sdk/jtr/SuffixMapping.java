package com.wacai.sdk.jtr;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuffixMapping extends HandlerWrapper {

    private final File                file;
    private final Map<String, String> map;

    public SuffixMapping(File file) {
        this.file = file;
        map = new HashMap<>();
    }

    @Override
    protected void doStart() throws Exception {
        load();
        super.doStart();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        final String suffix = RequestPath.suffix(target);
        if (!map.containsKey(suffix))
            super.handle(target, baseRequest, request, response);
        else {
            final String mapped = map.get(suffix);
            final String value = target.replace(suffix, mapped);
            baseRequest.setPathInfo(value);
            super.handle(value, baseRequest, request, response);
        }
    }

    private void load() throws IOException {
        final Path path = file.toPath();
        final List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
        for (String line : lines) {
            final String[] split = line.split("\\s+", 2);
            map.put(split[0], split[1]);
        }
    }
}
