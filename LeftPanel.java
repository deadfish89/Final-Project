import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class LeftPanel extends JPanel implements ActionListener{
	
	private Player player;
	private JButton refresh, level;
	private JTextField xp, gold;
	
	public LeftPanel(Player player){
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
		}
	}
}