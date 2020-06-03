import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 

public class TFT{
	
	public static void main(String[] args){
		Player player = new Player();
		Board board = new Board(player);
		
		JFrame frame = new JFrame("Teamfight Tactics");
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(board, BorderLayout.CENTER);
		frame.getContentPane().add(new SouthPanel(player, board), BorderLayout.SOUTH);
		frame.getContentPane().add(new Bench(player, board), BorderLayout.NORTH);
		frame.pack();
		
	}
}
