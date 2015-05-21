package com.wacai.sdk.jtr;

import com.alibaba.fastjson.JSONObject;
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
    public Map<String, Object> load(String path, String queryString) throws FileNotFoundException {
        String name = get(queryString, "m", RequestPath.base(path));
        final File file = new File(jsonDir, name + ".json");
        if (!file.exists()) {
            logger.warn("Missing model file {}", file);
            return new HashMap<>();
        }

        try (JSONReader reader = new JSONReader(new FileReader(file))) {
            final JSONObject object = (JSONObject) reader.readObject();
            logger.info("Inject model {}", object);
            return object;
        }
    }

    static String get(String queryString, String key, String defaultValue) {
        if (queryString == null || queryString.trim().isEmpty()) return defaultValue;
        final String prefix = key + '=';
        for (String s : queryString.split("&")) {
            if (s.startsWith(prefix)) return s.substring(prefix.length());
        }
        return defaultValue;
    }
}