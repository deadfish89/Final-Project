import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 

public class SouthPanel extends JPanel implements ActionListener{
	
	private Player player;
	private Board board;
	
	public SouthPanel(Player player, Board board){
		this.player = player;
		this.board = board;
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(1000,105));
		
		this.add(new HUD(player, board), BorderLayout.SOUTH);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
	
}

class HUD extends JPanel implements ActionListener{
	
	private Player player;
	private Board board;
	private ImageIcon bg;
	private LeftPanel2 leftPanel2;
	private Shop shop;
	
	public HUD(Player player, Board board){
		this.player = player;
		this.board = board;
		leftPanel2 = new LeftPanel2(player,board);
		shop = new Shop(player, board, leftPanel2);
		this.setBackground(Color.black);
		this.setPreferredSize(new Dimension(1000,105));
		/*this.setLayout(new BorderLayout());
		this.add(shop, BorderLayout.CENTER);
		this.add(new RightPanel(),BorderLayout.EAST);
		this.add(new LeftPanel(player, leftPanel2, shop), BorderLayout.WEST);
		this.add(leftPanel2, BorderLayout.WEST);*/
		this.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
		this.add(new RightPanel(leftPanel2));
		this.add(new LeftPanel(player, leftPanel2, shop));
		this.add(leftPanel2);
		this.add(shop);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
}

class LeftPanel extends JPanel implements ActionListener{
	
	private Player player;
	private Shop shop;
	private LeftPanel2 leftPanel2;
	private JButton refresh, level;
	private JTextField xp, gold;
	
	public LeftPanel(Player player, LeftPanel2 leftPanel2, Shop shop){
		this.player = player;
		this.shop = shop;
		this.leftPanel2 = leftPanel2;
		this.setPreferredSize(new Dimension(185,105));
		this.setLayout(new GridLayout(2,1));
		this.setBackground(Color.BLACK);
		
		refresh = new JButton(new ImageIcon("Refresh.png"));
		level = new JButton(new ImageIcon("Level.png"));
		
		this.add(level);
		this.add(refresh);
	
		level.addActionListener(this);
		refresh.addActionListener(this);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);

	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==level){
			if (player.getGold()>=4&&player.getLevel()<10) {
				player.spendGold(4);
				player.gainXP(4);
				leftPanel2.redisplay();
			}
		}else if (e.getSource()==refresh){
			if (player.getGold()>=2){
				player.spendGold(2);
				shop.reloadImages();
				leftPanel2.redisplay();
			}
		}else{
			
		}
	}
}

class LeftPanel2 extends JPanel implements ActionListener{
	
	private Timer timer;
	private JButton refresh, level;
	private JLabel xpLabel, goldLabel, lvlLabel;
	private Player player;
	private Board board;
	public LeftPanel2(Player player, Board board){
		this.player = player;
		this.board = board;
		this.setPreferredSize(new Dimension(100,105));
		this.setLayout(new GridLayout(3,1));
		this.setBackground(Color.WHITE);
		timer = new Timer(100,this);
		timer.start();
		display();
	}
	
	public void display(){
		if(player.getLevel()==10){
			xpLabel = new JLabel("XP: MAX", JLabel.CENTER);
		}
		else{
			xpLabel = new JLabel("XP: " + player.getXP()+"/"+player.getLevelUp(), JLabel.CENTER);
		}
		goldLabel = new JLabel("Gold: " + player.getGold(), JLabel.CENTER);
		lvlLabel = new JLabel("Level: " + player.getLevel(), JLabel.CENTER);
		
		xpLabel.setBackground(new Color(65,105,225));
		goldLabel.setBackground(new Color(218,165,32));
		lvlLabel.setBackground(new Color(147,112,219));
		
		xpLabel.setForeground(Color.WHITE);
		goldLabel.setForeground(Color.WHITE);
		lvlLabel.setForeground(Color.WHITE);
		
		xpLabel.setOpaque(true);
		goldLabel.setOpaque(true);
		lvlLabel.setOpaque(true);
		
		this.add(lvlLabel);
		this.add(xpLabel);
		this.add(goldLabel);
		this.revalidate();
		this.repaint();
	}
	
