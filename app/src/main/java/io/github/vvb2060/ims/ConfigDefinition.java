package io.github.vvb2060.ims;

public class ConfigDefinition {
    public String key;
    public int titleRes;
    public int descRes;
    public int iconRes;
    public Object defaultValue;

    public ConfigDefinition(String key, int titleRes, int descRes, int iconRes, Object defaultValue) {
        this.key = key;
        this.titleRes = titleRes;
        this.descRes = descRes;
        this.iconRes = iconRes;
        this.defaultValue = defaultValue;
    }
}
