package ch.bfh.anuto.game;

import android.util.Log;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

import java.io.ByteArrayOutputStream;
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

    public void serialize() {
        // TODO: this is just for testing

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        try {
            Strategy strategy = new CycleStrategy("id", "ref");
            Serializer serializer = new Persister(strategy);
            serializer.write(this, outStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("XML", outStream.toString());
    }
}
