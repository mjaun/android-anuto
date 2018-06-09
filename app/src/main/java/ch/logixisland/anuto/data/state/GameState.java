package ch.logixisland.anuto.data.state;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameState {

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
    private List<ActiveWaveData> mActiveWaveData = new ArrayList<>();

    @ElementListUnion({
            @ElementList(entry = "plateau", inline = true, required = false, type = PlateauData.class),
            @ElementList(entry = "enemy", inline = true, required = false, type = EnemyData.class),
            @ElementList(entry = "tower", inline = true, required = false, type = TowerData.class),
    })
    private List<EntityData> mEntityData = new ArrayList<>();

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

    public List<ActiveWaveData> getActiveWaveData() {
        return Collections.unmodifiableList(mActiveWaveData);
    }

    public void addActiveWaveData(ActiveWaveData activeWaveData) {
        mActiveWaveData.add(activeWaveData);
    }

    public List<EntityData> getEntityData() {
        return Collections.unmodifiableList(mEntityData);
    }

    public void addEntityData(EntityData entityData) {
        mEntityData.add(entityData);
    }
}
