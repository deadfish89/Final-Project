import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 

public class EastPanel extends JPanel implements ActionListener{
	
	private Board board;
	private ImageIcon bg = new ImageIcon("background.png");
	
	public EastPanel(Board board){
		this.board = board;
		
		this.setPreferredSize(new Dimension(150, 570));
		this.setLayout(new FlowLayout());
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(bg.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
	}
	
	public void actionPerformed(ActionEvent e){
		
	}
	
}
