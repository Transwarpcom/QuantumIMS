package io.github.vvb2060.ims;

public class ConfigDefinition {
    public String key;
    public String title; // Can be resource ID or string
    public String description;
    public int iconRes;
    public Object defaultValue;

    public ConfigDefinition(String key, String title, String description, int iconRes, Object defaultValue) {
        this.key = key;
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
        this.defaultValue = defaultValue;
    }
}
