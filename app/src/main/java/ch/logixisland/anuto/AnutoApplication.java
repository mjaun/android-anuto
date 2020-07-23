package ch.logixisland.anuto;

import android.app.Application;
import android.content.Context;

public class AnutoApplication extends Application {

    private static Context appContext;
    private static AnutoApplication sInstance;
    private GameFactory mGameFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        sInstance = this;
        mGameFactory = new GameFactory(getApplicationContext());
    }

    public static Context getContext() {
        return appContext;
    }

    public static AnutoApplication getInstance() {
        return sInstance;
    }

    public GameFactory getGameFactory() {
        return mGameFactory;
    }

}
