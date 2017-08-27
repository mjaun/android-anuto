package ch.logixisland.anuto.data.game;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.data.SerializerFactory;

public class GameDescriptorRoot {

    @Element(name = "map")
    private String mMap;

    @Element(name = "tickCount")
    private int mTickCount;

    @Element(name = "lives")
    private int mLives;

    @Element(name = "credits")
    private int mCredits;

    @Element(name = "creditsEarned")
    private int mCreditsEarned;

    @Element(name = "waveNumber")
    private int mWaveNumber;

    @ElementList(name = "activeWaves", entry = "wave")
    private List<ActiveWaveDescriptor> mActiveWaveDescriptors;

    @Element(name = "nextEntityId")
    private int mNextEntityId;

    @ElementListUnion({
            @ElementList(name = "entity", entry = "enemy", type = EnemyDescriptor.class),
            @ElementList(name = "entity", entry = "tower", type = TowerDescriptor.class),
    })
    private List<EntityDescriptor> mEntityDescriptors = new ArrayList<>();

    public static GameDescriptorRoot fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(GameDescriptorRoot.class, stream);
    }

    public void toXml(OutputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        serializer.write(this, stream);
    }

    public String getMap() {
        return mMap;
    }

    public void setMap(String map) {
        mMap = map;
    }

    public int getTickCount() {
        return mTickCount;
    }

    public void setTickCount(int tickCount) {
        mTickCount = tickCount;
    }

    public int getLives() {
        return mLives;
    }

    public void setLives(int lives) {
        mLives = lives;
    }

    public int getCredits() {
        return mCredits;
    }

    public void setCredits(int credits) {
        mCredits = credits;
    }

    public int getCreditsEarned() {
        return mCreditsEarned;
    }

    public void setCreditsEarned(int creditsEarned) {
        mCreditsEarned = creditsEarned;
    }

    public int getWaveNumber() {
        return mWaveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        mWaveNumber = waveNumber;
    }

    public List<ActiveWaveDescriptor> getActiveWaveDescriptors() {
        return mActiveWaveDescriptors;
    }

    public void addActiveWaveDescriptor(ActiveWaveDescriptor activeWaveDescriptor) {
        mActiveWaveDescriptors.add(activeWaveDescriptor);
    }

    public List<EntityDescriptor> getEntityDescriptors() {
        return mEntityDescriptors;
    }

    public void addEntityDescriptor(EntityDescriptor entityDescriptor) {
        mEntityDescriptors.add(entityDescriptor);
    }

    public int getNextEntityId() {
        return mNextEntityId;
    }

    public void setNextEntityId(int nextEntityId) {
        mNextEntityId = nextEntityId;
    }
}
