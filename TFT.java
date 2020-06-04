import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 

public class TFT{
	public static JFrame frame = new JFrame("Teamfight Tactics");
	public static void main(String[] args){
		JFrame frame = new JFrame("Teamfight Tactics");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		LoadMenu();
	}
	public static void LoadMenu(){
		frame.getContentPane().removeAll();
		frame.revalidate();
		frame.repaint();
		Menu menu = new Menu();
		frame.setVisible(true);
		frame.setSize(1300,700);
	    	frame.getContentPane().add(menu,BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
	}
	public static void LoadGame(){
		frame.getContentPane().removeAll();
		frame.revalidate();
		frame.repaint();
		Player player = new Player();
		Board board = new Board(player);
		frame.getContentPane().add(board, BorderLayout.CENTER);
		frame.getContentPane().add(new SouthPanel(player, board), BorderLayout.SOUTH);
		frame.getContentPane().add(new Bench(player, board), BorderLayout.NORTH);
		frame.pack();
	}
	public static void LoadInstructions(){
		frame.getContentPane().removeAll();
		frame.revalidate();
		frame.repaint();
		InstructionPanel panel = new InstructionPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.revalidate();
		frame.repaint();
	}
}
