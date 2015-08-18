package ch.logixisland.anuto.game.data;

import android.util.Log;

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

import ch.logixisland.anuto.game.objects.Plateau;

@Root
public class Level {
    /*
    ------ Members ------
     */

    @Element(name="settings")
    private GameSettings mSettings = new GameSettings();

    @ElementList(name="plateaus")
    private ArrayList<Plateau> mPlateaus = new ArrayList<>();

    @ElementList(name="paths")
    private ArrayList<Path> mPaths = new ArrayList<>();

    @ElementList(name="waves")
    private ArrayList<Wave> mWaves = new ArrayList<>();

    /*
    ------ Methods ------
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
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        try {
            serialize(outStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("XML", outStream.toString());
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
