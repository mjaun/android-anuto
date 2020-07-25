package ch.logixisland.anuto;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
        GameSimulator simulator = new DefaultGameSimulator(mActivityRule.getActivity(), AnutoApplication.getInstance().getGameFactory());
        simulator.startSimulation();
        simulator.waitForFinished();
    }


}
