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
    private GameSettings mGameSettings;

    @Element(name = "map")
    private MapDescriptor mMapDescriptor;

    @Element(name = "waves")
    private List<WaveDescriptor> mWaveDescriptors;

    @ElementList(name = "activeWaves", entry = "wave")
    private List<ActiveWaveDescriptor> mActiveWaves = new ArrayList<>();

    @ElementListUnion({
            @ElementList(name = "entity", entry = "enemy", type = EnemyDescriptor.class),
            @ElementList(name = "entity", entry = "tower", type = TowerDescriptor.class),
    })
    private List<EntityDescriptor> mEntityDescriptors = new ArrayList<>();

    @Element(name = "mapId")
    private String mMapId;

    public static GameDescriptor fromXml(Context context, int gameSettingsResId,
                                         int enemySettingsResId, int towerSettingsResId,
                                         int wavesResId, int mapResId, String mapId) throws Exception {
        GameDescriptor result = new GameDescriptor();
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

    public GameSettings getGameSettings() {
        return mGameSettings;
    }

    public MapDescriptor getMapDescriptor() {
        return mMapDescriptor;
    }

    public List<WaveDescriptor> getWaveDescriptors() {
        return mWaveDescriptors;
    }

    public List<ActiveWaveDescriptor> getActiveWaves() {
        return mActiveWaves;
    }

    public void addActiveWaveDescriptor(ActiveWaveDescriptor activeWaveDescriptor) {
        mActiveWaves.add(activeWaveDescriptor);
    }

    public List<EntityDescriptor> getEntityDescriptors() {
        return mEntityDescriptors;
    }

    public void addEntityDescriptor(EntityDescriptor entityDescriptor) {
        mEntityDescriptors.add(entityDescriptor);
    }

    public String getMapId() {
        return mMapId;
    }
}
