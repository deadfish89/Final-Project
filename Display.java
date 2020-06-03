import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Display extends JPanel{
	
	private JButton refresh, level;
	private JTextField xp, gold;
	
	public Display(){
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