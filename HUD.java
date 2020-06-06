import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class HUD extends JPanel implements ActionListener{
	
	private Player player;
	private Board board;
	private ImageIcon bg;
	private LeftPanel2 leftPanel2;
	private Shop shop;
	
	public HUD(Player player, Board board){
		this.player = player;
		this.board = board;
		leftPanel2 = new LeftPanel2(player);
		shop = new Shop(player, board, leftPanel2);
		this.setBackground(Color.black);
		this.setPreferredSize(new Dimension(1000,130));
		this.setLayout(new BorderLayout());
		this.add(shop, BorderLayout.EAST);
		this.add(new LeftPanel(player, board, leftPanel2, shop), BorderLayout.WEST);
		this.add(leftPanel2, BorderLayout.CENTER);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
}