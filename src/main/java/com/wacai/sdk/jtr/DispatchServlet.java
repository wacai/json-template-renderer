package com.wacai.sdk.jtr;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class DispatchServlet extends HttpServlet {

    private final Map<String, String> mapping;
    private final String              defaultPath;

    public DispatchServlet(Map<String, String> mapping, String defaultPath) {
        this.mapping = mapping;
        this.defaultPath = defaultPath;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String path = req.getServletPath();
        String cp = mapping.get(suffix(path));
        cp = cp == null ? defaultPath : cp;
        getServletContext().getContext(cp).getRequestDispatcher(path).forward(req, resp);
    }

    private String suffix(String path) {
        final int index = path.lastIndexOf('.');
        if (index < 0) return null;
        return path.substring(index);
    }
}
