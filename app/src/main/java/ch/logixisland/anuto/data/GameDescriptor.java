package ch.logixisland.anuto.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.logixisland.anuto.data.entity.EnemyDescriptor;
import ch.logixisland.anuto.data.entity.EntityDescriptor;
import ch.logixisland.anuto.data.entity.PlateauDescriptor;
import ch.logixisland.anuto.data.entity.TowerDescriptor;
import ch.logixisland.anuto.data.wave.ActiveWaveDescriptor;

public class GameDescriptor {

    @Element(name = "appVersion")
    private int mAppVersion;

    @Element(name = "mapId")
    private String mMapId;

    @Element(name = "credits")
    private int mCredits;

    @Element(name = "lives")
    private int mLives;

    @Element(name = "tickCount")
    private int mTickCount;

    @Element(name = "nextEntityId")
    private int mNextEntityId;

    @Element(name = "waveNumber")
    private int mWaveNumber;

    @Element(name = "creditsEarned")
    private int mCreditsEarned;

    @ElementList(entry = "activeWave", inline = true, required = false)
    private List<ActiveWaveDescriptor> mActiveWaveDescriptors = new ArrayList<>();

    @ElementListUnion({
            @ElementList(entry = "plateau", inline = true, required = false, type = PlateauDescriptor.class),
            @ElementList(entry = "enemy", inline = true, required = false, type = EnemyDescriptor.class),
            @ElementList(entry = "tower", inline = true, required = false, type = TowerDescriptor.class),
    })
    private List<EntityDescriptor> mEntityDescriptors = new ArrayList<>();

    public int getAppVersion() {
        return mAppVersion;
    }

    public void setAppVersion(int appVersion) {
        mAppVersion = appVersion;
    }

    public String getMapId() {
        return mMapId;
    }

    public void setMapId(String mapId) {
        mMapId = mapId;
    }

    public int getCredits() {
        return mCredits;
    }

    public void setCredits(int credits) {
        mCredits = credits;
    }

    public int getLives() {
        return mLives;
    }

    public void setLives(int lives) {
        mLives = lives;
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

    public int getWaveNumber() {
        return mWaveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        mWaveNumber = waveNumber;
    }

    public int getCreditsEarned() {
        return mCreditsEarned;
    }

    public void setCreditsEarned(int creditsEarned) {
        mCreditsEarned = creditsEarned;
    }

    public List<ActiveWaveDescriptor> getActiveWaveDescriptors() {
        return Collections.unmodifiableList(mActiveWaveDescriptors);
    }

    public void addActiveWaveDescriptor(ActiveWaveDescriptor activeWaveDescriptor) {
        mActiveWaveDescriptors.add(activeWaveDescriptor);
    }

    public List<EntityDescriptor> getEntityDescriptors() {
        return Collections.unmodifiableList(mEntityDescriptors);
    }

    public void addEntityDescriptor(EntityDescriptor entityDescriptor) {
        mEntityDescriptors.add(entityDescriptor);
    }
}
