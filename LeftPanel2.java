import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LeftPanel2 extends JPanel{
	
	private JButton refresh, level;
	private JLabel xpLabel, goldLabel, lvlLabel;
	private Player player;
	
	public LeftPanel2(Player player){
		this.player = player;
		this.setPreferredSize(new Dimension(115,130));
		this.setLayout(new GridLayout(3,1));
		this.setBackground(Color.white);
		display();
	}
	
	public void display(){
		System.out.println("display");
		xpLabel = new JLabel("XP: " + player.getXP(), JLabel.CENTER);
		goldLabel = new JLabel("Gold: " + player.getGold(), JLabel.CENTER);
		lvlLabel = new JLabel("Level: " + player.getLevel(), JLabel.CENTER);
		
		System.out.println(player.getGold());
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
		System.out.println("redisplay");
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
}
