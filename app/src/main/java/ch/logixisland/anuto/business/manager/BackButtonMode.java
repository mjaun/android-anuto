package ch.logixisland.anuto.business.manager;

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
