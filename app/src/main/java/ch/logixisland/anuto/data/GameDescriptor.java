package ch.logixisland.anuto.data;

import android.content.res.Resources;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.Serializer;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.BuildConfig;
import ch.logixisland.anuto.data.entity.EnemyDescriptor;
import ch.logixisland.anuto.data.entity.EntityDescriptor;
import ch.logixisland.anuto.data.entity.PlateauDescriptor;
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

    @ElementList(entry = "wave", inline = true)
    private List<WaveDescriptor> mWaveDescriptors;

    @ElementList(entry = "activeWave", inline = true, required = false)
    private List<ActiveWaveDescriptor> mActiveWaveDescriptors = new ArrayList<>();

    @ElementListUnion({
            @ElementList(entry = "plateau", inline = true, required = false, type = PlateauDescriptor.class),
            @ElementList(entry = "enemy", inline = true, required = false, type = EnemyDescriptor.class),
            @ElementList(entry = "tower", inline = true, required = false, type = TowerDescriptor.class),
    })
    private List<EntityDescriptor> mEntityDescriptors = new ArrayList<>();


    public static GameDescriptor fromXml(Serializer serializer, Resources resources, int gameSettingsResId,
                                         int enemySettingsResId, int towerSettingsResId,
                                         int wavesResId, int mapResId, String mapId) throws Exception {
        GameDescriptor result = new GameDescriptor();
        result.mAppVersion = BuildConfig.VERSION_CODE;
        result.mGameSettings = GameSettings.fromXml(serializer, resources, gameSettingsResId, enemySettingsResId, towerSettingsResId);
        result.mWaveDescriptors = WaveDescriptorList.fromXml(serializer, resources, wavesResId);
        result.mMapDescriptor = MapDescriptor.fromXml(serializer, resources, mapResId, mapId);
        return result;
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