	public void redisplay(){
		this.removeAll();
		this.revalidate();
		this.repaint();
		display();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g); 
	}
	
	//when player doesn't have enough gold to spend
	public static void flashRed(){
		
	}
	
	//when player levels up
	public static void flashWhite(){
		
	}
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==timer){
			if(board.needUpdate){
				redisplay();	
				board.needUpdate = true;	
			}
		}
	}
}

class Shop extends JPanel implements ActionListener{
	
	private Timer timer;
	private Player player;
	private Board board;
	private LeftPanel2 leftPanel2;
	private int[] inShop;
	private final int[] price;
	private JButton[] shopItems;
	private ChampionPool champPool;
	//has the images of the champions in shop
	private ImageIcon[] items = new ImageIcon[20];
	
	public Shop(Player player, Board board, LeftPanel2 leftPanel2){
		price = new int[] {5, 5, 5, 5, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1,1};
		champPool = new ChampionPool(player);
		inShop = new int[5];
		shopItems = new JButton[5];
		
		this.player = player;
		this.board = board;
		this.leftPanel2 = leftPanel2;
		this.setPreferredSize(new Dimension(610,105));
		this.setLayout(new GridLayout(1,5));
		this.setBackground(new Color(119,136,153));
		
		items[0] = new ImageIcon("annie.png");
		items[1] = new ImageIcon("wukong.png");
		items[2] = new ImageIcon("cho'gath.png");
		items[3] = new ImageIcon("vel'koz.png");
		
		items[4] = new ImageIcon("brand.png");
		items[5] = new ImageIcon("lux.png");
		items[6] = new ImageIcon("nautilus.png");
		items[7] = new ImageIcon("syndra.png");
		items[8] = new ImageIcon("varus.png");
		items[9] = new ImageIcon("veigar.png");
		items[10] = new ImageIcon("vi.png");
		items[11] = new ImageIcon("qiyana.png");
		
		items[12] = new ImageIcon("riven.png");
		items[13] = new ImageIcon("braum.png");
		items[14] = new ImageIcon("darius.png");
		items[15] = new ImageIcon("kassadin.png");
		items[16] = new ImageIcon("sivir.png");
		items[17] = new ImageIcon("jinx.png");
		items[18] = new ImageIcon("yasuo.png");
		
		loadImages();
		
		timer = new Timer(120, this);
		timer.start();
	}
	
	public void reloadImages(){
		this.removeAll();
		this.revalidate();
		this.repaint();
		loadImages();
	}
	
	public void loadImages(){	
		for (int i=0; i<5; i++){
			inShop[i] = champPool.getNext();
			shopItems[i] = new JButton(items[inShop[i]]);
			this.add(shopItems[i]);
			shopItems[i].addActionListener(this);
			if (inShop[i]<=3) shopItems[i].setBackground(new Color(221,160,221));
			else if (inShop[i]<=11) shopItems[i].setBackground(new Color(127,255,0));
			else shopItems[i].setBackground(new Color(248,248,255));
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==timer){
			if (board.needReset()){
				this.reloadImages();
				leftPanel2.redisplay();
				board.beenReset();
			}
		}
		for (int i=0; i<5; i++){
			if (e.getSource()==shopItems[i] && player.getGold()>=price[inShop[i]]){
				if (board.summonChamp(inShop[i], 1)){
					player.spendGold(price[inShop[i]]);
					leftPanel2.redisplay();
					shopItems[i].setVisible(false);
				}
			}
		}
	}
	
}

class RightPanel extends JPanel{
	private JLabel sell;
	private LeftPanel2 leftPanel2;
	public RightPanel(LeftPanel2 leftPanel2){
		this.leftPanel2 = leftPanel2;
		this.setPreferredSize(new Dimension(105,105));
		sell = new JLabel(new ImageIcon("sell.png"));
		this.add(sell);
	}
}