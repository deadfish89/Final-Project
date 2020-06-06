import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LeftPanel extends JPanel implements ActionListener{
	
	private Player player;
	private Shop shop;
	private LeftPanel2 leftPanel2;
	private JButton refresh, level;
	private JTextField xp, gold;
	
	public LeftPanel(Player player, Board board, LeftPanel2 leftPanel2, Shop shop){
		this.player = player;
		this.shop = shop;
		this.leftPanel2 = leftPanel2;
		this.setPreferredSize(new Dimension(185,130));
		this.setLayout(new GridLayout(2,1));
		this.setBackground(Color.black);
		
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
			if (player.getGold()>=4) {
				player.spendGold(4);
				player.gainXP(4);
				leftPanel2.redisplay();
			}
		}else if (e.getSource()==refresh){
			if (player.getGold()>=2){
				player.spendGold(2);
				shop.reloadImages();
				System.out.println("yes");
				leftPanel2.redisplay();
			}
		}else{
			
		}
	}
}