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
		this.setPreferredSize(new Dimension(1000,230));
		
		this.add(new Bench(player, board), BorderLayout.CENTER);
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
	
	public HUD(Player player, Board board){
		this.player = player;
		this.board = board;
		this.setBackground(Color.black);
		this.setPreferredSize(new Dimension(1000,130));
		this.setLayout(new BorderLayout());
		this.add(new leftPanel(player), BorderLayout.WEST);
		this.add(new leftPanel2(), BorderLayout.CENTER);
		this.add(new shop(player, board), BorderLayout.EAST);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
}

class leftPanel extends JPanel implements ActionListener{
	
	private Player player;
	private JButton refresh, level;
	private JTextField xp, gold;
	
	public leftPanel(Player player){
		this.player = player;
		this.setPreferredSize(new Dimension(185,130));
		this.setLayout(new GridLayout(2,1));
		this.setBackground(Color.black);
		
		refresh = new JButton(new ImageIcon("Refresh.jpg"));
		level = new JButton(new ImageIcon("Level.jpg"));
		
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
			if (player.getGold()>=4) {
				player.spendGold(4);
			}
		}else if (e.getSource()==refresh){
			if (player.getGold()>=2){
				player.spendGold(2);
			}
		}else{
			
		}
	}
}

class leftPanel2 extends JPanel{
	
	private JButton refresh, level;
	private JTextField xp, gold;
	
	public leftPanel2(){
		this.setPreferredSize(new Dimension(115,130));
		this.setLayout(new GridLayout(2,1));
		this.setBackground(Color.black);

		xp = new JTextField("XP: 200");
		gold = new JTextField("Gold: 50");
		
		this.add(xp);
		this.add(gold);
	}
	
	//when player doesn't have enough gold to spend
	public static void flashRed(){
		
	}
	
	//when player levels up
	public static void flashBlue(){
		
	}
}

class shop extends JPanel implements ActionListener{
	
	private Player player;
	private Board board;
	private JButton item1,item2, item3, item4, item5;
	//private Pool champPool;
	
	public shop(Player player, Board board){
		//champPool = new Pool(player);
		
		this.player = player;
		this.board = board;
		this.setPreferredSize(new Dimension(700,130));
		this.setLayout(new GridLayout(1,5));
		this.setBackground(Color.black);
		
		//has the images of the champions in shop
		ImageIcon[] items = new ImageIcon[19];	
		
		items[0] = scaleImage(new ImageIcon("annie.png"));
		items[1] = scaleImage(new ImageIcon("wukong.png"));
		items[2] = scaleImage(new ImageIcon("cho'gath.png"));
		items[3] = scaleImage(new ImageIcon("qiyana.png"));
		items[4] = scaleImage(new ImageIcon("brand.png"));
		items[5] = scaleImage(new ImageIcon("lux.png"));
		items[6] = scaleImage(new ImageIcon("nautilus.png"));
		items[7] = scaleImage(new ImageIcon("riven.png"));
		items[8] = scaleImage(new ImageIcon("syndra.png"));
		items[9] = scaleImage(new ImageIcon("varus.png"));
		items[10] = scaleImage(new ImageIcon("veigar.png"));
		items[11] = scaleImage(new ImageIcon("vi.png"));
		items[12] = scaleImage(new ImageIcon("braum.png"));
		items[13] = scaleImage(new ImageIcon("darius.png"));
		items[14] = scaleImage(new ImageIcon("kassadin.png"));
		items[15] = scaleImage(new ImageIcon("kha'zix.png"));
		items[16] = scaleImage(new ImageIcon("sivir.png"));
		items[17] = scaleImage(new ImageIcon("jinx.png"));
		items[18] = scaleImage(new ImageIcon("yasuo.png"));
		
		//these are filler images
		/*
		item1 = new JButton(items[champPool.getNext()]);
		item2 = new JButton(items[champPool.getNext()]);
		item3 = new JButton(items[champPool.getNext()]);
		item4 = new JButton(items[champPool.getNext()]);
		item5 = new JButton(items[champPool.getNext()]);
		*/
		
		//testing
		item1 = new JButton(items[1]);
		item2 = new JButton(items[2]);
		item3 = new JButton(items[3]);
		item4 = new JButton(items[4]);
		item5 = new JButton(items[5]);
		
		this.add(item1);
		this.add(item2);
		this.add(item3);
		this.add(item4);
		this.add(item5);
		
		item1.addActionListener(this);
		item2.addActionListener(this);
		item3.addActionListener(this);
		item4.addActionListener(this);
		item5.addActionListener(this);
		
	}
	
	private ImageIcon scaleImage(ImageIcon imageIcon){
		Image image = imageIcon.getImage(); 
		Image newimg = image.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newimg);  
		return imageIcon;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}
	
	public void actionPerformed(ActionEvent e){
		if(e.getSource()==item1){
			board.summonChamp(1, 1);
		}else if (e.getSource()==item2){
			board.summonChamp(2, 1);
		}else if (e.getSource()==item3){
			board.summonChamp(3, 1);
		}else if (e.getSource()==item4){
			board.summonChamp(4, 1);
		}else if (e.getSource()==item5){
			board.summonChamp(5, 1);
		}
	}
	
}
