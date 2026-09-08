package fish;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class GameFrame extends JFrame {
	private FishUI fishUI;

	private JPanel recordPanel;     //存档面板
	private JPanel rankingPanel;    //排行榜面板
	private JPanel menuPanel;

	private JLabel[] textLabel = new JLabel[6];
	private JButton[] buttons = new JButton[3];

	private ActListener actListener = new ActListener(this);
//    private ImagePool imagePool = new ImagePool();

	public GameFrame() {
		this.setTitle("大鱼吃小鱼");
//        this.setLocationRelativeTo(null);  //居中显示
		this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(0, 0, 1900, 1000);

		Font f = new Font("隶书", Font.PLAIN, 30);
		UIManager.put("Label.font", f);
		UIManager.put("Button.font", f);

		//存档面板
		setRecordPanel();
		this.add(recordPanel);

		//排行榜面板
		setRankingPanel();
		this.add(rankingPanel);

		//菜单界面
		setMenuPanel();
		this.add(menuPanel);

		JButton startbt = new JButton("开始游戏");
		JButton recordbt = new JButton("选取存档");
		JButton rankingbt = new JButton("排行榜");
		buttons[0] = startbt;
		buttons[1] = recordbt;
		buttons[2] = rankingbt;
		for (JButton b : buttons) {
			this.add(b);
		}
		startbt.setBounds(500, 700, 200, 70);
		recordbt.setBounds(800, 700, 200, 70);
		rankingbt.setBounds(1100, 700, 200, 70);
		startbt.addActionListener(actListener);
		recordbt.addActionListener(actListener);
		rankingbt.addActionListener(actListener);

		//分数 时间 面板
//        setScoreAndTime();
//        this.add(textLabel[0]);
//        this.add(textLabel[1]);

		fishUI = new FishUI();
		fishUI.addKeyListener(actListener);
		for (JButton b : fishUI.getGameButton()) {
			b.addActionListener(actListener);
		}

//        fishUI.add(textLabel[0]);
//        fishUI.add(textLabel[1]);
		this.add(fishUI);

		this.setVisible(true);
		recordPanel.setVisible(false);
		rankingPanel.setVisible(false);
		menuPanel.setVisible(false);
	}

	public static void main(String[] args) {
		ImagePool imagePool = new ImagePool();
		GameFrame frame = new GameFrame();
		frame.setRecord();
		frame.getMenuPanel().getGraphics().drawImage(imagePool.getImage(2), 10, 10, 30, 30, null);
	}

	public FishUI getFishUI() {
		return this.fishUI;
	}

	public void setFishUI(FishUI fishUI) {
		this.fishUI = fishUI;
	}

	public JPanel getRecordPanel() {
		return recordPanel;
	}

	public void setRecordPanel(JPanel recordPanel) {
		this.recordPanel = recordPanel;
	}

	public JPanel getRankingPanel() {
		return rankingPanel;
	}

	public void setRankingPanel(JPanel rankingPanel) {
		this.rankingPanel = rankingPanel;
	}

	public JButton[] getButtons() {
		return buttons;
	}

	public void setButtons(JButton[] buttons) {
		this.buttons = buttons;
	}

	public ActListener getActListener() {
		return actListener;
	}

	public void setActListener(ActListener actListener) {
		this.actListener = actListener;
	}

	public JLabel[] getTextLabel() {
		return textLabel;
	}

	public void setTextLabel(JLabel[] textLabel) {
		this.textLabel = textLabel;
	}

	public JPanel getMenuPanel() {
		return menuPanel;
	}

//    public void setScoreAndTime(){
//        JLabel score = new JLabel();
//        JLabel time = new JLabel();
//        textLabel[0] = score;
//        textLabel[1] = time;
//        score.setBounds(20,20,200,100);
//        time.setBounds(250,20,200,100);
//        score.setForeground(Color.white);
//        score.setOpaque(true);
//        time.setForeground(Color.white);
//        time.setOpaque(true);
//    }

	public void setMenuPanel(JPanel menuPanel) {
		this.menuPanel = menuPanel;
	}

	public void setRecordPanel() {
		JButton record1 = new JButton("record 1");
		JButton record2 = new JButton("record 2");
		JButton record3 = new JButton("record 3");
		JButton recordBack = new JButton("关闭存档");
		record1.setBounds(400, 100, 180, 70);
		record2.setBounds(400, 250, 180, 70);
		record3.setBounds(400, 400, 180, 70);
		recordBack.setBounds(200, 500, 200, 70);
		record1.addActionListener(actListener);
		record2.addActionListener(actListener);
		record3.addActionListener(actListener);
		recordBack.addActionListener(actListener);
		JLabel label1 = new JLabel("");
		JLabel label2 = new JLabel("");
		JLabel label3 = new JLabel("");
		textLabel[0] = label1;
		textLabel[1] = label2;
		textLabel[2] = label3;
		label1.setBounds(30, 100, 370, 70);
		label2.setBounds(30, 250, 370, 70);
		label3.setBounds(30, 400, 370, 70);
		recordPanel = new JPanel();
		recordPanel.setBounds(600, 100, 600, 600);
		recordPanel.setLayout(null);
		recordPanel.add(record1);
		recordPanel.add(record2);
		recordPanel.add(record3);
		recordPanel.add(recordBack);
		recordPanel.add(label1);
		recordPanel.add(label2);
		recordPanel.add(label3);
//        recordPanel.setOpaque(false);
	}

	public void setRankingPanel() {
		rankingPanel = new JPanel();
		rankingPanel.setBounds(600, 100, 600, 600);
		rankingPanel.setLayout(null);
		JLabel label1 = new JLabel("第一名");
		JLabel label2 = new JLabel("第二名");
		JLabel label3 = new JLabel("第三名");
		textLabel[3] = label1;
		textLabel[4] = label2;
		textLabel[5] = label3;
		JButton rankingBack = new JButton("关闭排行榜");
		rankingBack.setBounds(200, 500, 200, 70);
		rankingBack.addActionListener(actListener);
		label1.setBounds(150, 100, 370, 70);
		label2.setBounds(150, 250, 370, 70);
		label3.setBounds(150, 400, 370, 70);
		rankingPanel.add(label1);
		rankingPanel.add(label2);
		rankingPanel.add(label3);
		rankingPanel.add(rankingBack);
	}

	public void setMenuPanel() {
		menuPanel = new JPanel();
		menuPanel.setBounds(700, 100, 400, 600);
		menuPanel.setLayout(null);
		JButton continueButton = new JButton("继续游戏");
		JButton restartButton = new JButton("重新开始");
		JButton record = new JButton("存档");
		JButton back = new JButton("返回主页面");
		continueButton.setBounds(100, 100, 200, 70);
		restartButton.setBounds(100, 200, 200, 70);
		record.setBounds(100, 300, 200, 70);
		back.setBounds(100, 400, 200, 70);
		continueButton.addActionListener(actListener);
		restartButton.addActionListener(actListener);
		record.addActionListener(actListener);
		back.addActionListener(actListener);

		menuPanel.add(continueButton);
		menuPanel.add(restartButton);
		menuPanel.add(record);
		menuPanel.add(back);

	}

	public void setRanking() {
		int[][] data = fishUI.getRangkingFile();
		for (int i = 0; i < 3; i++) {
			if (data[i][0] != 0 || data[i][1] != 0) {
				if (data[i][1] < 10) {
					textLabel[i + 3].setText(data[i][0] + ":0" + data[i][1]);
				} else textLabel[i + 3].setText(data[i][0] + ":" + data[i][1]);
			} else textLabel[i + 3].setText("暂无记录");
		}
	}

	public void setRecord() {
		for (int i = 0; i < 3; i++) {
			int[] data = new int[3];
			try {
				data = fishUI.getRecordData(new FileReader("src/main/resources/record" + i + ".txt"));
			} catch (FileNotFoundException fileNotFoundException) {
				fileNotFoundException.printStackTrace();
			} catch (NoSuchFieldException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			if (data[2] != 0) {
				textLabel[i].setText("score " + data[0] + ",time " + data[1] + ":" + data[2]);
			} else textLabel[i].setText("暂无记录");

		}
	}
}

