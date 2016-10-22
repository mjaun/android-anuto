package ch.logixisland.anuto;

import android.app.Application;

public class AnutoApplication extends Application {

    Thread.UncaughtExceptionHandler defaultHandler;

    public AnutoApplication() {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                AnutoApplication.this.uncaughtException(thread, ex);
                defaultHandler.uncaughtException(thread, ex);
            }
        });
    }

    private void uncaughtException(Thread thread, Throwable ex) {

    }
}
