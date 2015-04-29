package com.wacai.sdk.jtr;

import com.alibaba.fastjson.JSONReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InjectModelServlet extends HttpServlet {

    private final File   jsonDir;
    private final String jspContextPath;
    private final Logger logger;

    public InjectModelServlet(File jsonDir, String jspContextPath) {
        this.jsonDir = jsonDir;
        this.jspContextPath = jspContextPath;
        logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String path = req.getServletPath();
        String name = get(req.getQueryString(), "m", base(path));
        final File file = new File(jsonDir, name + ".json");
        if (file.exists()) {
            setAttributes(req, new JSONReader(new FileReader(file)));
        } else {
            logger.warn("Missing model file {}", file);
        }
        req.getServletContext().getContext(jspContextPath).getRequestDispatcher(path).forward(req, resp);
    }

    @SuppressWarnings({"unchecked"})
    private void setAttributes(HttpServletRequest req, JSONReader reader) {
        final Map<String, String> model = (Map<String, String>) reader.readObject(new HashMap<String, String>());
        logger.info("Inject model {}", model);
        try {
            for (String key : model.keySet()) {
                req.setAttribute(key, model.get(key));
            }
        } finally {
            reader.close();
        }
    }

    static String base(String path) {
        return path.replace(".jsp", "");
    }

    static String get(String queryString, String key, String defaultValue) {
        if (queryString == null) return defaultValue;
        final int i = queryString.indexOf(key);
        if (i < 0) return defaultValue;
        final int j = queryString.indexOf('&', i);
        return queryString.substring(i + key.length() + 1, j < 0 ? queryString.length() : j);
    }
}
