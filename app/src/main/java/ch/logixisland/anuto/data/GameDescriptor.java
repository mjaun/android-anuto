package ch.logixisland.anuto.data;

import android.content.Context;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.data.entity.EnemyDescriptor;
import ch.logixisland.anuto.data.entity.EntityDescriptor;
import ch.logixisland.anuto.data.entity.TowerDescriptor;
import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.wave.ActiveWaveDescriptor;
import ch.logixisland.anuto.data.wave.WaveDescriptor;
import ch.logixisland.anuto.data.wave.WaveDescriptorList;

public class GameDescriptor {

    @Element(name = "settings")
    private GameSettings mSettings;

    @Element(name = "map")
    private MapDescriptor mMap;

    @Element(name = "waves")
    private List<WaveDescriptor> mWaves;

    @ElementList(name = "activeWaves", entry = "wave")
    private List<ActiveWaveDescriptor> mActiveWaves = new ArrayList<>();

    @ElementListUnion({
            @ElementList(name = "entity", entry = "enemy", type = EnemyDescriptor.class),
            @ElementList(name = "entity", entry = "tower", type = TowerDescriptor.class),
    })
    private List<EntityDescriptor> mEntities = new ArrayList<>();

    public static GameDescriptor fromXml(Context context, int gameSettingsResId,
                                         int enemySettingsResId, int towerSettingsResId,
                                         int mapResId, int wavesResId) throws Exception {
        GameDescriptor result = new GameDescriptor();
        result.mSettings = GameSettings.fromXml(context, gameSettingsResId, enemySettingsResId, towerSettingsResId);
        result.mMap = MapDescriptor.fromXml(context, mapResId);
        result.mWaves = WaveDescriptorList.fromXml(context, wavesResId);
        return result;
    }

    public static GameDescriptor fromXml(InputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        return serializer.read(GameDescriptor.class, stream);
    }

    public void toXml(OutputStream stream) throws Exception {
        Serializer serializer = new SerializerFactory().createSerializer();
        serializer.write(this, stream);
    }

    public String getMapId() {
        return mMapId;
    }

    public void setMapId(String mapId) {
        mMapId = mapId;
    }

    public int getTickCount() {
        return mTickCount;
    }

    public void setTickCount(int tickCount) {
        mTickCount = tickCount;
    }

    public int getNextEntityId() {
        return mNextEntityId;
    }

    public void setNextEntityId(int nextEntityId) {
        mNextEntityId = nextEntityId;
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

    public List<ActiveWaveDescriptor> getActiveWaves() {
        return mActiveWaves;
    }

    public void addActiveWaveDescriptor(ActiveWaveDescriptor activeWaveDescriptor) {
        mActiveWaves.add(activeWaveDescriptor);
    }

    public List<EntityDescriptor> getEntities() {
        return mEntities;
    }

    public void addEntityDescriptor(EntityDescriptor entityDescriptor) {
        mEntities.add(entityDescriptor);
    }

    @Element(name = "lives") // TODO remove
    private int mLives;

    @Element(name = "credits") // TODO remove
    private int mCredits;

    @Element(name = "creditsEarned") // TODO remove
    private int mCreditsEarned;

    @Element(name = "waveNumber") // TODO remove
    private int mWaveNumber;

    @Element(name = "tickCount") // TODO remove
    private int mTickCount;

    @Element(name = "nextEntityId") // TODO remove
    private int mNextEntityId;

    @Element(name = "mapId") // TODO remove
    private String mMapId;
}
