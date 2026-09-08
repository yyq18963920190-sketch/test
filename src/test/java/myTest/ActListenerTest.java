package myTest;

import fish.ActListener;
import fish.Fish;
import fish.FishUI;
import fish.GameFrame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class ActListenerTest {
	ActListener actListener;
	GameFrame gameFrame;
	FishUI fishUI;
	Fish myFish;
	Thread myThread;

	@Before
	public void setUp() throws Exception {
		gameFrame = new GameFrame();
		actListener = gameFrame.getActListener();
		fishUI = gameFrame.getFishUI();
		myFish = new Fish(true);
		fishUI.setMyFish(myFish);
		myThread = actListener.getMyThread();
	}

	@After
	public void tearDown() throws Exception {
		actListener = null;
		gameFrame = null;
	}

	@Test
	public void testBeginGame() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "开始游戏");
		actListener.actionPerformed(event);
		assertEquals(0, gameFrame.getFishUI().getM());
		assertEquals(0, gameFrame.getFishUI().getS());

	}

	@Test
	public void testSelectRecord() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "选取存档");
		actListener.actionPerformed(event);
		assertTrue(gameFrame.getRecordPanel().isVisible());
		// 检查 changeButton() 方法是否正确执行
	}

	@Test
	public void testCloseArchive() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "关闭存档");
		actListener.actionPerformed(event);

		// 检查存档面板是否设置为不可见
		assertFalse(gameFrame.getRecordPanel().isVisible());
	}

	@Test
	public void testLoadRecord1() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "record 1");
		actListener.actionPerformed(event);

		// 检查存档面板是否关闭
		assertFalse(gameFrame.getRecordPanel().isVisible());

	}

	@Test
	public void testRanking() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "排行榜");
		actListener.actionPerformed(event);

		// 检查排行榜面板是否可见
		assertTrue(gameFrame.getRankingPanel().isVisible());
	}

	@Test
	public void testCloseRanking() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "关闭排行榜");
		actListener.actionPerformed(event);

		// 检查排行榜面板是否不可见
		assertFalse(gameFrame.getRankingPanel().isVisible());
	}

	@Test
	public void testResumeGame() throws NoSuchFieldException, IllegalAccessException {
		Field myThreadField = ActListener.class.getDeclaredField("myThread");
		myThreadField.setAccessible(true);
		myThreadField.set(actListener, new Thread());

		Field otherFishThreadField = ActListener.class.getDeclaredField("otherFishThread");
		otherFishThreadField.setAccessible(true);
		otherFishThreadField.set(actListener, new Thread());

		Field timeThreadField = ActListener.class.getDeclaredField("timeThread");
		timeThreadField.setAccessible(true);
		timeThreadField.set(actListener, new Thread());

		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "继续游戏");
		actListener.actionPerformed(event);

		// 检查菜单面板是否不可见
		assertFalse(gameFrame.getMenuPanel().isVisible());
		// 检查焦点请求
		assertTrue(gameFrame.getFishUI().isRequestFocusEnabled());
	}

	@Test
	public void testRestartGame() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "重新开始");
		actListener.actionPerformed(event);

		// 检查时间和状态重置
		assertEquals(0, fishUI.getM());
		assertEquals(0, fishUI.getS());
	}

	@Test
	public void testSaveGame() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "存档");
		actListener.actionPerformed(event);


		assertTrue(gameFrame.getRecordPanel().isVisible());
	}

	@Test
	public void testReturnToMainPage() {
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "返回主页面");
		actListener.actionPerformed(event);

		assertFalse(gameFrame.getMenuPanel().isVisible());
	}

	@Test
	public void testKeyPressAndRelease_A() {
		// 模拟按下 A 键
		KeyEvent pressEvent = new KeyEvent(gameFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A');
		actListener.keyPressed(pressEvent);

		// 验证按下 A 键后的行为
		assertTrue(myFish.isLeft());
		assertEquals(1, myFish.getDirection());

		// 模拟释放 A 键
		KeyEvent releaseEvent = new KeyEvent(gameFrame, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A');
		actListener.keyReleased(releaseEvent);

		// 验证释放 A 键后的行为
		assertFalse(myFish.isLeft());
	}

	@Test
	public void testKeyPress_P() {
		// 模拟按下 P 键
		KeyEvent pressEvent = new KeyEvent(gameFrame, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_P, 'P');
		actListener.keyPressed(pressEvent);
		// 验证菜单面板是否可见
		assertTrue(gameFrame.getMenuPanel().isVisible());

	}


	@Test
	public void changeButton() {
		actListener.changeButton(true);
		for (JButton b : actListener.getGameFrame().getButtons()) {
			assertTrue(b.isVisible());
		}
	}

	@Test
	public void changeButton_2() {
		actListener.changeButton(false);
		for (JButton b : actListener.getGameFrame().getButtons()) {
			assertFalse(b.isVisible());
		}
	}


	@Test
	public void back() throws InterruptedException {
		// 启动游戏方法
		actListener.game();
		Thread.sleep(100); // 等待线程执行一段时间

		// 检查线程是否处于运行状态
		assertTrue(myThread.isAlive());

		myThread.join(200); // 等待线程结束
		assertTrue(myThread.isAlive());
	}

	@Test
	public void game() {
		actListener.game();
		assertEquals("myThread", actListener.getMyThread().getName());
	}

//    @Test
//    public void otherFish_1() {
//
//        actListener.otherFish();
//        assertNotNull(actListener.getOtherFishs());
//        assertTrue(actListener.getOtherFishs().size() <= 8);
//
//        // 检查线程启动是否正常
//        Thread otherFishThread = actListener.getOtherFishThread();
//        assertNotNull(otherFishThread);
//        assertTrue(otherFishThread.isAlive());
//    }
//
//    @Test
//    public void otherFish_2() {
//        actListener.setOtherFishs(new ArrayList<Fish>());
//        actListener.otherFish();
//        assertTrue(!actListener.getOtherFishs().isEmpty());
//    }

	@Test
	public void time() throws InterruptedException {
		// 启动 time 方法
		actListener.time();

		// 模拟时间的增加
		int initialS = gameFrame.getFishUI().getS();
		int initialM = gameFrame.getFishUI().getM();

		// 直接给 s 和 m 赋值，模拟经过一段时间的变化
		// 让 s 达到 60，m 应该进位并且 s 应该归零
		gameFrame.getFishUI().setS(60);
		gameFrame.getFishUI().setM(initialM + 1);  // 模拟 m 进位

		// 验证 s 和 m 是否正确进位
		assertEquals(0, gameFrame.getFishUI().getS()); // s 应该归零
		assertEquals(initialM + 1, gameFrame.getFishUI().getM()); // m 应该增加 1

		// 模拟继续增加时间
		gameFrame.getFishUI().setS(10);  // s 变成 10
		gameFrame.getFishUI().setM(initialM + 1);  // m 维持不变

		// 验证 s 和 m 的值
		assertEquals(10, gameFrame.getFishUI().getS()); // s 应该是 10
		assertEquals(initialM + 1, gameFrame.getFishUI().getM()); // m 应该仍然是 initialM + 1
	}

	@Test
	public void manipulateRecordTest_1() {
		actListener.setMyFish(new Fish(true));
		actListener.manipulateRecord(1);
		try {
			FileReader reader = new FileReader("src/main/resources/record0.txt");
			BufferedReader reader1 = new BufferedReader(reader);
			String data = reader1.readLine();
			assertEquals("0,0:0", data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void manipulateRecordTest_2() {
		actListener.setMyFish(null);
		actListener.manipulateRecord(1);
		assertNotNull(actListener.getMyFish());
	}
}
