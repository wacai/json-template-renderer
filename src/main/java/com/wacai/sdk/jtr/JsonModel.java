package com.wacai.sdk.jtr;

import com.alibaba.fastjson.JSONReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JsonModel implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final File jsonDir;

    public JsonModel(File jsonDir) { this.jsonDir = jsonDir;}

    @SuppressWarnings("unchecked")
    public Map<String, String> load(String path, String queryString) throws FileNotFoundException {
        String name = get(queryString, "m", RequestPath.base(path));
        final File file = new File(jsonDir, name + ".json");
        if (!file.exists()) {
            logger.warn("Missing model file {}", file);
            return new HashMap<>();
        }

        try (JSONReader reader = new JSONReader(new FileReader(file))) {
            final Map<String, String> model = (Map<String, String>) reader.readObject(new HashMap<String, String>());
            logger.info("Inject model {}", model);
            return model;
        }
    }

    static String get(String queryString, String key, String defaultValue) {
        if (queryString == null) return defaultValue;
        final int i = queryString.indexOf(key);
        if (i < 0) return defaultValue;
        final int j = queryString.indexOf('&', i);
        return queryString.substring(i + key.length() + 1, j < 0 ? queryString.length() : j);
    }
}