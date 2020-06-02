import javax.swing.*;
import java.awt.*;
import java.awt.Component.*;

public class Main {
    public static void main(String[] args) {
	    JFrame frame = new JFrame("Final Project");
	    Container container = frame.getContentPane();
	    container.setLayout(new BorderLayout());
	    Menu menu = new Menu();
	    frame.add(menu);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(1500,1000);
	    frame.setVisible(true);
    }
}
