import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
		this.add(new LeftPanel(player), BorderLayout.WEST);
		this.add(new Display(), BorderLayout.CENTER);
		this.add(new Shop(player, board), BorderLayout.EAST);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
}