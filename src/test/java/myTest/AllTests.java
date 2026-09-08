package myTest;

import fish.ActListener;
import fish.GameFrame;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ActListenerTest.class,
        CollisionDTest.class,
        CollisionTest.class,
        DaoJuTest.class,
        FishTest.class,
        FishUITest.class,
        FishUiOtherTest.class,
        GameFrame.class,
        UpgradeTest.class,
        ImagePoolTest.class,
        WinTest.class
})
public class AllTests {
}
