package handoff.support;

import fish.Fish;
import fish.FishUI;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

/**
 * AI-assisted handoff adaptation, not a recovered original helper.
 * Reuses the fixture idea in backup myTest/CollisionTest.java and WinTest.java.
 * Adds EDT execution, deterministic player setup and isolated ranking restoration.
 * Owner A maintains this shared file. Runtime verification is still pending.
 */
public abstract class AbTestBase {
    protected static final double EPS = 0.000001;

    @Rule
    public final TestRule edtAndWorkspace = (statement, description) -> new Statement() {
        @Override
        public void evaluate() throws Throwable {
            verifyWorkspace();
            if (SwingUtilities.isEventDispatchThread()) {
                statement.evaluate();
                return;
            }
            AtomicReference<Throwable> failure = new AtomicReference<>();
            SwingUtilities.invokeAndWait(() -> {
                try {
                    statement.evaluate();
                } catch (Throwable ex) {
                    failure.set(ex);
                }
            });
            if (failure.get() != null) {
                throw failure.get();
            }
        }
    };

    private static Path verifiedWorkspace() {
        String configured = System.getProperty("ab.test.workdir");
        if (configured == null || configured.trim().isEmpty()) {
            throw new IllegalStateException("Run through the A-01 Maven configuration.");
        }
        Path expected = Paths.get(configured).toAbsolutePath().normalize();
        Path actual = Paths.get("").toAbsolutePath().normalize();
        if (!actual.equals(expected)
                || !actual.endsWith(Paths.get("target", "ab-test-work"))) {
            throw new IllegalStateException("Refusing to run outside target/ab-test-work: " + actual);
        }
        return actual;
    }

    private static void verifyWorkspace() throws IOException {
        Path base = verifiedWorkspace();
        Path resources = base.resolve("src/main/resources/resource");
        List<String> names = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            names.add(i + ".png");
        }
        for (int i = 1; i <= 7; i++) {
            names.add("no" + i + ".png");
        }
        names.add("BG1.png");
        names.add("daoju.png");
        for (String name : names) {
            Path image = resources.resolve(name);
            assertTrue("Missing image: " + image, Files.isRegularFile(image));
            assertNotNull("Unreadable image: " + image, ImageIO.read(image.toFile()));
        }
        assertTrue("Missing copied ranking file",
                Files.isRegularFile(base.resolve("src/main/resources/record/排行榜.txt")));
    }

    protected static Fish playerAt(float x, float y) {
        Fish player = new Fish(true);
        player.setX(x);
        player.setY(y);
        player.setGrade(4);
        player.setScore(0);
        player.setState(1);
        player.setWidth(77);
        player.setHeight(53);
        player.setSpeed(9);
        player.setY_speed(9);
        player.setDirection(0);
        player.setUp(false);
        player.setDown(false);
        player.setLeft(false);
        player.setRight(false);
        return player;
    }

    protected static FishUI uiFor(Fish player) {
        FishUI ui = new FishUI();
        ui.setMyFish(player);
        ui.setFishList(new ArrayList<Fish>());
        ui.setDaoJu(null);
        ui.setM(1);
        ui.setS(30);
        return ui;
    }

    protected static byte[] rankingResource(String name) throws IOException {
        String resource = "/ab/ranking/" + name;
        try (InputStream in = AbTestBase.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IOException("Missing test fixture: " + resource);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = in.read(buffer)) != -1) {
                out.write(buffer, 0, count);
            }
            return out.toByteArray();
        }
    }

    private static List<String> rankingLines(byte[] bytes) {
        String text = new String(bytes, StandardCharsets.UTF_8)
                .replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = text.split("\n");
        assertEquals("Ranking must contain exactly three records", 3, lines.length);
        return Arrays.asList(lines);
    }

    /** Operates only on Maven's copied ranking file, then restores its exact bytes. */
    protected static final class RankingFixture implements AutoCloseable {
        private final Path path;
        private final byte[] original;

        public RankingFixture(String initialResource) throws IOException {
            path = verifiedWorkspace().resolve("src/main/resources/record/排行榜.txt");
            original = Files.readAllBytes(path);
            byte[] initial = rankingResource(initialResource);
            rankingLines(initial);
            try {
                Files.write(path, initial);
                assertArrayEquals("Fixture setup failed", initial, Files.readAllBytes(path));
            } catch (IOException | RuntimeException | Error ex) {
                try {
                    Files.write(path, original);
                } catch (IOException restoreFailure) {
                    ex.addSuppressed(restoreFailure);
                }
                throw ex;
            }
        }

        public void assertMatches(String expectedResource) throws IOException {
            assertEquals("Ranking records differ", rankingLines(rankingResource(expectedResource)),
                    rankingLines(Files.readAllBytes(path)));
        }

        @Override
        public void close() throws IOException {
            Files.write(path, original);
            assertArrayEquals("Ranking restoration failed", original, Files.readAllBytes(path));
        }
    }
}
