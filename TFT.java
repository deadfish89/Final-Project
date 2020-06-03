import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 

public class TFT{
	
	public static void main(String[] args){
		Player player = new Player();
		
		JFrame frame = new JFrame();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new Board(), BorderLayout.CENTER);
		frame.getContentPane().add(new SouthPanel(player), BorderLayout.SOUTH);
		frame.getContentPane().add(new Bench(player), BorderLayout.NORTH);
		frame.pack();
		
	}
}