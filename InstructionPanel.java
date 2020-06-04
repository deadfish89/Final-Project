import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class InstructionPanel extends JPanel implements ActionListener{
	private JLabel title,picture1,bottomtext;
    	private JButton returnMenu;
    	private ImageIcon shop;
	public InstructionPanel(){
		shop = new ImageIcon("ShopPicture.png");
		picture1 = new JLabel(shop);
		returnMenu = new JButton("Return to Menu");
        	bottomtext = new JLabel("All champions and art belong to Riot Games. Pls dont sue me.");
		returnMenu.addActionListener(this);
		this.setBackground(Color.WHITE);
        	this.setLayout(null);
		picture1.setBounds(700,200,700,300);
		returnMenu.setBounds(50,800,200,100);
		this.add(picture1);
		this.add(returnMenu);
	}
	public void paintComponent(Graphics g){
        	super.paintComponent(g);
        	g.setFont(new Font("Open Sans",Font.BOLD,80));
        	g.drawString("Instructions",550,100);
		g.setFont(new Font("Open Sans",Font.BOLD,40));
		g.drawString("Buy champions from the shop",50,300);
		g.drawString("Each champion has traits. Having champions",500,500); 
		g.drawString("with the same traits gives bonuses.",500,550);
		g.drawString("Combine 3 of the same champion to make a more powerful version",50,700);
    	}
    	public void actionPerformed(ActionEvent event) {
            if(event.getSource()==returnMenu){
		TFT.LoadMenu();
            }
    }
}

