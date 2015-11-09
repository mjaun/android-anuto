package ch.logixisland.anuto;

import android.app.Application;

public class AnutoApp extends Application {

    Thread.UncaughtExceptionHandler defaultUEH;

    public AnutoApp() {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                AnutoApp.this.uncaughtException(thread, ex);
                defaultUEH.uncaughtException(thread, ex);
            }
        });
    }

    private void uncaughtException(Thread thread, Throwable ex) {

    }
}
