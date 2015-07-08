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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlRemapping extends HandlerWrapper {

    private final File             file;
    private final List<Substitute> substitutes;

    public UrlRemapping(File file) {
        this.file = file;
        this.substitutes = new ArrayList<>();
    }

    @Override
    protected void doStart() throws Exception {
        load();
        super.doStart();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        for (Substitute sub : substitutes) {
            try {
                String replaced = sub.apply(target);
                baseRequest.setPathInfo(replaced);
                super.handle(replaced, baseRequest, request, response);
                return;
            } catch (NotMatchingException ignore) { }
        }
        super.handle(target, baseRequest, request, response);
    }

    void load() throws IOException {
        final List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("UTF-8"));
        for (String line : lines) {
            final String[] split = line.split("\\s+", 2);
            substitutes.add(new Substitute(Pattern.compile(split[0]), split[1]));
        }
    }

    static class Substitute {

        private final Pattern regex;
        private final String  replacement;

        public Substitute(Pattern regex, String replacement) {
            this.regex = regex;
            this.replacement = replacement;
        }

        String apply(String input) {
            final Matcher matcher = regex.matcher(input);
            if (matcher.matches()) return matcher.replaceAll(replacement);
            throw new NotMatchingException();
        }

    }

    private static class NotMatchingException extends RuntimeException {
        @Override
        public synchronized Throwable fillInStackTrace() {
            return this;
        }
    }
}
