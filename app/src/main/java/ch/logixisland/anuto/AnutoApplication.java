package ch.logixisland.anuto;

import android.app.Application;

public class AnutoApplication extends Application {

    private static AnutoApplication sInstance;
    private GameFactory mGameFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mGameFactory = new GameFactory(getApplicationContext());
    }

    public static AnutoApplication getInstance() {
        return sInstance;
    }

    public GameFactory getGameFactory() {
        return mGameFactory;
    }

}
