package ch.logixisland.anuto;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.logixisland.anuto.view.game.GameActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntegrationTest {

    @Rule
    public ActivityTestRule<GameActivity> mActivityRule = new ActivityTestRule<>(GameActivity.class);

    @Test
    public void integrationTest() {
        GameSimulator simulator = new DefaultGameSimulator(AnutoApplication.getInstance().getGameFactory());
        simulator.startSimulation();
        simulator.waitForFinished();
    }


}
