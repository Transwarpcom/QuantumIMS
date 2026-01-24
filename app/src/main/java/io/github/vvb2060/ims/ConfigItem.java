package io.github.vvb2060.ims;

import java.util.Arrays;

public class ConfigItem {
    public String key;
    public Object defaultValue;
    public Object overrideValue;
    public boolean isOverridden;

    public ConfigItem(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getValueString() {
        Object val = isOverridden ? overrideValue : defaultValue;
        if (val instanceof int[]) {
            return Arrays.toString((int[]) val);
        } else if (val instanceof String[]) {
            return Arrays.toString((String[]) val);
        }
        return String.valueOf(val);
    }

    public Object parse(String s) throws Exception {
        if (defaultValue instanceof Boolean) {
            return Boolean.parseBoolean(s);
        } else if (defaultValue instanceof Integer) {
            return Integer.parseInt(s);
        } else if (defaultValue instanceof Long) {
            return Long.parseLong(s);
        } else if (defaultValue instanceof Double) {
            return Double.parseDouble(s);
        } else if (defaultValue instanceof String) {
            return s;
        } else if (defaultValue instanceof int[]) {
            String[] parts = s.replace("[", "").replace("]", "").split(",");
            int[] result = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                result[i] = Integer.parseInt(parts[i].trim());
            }
            return result;
        } else if (defaultValue instanceof String[]) {
            String[] parts = s.replace("[", "").replace("]", "").split(",");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }
            return parts;
        }
        return s;
    }
}
