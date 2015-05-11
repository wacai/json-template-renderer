package com.wacai.sdk.jtr;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class JSPServlet extends HttpServlet {

    private final String jspContextPath;
    private final JsonModel jsonModel;

    public JSPServlet(String jspContextPath, JsonModel jsonModel) {
        this.jsonModel = jsonModel;
        this.jspContextPath = jspContextPath;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doRequest(req, resp);
    }

    private void doRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String path = req.getServletPath();
        final Map<String, Object> model = jsonModel.load(path, req.getQueryString());
        for (String key : model.keySet()) {
            req.setAttribute(key, model.get(key));
        }
        req.getServletContext().getContext(jspContextPath).getRequestDispatcher(path).forward(req, resp);
    }

}
