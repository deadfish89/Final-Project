import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Shop extends JPanel implements ActionListener{
	
	private Player player;
	private Board board;
	private LeftPanel2 leftPanel2;
	private int[] inShop;
	private final int[] price;
	private JButton[] shopItems;
	private ChampionPool champPool;
	//has the images of the champions in shop
	private ImageIcon[] items = new ImageIcon[19];
	
	public Shop(Player player, Board board, LeftPanel2 leftPanel2){
		price = new int[] {5, 5, 5, 5, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 1, 1, 1,};
		champPool = new ChampionPool(player);
		inShop = new int[5];
		shopItems = new JButton[5];
		
		this.player = player;
		this.board = board;
		this.leftPanel2 = leftPanel2;
		this.setPreferredSize(new Dimension(700,130));
		this.setLayout(new GridLayout(1,5));
		this.setBackground(Color.black);
		
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
	}
	
	public void reloadImages(){
		this.removeAll();
		this.revalidate();
		this.repaint();
		loadImages();
	}
	
	public void loadImages(){	
		for (int i=0; i<5; i++){
			System.out.println("add");
			inShop[i] = champPool.getNext();
			shopItems[i] = new JButton(items[inShop[i]]);
			this.add(shopItems[i]);
			shopItems[i].addActionListener(this);
		}
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}
	
	public void actionPerformed(ActionEvent e){
		for (int i=0; i<5; i++){
			if (e.getSource()==shopItems[i] && player.getGold()>=price[i]){
				player.spendGold(price[i]);
				leftPanel2.redisplay();
				board.summonChamp(inShop[i], 1);
			}
		}
	}
	
}
