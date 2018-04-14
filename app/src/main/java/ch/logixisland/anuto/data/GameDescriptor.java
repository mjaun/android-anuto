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

import ch.logixisland.anuto.BuildConfig;
import ch.logixisland.anuto.data.entity.EnemyDescriptor;
import ch.logixisland.anuto.data.entity.EntityDescriptor;
import ch.logixisland.anuto.data.entity.MineLayerDescriptor;
import ch.logixisland.anuto.data.entity.TowerDescriptor;
import ch.logixisland.anuto.data.map.MapDescriptor;
import ch.logixisland.anuto.data.setting.GameSettings;
import ch.logixisland.anuto.data.wave.ActiveWaveDescriptor;
import ch.logixisland.anuto.data.wave.WaveDescriptor;
import ch.logixisland.anuto.data.wave.WaveDescriptorList;

public class GameDescriptor {

    @Element(name = "appVersion")
    private int mAppVersion;

    @Element(name = "settings")
    private GameSettings mGameSettings;

    @Element(name = "map")
    private MapDescriptor mMapDescriptor;

    @Element(name = "mapId")
    private String mMapId;

    @ElementList(entry = "wave", inline = true)
    private List<WaveDescriptor> mWaveDescriptors;

    @ElementList(entry = "activeWave", inline = true, required = false)
    private List<ActiveWaveDescriptor> mActiveWaveDescriptors = new ArrayList<>();

    @ElementListUnion({
            @ElementList(entry = "entity", inline = true, required = false, type = EntityDescriptor.class),
            @ElementList(entry = "enemy", inline = true, required = false, type = EnemyDescriptor.class),
            @ElementList(entry = "tower", inline = true, required = false, type = TowerDescriptor.class),
            @ElementList(entry = "mineLayer", inline = true, required = false, type = MineLayerDescriptor.class),
    })
    private List<EntityDescriptor> mEntityDescriptors = new ArrayList<>();


    public static GameDescriptor fromXml(Context context, int gameSettingsResId,
                                         int enemySettingsResId, int towerSettingsResId,
                                         int wavesResId, int mapResId, String mapId) throws Exception {
        GameDescriptor result = new GameDescriptor();
        result.mAppVersion = BuildConfig.VERSION_CODE;
        result.mGameSettings = GameSettings.fromXml(context, gameSettingsResId, enemySettingsResId, towerSettingsResId);
        result.mWaveDescriptors = WaveDescriptorList.fromXml(context, wavesResId);
        result.mMapDescriptor = MapDescriptor.fromXml(context, mapResId);
        result.mMapId = mapId;
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

    public int getAppVersion() {
        return mAppVersion;
    }

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public MapDescriptor getMapDescriptor() {
        return mMapDescriptor;
    }

    public String getMapId() {
        return mMapId;
    }

    public List<WaveDescriptor> getWaveDescriptors() {
        return mWaveDescriptors;
    }

    public List<ActiveWaveDescriptor> getActiveWaveDescriptors() {
        return mActiveWaveDescriptors;
    }

    public void clearActiveWaveDescriptors() {
        mActiveWaveDescriptors.clear();
    }

    public void addActiveWaveDescriptor(ActiveWaveDescriptor activeWaveDescriptor) {
        mActiveWaveDescriptors.add(activeWaveDescriptor);
    }

    public List<EntityDescriptor> getEntityDescriptors() {
        return mEntityDescriptors;
    }

    public void clearEntityDescriptors() {
        mEntityDescriptors.clear();
    }

    public void addEntityDescriptor(EntityDescriptor entityDescriptor) {
        mEntityDescriptors.add(entityDescriptor);
    }
}
