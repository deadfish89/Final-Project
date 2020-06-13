import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 
import java.util.Arrays;

public class TFT{

	public static JFrame frame;
	public static Container container;
	
	public static void main(String[] args){
		frame = new JFrame("Teamfight Tactics");
		container = frame.getContentPane();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		LoadMenu();
	}
	public static void LoadMenu(){
		container.removeAll();
		Menu menu = new Menu();
	    container.add(menu,BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
		frame.pack();
	}
	public static void LoadGame(){
		container.removeAll();
		Player player = new Player();
		Board board = new Board(player);
		container.add(board, BorderLayout.CENTER);
		container.add(new SouthPanel(player, board), BorderLayout.SOUTH);
		container.add(new EastPanel(board), BorderLayout.EAST);
		frame.pack();
	}
	public static void LoadInstructions(){
		container.removeAll();
		InstructionPanel panel = new InstructionPanel();
		container.add(panel, BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
	}
}
