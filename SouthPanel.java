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

