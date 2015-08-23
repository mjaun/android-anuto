package ch.logixisland.anuto;

import android.app.Application;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AnutoApp extends Application {

    public AnutoApp() {
        final Thread.UncaughtExceptionHandler defaultUEH = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                AnutoApp.this.uncaughtException(thread, ex);
                defaultUEH.uncaughtException(thread, ex);
            }
        });
    }

    private void uncaughtException(Thread thread, Throwable ex) {
        DateFormat df = new SimpleDateFormat("yyyy-dd-MM_HH-mm-ss");
        Date now = Calendar.getInstance().getTime();

        String fileName = String.format("anuto-crash-%s.log", df.format(now));
        File logFile = new File(Environment.getExternalStorageDirectory(), fileName);
        String[] cmd = new String[] { "logcat", "-d", "-f", logFile.getAbsolutePath() };

        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
