package ch.bfh.anuto.game;

import android.util.Log;
import android.view.SurfaceHolder;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Root
public class Level {
    /*
    ------ Members ------
     */

    @Element(name="settings")
    private GameSettings mSettings;

    @ElementList(name="plateaus")
    private ArrayList<Plateau> mPlateaus;

    @ElementList(name="paths")
    private ArrayList<Path> mPaths;

    @ElementList(name="waves")
    private ArrayList<Wave> mWaves;

    /*
    ------ Constructors ------
     */

    public Level() {
        mSettings = new GameSettings();
        mPlateaus = new ArrayList<>();
        mPaths = new ArrayList<>();
        mWaves = new ArrayList<>();
    }

    /*
    ------ Public Methods ------
     */

    public GameSettings getSettings() {
        return mSettings;
    }

    public List<Plateau> getPlateaus() {
        return mPlateaus;
    }

    public List<Path> getPaths() {
        return mPaths;
    }

    public List<Wave> getWaves() {
        return mWaves;
    }

    public GameEngine createGame() {
        GameEngine ret = new GameEngine();
        ret.setGameSize(mSettings.width, mSettings.height);

        for (Plateau p : mPlateaus) {
            ret.addObject(p);
        }

        return ret;
    }

    public void startWave(GameEngine game, int idx) {
        Wave wave = mWaves.get(idx);

        for (Enemy e : wave.getEnemies()) {
            game.addObject(e);
        }
    }

    public void serialize() {
        // TODO: this is just for testing

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        try {
            serialize(outStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("XML", outStream.toString());
    }

    public void serialize(OutputStream outStream) throws Exception {
        Strategy strategy = new CycleStrategy("id", "ref");
        Serializer serializer = new Persister(strategy);
        serializer.write(this, outStream);
    }

    public static Level deserialize(InputStream inStream) throws Exception {
        Strategy strategy = new CycleStrategy("id", "ref");
        Serializer serializer = new Persister(strategy);
        return serializer.read(Level.class, inStream);
    }
}
