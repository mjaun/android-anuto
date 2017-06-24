package ch.logixisland.anuto.business.manager;

import java.util.HashMap;
import java.util.Map;

public enum BackButtonMode {
    DISABLED,
    ENABLED,
    TWICE;

    public static BackButtonMode fromString(String code) {
        try {
            return valueOf(code);
        } catch (Exception e) {
            return DISABLED;
        }
    }

}
