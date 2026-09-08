package myTest;

import fish.ActListener;
import fish.FishUI;
import fish.GameFrame;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.swing.*;
import java.awt.*;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileReader;
import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class GameFrameTest {

	@Mock
	private FishUI mockFishUI;
	@Mock
	private JPanel mockRecordPanel;
	@Mock
	private JPanel mockRankingPanel;
	@Mock
	private JPanel mockMenuPanel;
	@Mock
	private ActListener mockActListener;
	@InjectMocks
	private GameFrame gameFrameUnderTest;

	@Before
    public void setUp() {
        gameFrameUnderTest = new GameFrame();
        gameFrameUnderTest.setFishUI(mockFishUI);
        gameFrameUnderTest.setRecordPanel(mockRecordPanel);
        gameFrameUnderTest.setRankingPanel(mockRankingPanel);
        gameFrameUnderTest.setMenuPanel(mockMenuPanel);
        gameFrameUnderTest.setActListener(mockActListener);
    }
	@Test
    public void testGameFrameInitialization() {
		gameFrameUnderTest = new GameFrame();
        // 检查窗口标题
        assertEquals("大鱼吃小鱼", gameFrameUnderTest.getTitle());

        // 检查窗口尺寸
        assertEquals(1900, gameFrameUnderTest.getWidth());
        assertEquals(1000, gameFrameUnderTest.getHeight());

        // 检查窗口关闭操作
        assertEquals(JFrame.EXIT_ON_CLOSE, gameFrameUnderTest.getDefaultCloseOperation());

        // 检查按钮是否已初始化
        JButton[] buttons = gameFrameUnderTest.getButtons();
        assertNotNull(buttons);
        assertEquals(3, buttons.length);
        assertEquals("开始游戏", buttons[0].getText());
        assertEquals("选取存档", buttons[1].getText());
        assertEquals("排行榜", buttons[2].getText());

        // 检查各个面板是否已添加到界面
        JPanel recordPanel = gameFrameUnderTest.getRecordPanel();
        JPanel rankingPanel = gameFrameUnderTest.getRankingPanel();
        JPanel menuPanel = gameFrameUnderTest.getMenuPanel();
        assertNotNull(recordPanel);
        assertNotNull(rankingPanel);
        assertNotNull(menuPanel);
        assertTrue(gameFrameUnderTest.isAncestorOf(recordPanel));
        assertTrue(gameFrameUnderTest.isAncestorOf(rankingPanel));
        assertTrue(gameFrameUnderTest.isAncestorOf(menuPanel));

        // 检查各个面板的可见性
        assertFalse(recordPanel.isVisible());
        assertFalse(rankingPanel.isVisible());
        assertFalse(menuPanel.isVisible());

        // 检查 FishUI 是否添加成功
        assertNotNull(gameFrameUnderTest.getFishUI());
        assertTrue(gameFrameUnderTest.isAncestorOf(gameFrameUnderTest.getFishUI()));
    }
 @Test
    public void testSetRecordPanel() {
		gameFrameUnderTest.setRecordPanel();
        // 获取设置后的 recordPanel
        JPanel recordPanel = gameFrameUnderTest.getRecordPanel();
        assertNotNull("recordPanel 应该被初始化", recordPanel);
        assertEquals(600, recordPanel.getWidth());
        assertEquals(600, recordPanel.getHeight());
        assertEquals(600, recordPanel.getX());
        assertEquals(100, recordPanel.getY());

        // 验证按钮是否添加成功
        JButton record1 = (JButton) recordPanel.getComponent(0);
        JButton record2 = (JButton) recordPanel.getComponent(1);
        JButton record3 = (JButton) recordPanel.getComponent(2);
        JButton recordBack = (JButton) recordPanel.getComponent(3);

        assertEquals("record 1", record1.getText());
        assertEquals(400, record1.getX());
        assertEquals(100, record1.getY());
        assertEquals(180, record1.getWidth());
        assertEquals(70, record1.getHeight());

        assertEquals("record 2", record2.getText());
        assertEquals(400, record2.getX());
        assertEquals(250, record2.getY());
        assertEquals(180, record2.getWidth());
        assertEquals(70, record2.getHeight());

        assertEquals("record 3", record3.getText());
        assertEquals(400, record3.getX());
        assertEquals(400, record3.getY());
        assertEquals(180, record3.getWidth());
        assertEquals(70, record3.getHeight());

        assertEquals("关闭存档", recordBack.getText());
        assertEquals(200, recordBack.getX());
        assertEquals(500, recordBack.getY());
        assertEquals(200, recordBack.getWidth());
        assertEquals(70, recordBack.getHeight());

        // 验证标签是否添加成功
        JLabel label1 = (JLabel) recordPanel.getComponent(4);
        JLabel label2 = (JLabel) recordPanel.getComponent(5);
        JLabel label3 = (JLabel) recordPanel.getComponent(6);

        assertEquals(30, label1.getX());
        assertEquals(100, label1.getY());
        assertEquals(370, label1.getWidth());
        assertEquals(70, label1.getHeight());

        assertEquals(30, label2.getX());
        assertEquals(250, label2.getY());
        assertEquals(370, label2.getWidth());
        assertEquals(70, label2.getHeight());

        assertEquals(30, label3.getX());
        assertEquals(400, label3.getY());
        assertEquals(370, label3.getWidth());
        assertEquals(70, label3.getHeight());
    }

	 @Test
    public void testSetRankingPanel() {
		gameFrameUnderTest.setRankingPanel();
        // 获取设置后的 rankingPanel
        JPanel rankingPanel = gameFrameUnderTest.getRankingPanel();
        assertNotNull("rankingPanel 应该被初始化", rankingPanel);
        assertEquals(600, rankingPanel.getWidth());
        assertEquals(600, rankingPanel.getHeight());
        assertEquals(600, rankingPanel.getX());
        assertEquals(100, rankingPanel.getY());

        // 验证标签是否添加成功
        JLabel label1 = (JLabel) rankingPanel.getComponent(0);
        JLabel label2 = (JLabel) rankingPanel.getComponent(1);
        JLabel label3 = (JLabel) rankingPanel.getComponent(2);

        assertEquals("第一名", label1.getText());
        assertEquals(150, label1.getX());
        assertEquals(100, label1.getY());
        assertEquals(370, label1.getWidth());
        assertEquals(70, label1.getHeight());

        assertEquals("第二名", label2.getText());
        assertEquals(150, label2.getX());
        assertEquals(250, label2.getY());
        assertEquals(370, label2.getWidth());
        assertEquals(70, label2.getHeight());

        assertEquals("第三名", label3.getText());
        assertEquals(150, label3.getX());
        assertEquals(400, label3.getY());
        assertEquals(370, label3.getWidth());
        assertEquals(70, label3.getHeight());

        // 验证按钮是否添加成功
        JButton rankingBack = (JButton) rankingPanel.getComponent(3);
        assertEquals("关闭排行榜", rankingBack.getText());
        assertEquals(200, rankingBack.getX());
        assertEquals(500, rankingBack.getY());
        assertEquals(200, rankingBack.getWidth());
        assertEquals(70, rankingBack.getHeight());
    }

	@Test
    public void testSetMenuPanel() {
		gameFrameUnderTest.setMenuPanel();
        // 获取设置后的 menuPanel
        JPanel menuPanel = gameFrameUnderTest.getMenuPanel();
        assertNotNull("menuPanel 应该被初始化", menuPanel);
        assertEquals(400, menuPanel.getWidth());
        assertEquals(600, menuPanel.getHeight());
        assertEquals(700, menuPanel.getX());
        assertEquals(100, menuPanel.getY());

        // 验证按钮是否添加成功
        JButton continueButton = (JButton) menuPanel.getComponent(0);
        JButton restartButton = (JButton) menuPanel.getComponent(1);
        JButton recordButton = (JButton) menuPanel.getComponent(2);
        JButton backButton = (JButton) menuPanel.getComponent(3);

        assertEquals("继续游戏", continueButton.getText());
        assertEquals(100, continueButton.getX());
        assertEquals(100, continueButton.getY());
        assertEquals(200, continueButton.getWidth());
        assertEquals(70, continueButton.getHeight());

        assertEquals("重新开始", restartButton.getText());
        assertEquals(100, restartButton.getX());
        assertEquals(200, restartButton.getY());
        assertEquals(200, restartButton.getWidth());
        assertEquals(70, restartButton.getHeight());

        assertEquals("存档", recordButton.getText());
        assertEquals(100, recordButton.getX());
        assertEquals(300, recordButton.getY());
        assertEquals(200, recordButton.getWidth());
        assertEquals(70, recordButton.getHeight());

        assertEquals("返回主页面", backButton.getText());
        assertEquals(100, backButton.getX());
        assertEquals(400, backButton.getY());
        assertEquals(200, backButton.getWidth());
        assertEquals(70, backButton.getHeight());
    }

	 @Test
    public void testSetRanking() {
		// 创建一个 FishUI 的 Mock 实例并设置返回数据
        FishUI mockFishUI = new FishUI() {
            @Override
            public int[][] getRangkingFile() {
                // 返回模拟的排名数据
                return new int[][] {
                    {0, 5},  // 第一名
                    {1, 15}, // 第二名
                    {0, 0}     // 第三名（无记录）
                };
            }
        };

        // 将 mockFishUI 注入到 GameFrame 实例中
        gameFrameUnderTest.setFishUI(mockFishUI);

        // 设置所需的标签
        gameFrameUnderTest.getTextLabel()[3] = new JLabel();
        gameFrameUnderTest.getTextLabel()[4] = new JLabel();
        gameFrameUnderTest.getTextLabel()[5] = new JLabel();
        // 调用 setRanking 方法
        gameFrameUnderTest.setRanking();

        // 验证标签内容是否符合预期
        assertEquals("0:05", gameFrameUnderTest.getTextLabel()[3].getText());
        assertEquals("1:15", gameFrameUnderTest.getTextLabel()[4].getText());
        assertEquals("暂无记录", gameFrameUnderTest.getTextLabel()[5].getText());
    }

	@Test
    public void testSetRecord() {
		FishUI mockFishUI = new FishUI() {
			int num = 0;
            @Override
            public int[] getRecordData(FileReader fileReader) {

                if (num == 0) {
					num+=1;
                    return new int[] {30, 2, 1}; // 模拟第一条记录
                } else if (num == 1) {
					num+=1;
                    return new int[] {30, 5, 2};  // 模拟第二条记录
                } else {
                    return new int[] {0, 0, 0};    // 模拟第三条记录无数据
                }
            }
        };

        // 注入 mockFishUI 实例
        gameFrameUnderTest.setFishUI(mockFishUI);

        // 初始化 textLabel 标签
        gameFrameUnderTest.getTextLabel()[0] = new JLabel();
        gameFrameUnderTest.getTextLabel()[1] = new JLabel();
        gameFrameUnderTest.getTextLabel()[2] = new JLabel();
        // 调用 setRecord 方法
        gameFrameUnderTest.setRecord();

        // 验证标签内容是否符合预期
        assertEquals("score 30,time 2:1", gameFrameUnderTest.getTextLabel()[0].getText());
        assertEquals("score 30,time 5:2", gameFrameUnderTest.getTextLabel()[1].getText());
        assertEquals("暂无记录", gameFrameUnderTest.getTextLabel()[2].getText());
    }

	@Test
	public void testGetFishUI() {
		assertThat(gameFrameUnderTest.getFishUI()).isEqualTo(mockFishUI);
	}

	@Test
	public void testGetRecordPanel() {
		assertThat(gameFrameUnderTest.getRecordPanel()).isEqualTo(mockRecordPanel);
	}

	@Test
	public void testGetRankingPanel() {
		assertThat(gameFrameUnderTest.getRankingPanel()).isEqualTo(mockRankingPanel);
	}

	@Test
	public void testButtonsGetterAndSetter() {
		final JButton[] buttons = new JButton[]{new JButton("record 1")};
		gameFrameUnderTest.setButtons(buttons);
		assertThat(gameFrameUnderTest.getButtons()).isEqualTo(buttons);
	}

	@Test
	public void testGetActListener() {
		assertThat(gameFrameUnderTest.getActListener()).isEqualTo(mockActListener);
	}

	@Test
	public void testTextLabelGetterAndSetter() {
		final JLabel[] textLabel = new JLabel[]{new JLabel("text", 0)};
		gameFrameUnderTest.setTextLabel(textLabel);
		assertThat(gameFrameUnderTest.getTextLabel()).isEqualTo(textLabel);
	}

	@Test
	public void testGetMenuPanel() {
		assertThat(gameFrameUnderTest.getMenuPanel()).isEqualTo(mockMenuPanel);
	}

}
