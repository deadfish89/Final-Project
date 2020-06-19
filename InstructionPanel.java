import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class InstructionPanel extends JPanel implements ActionListener{
	private JLabel title,shopPicture,traitPicture,starPicture,bottomtext;
    	private JButton returnMenu;
	public InstructionPanel(){
		shopPicture = new JLabel(new ImageIcon("ShopPicture.png"));
		traitPicture = new JLabel(new ImageIcon("TraitPicture.png"));
		starPicture = new JLabel(new ImageIcon("StarPicture.png"));
		returnMenu = new JButton("Return to Menu");
        	bottomtext = new JLabel("All champions and art belong to Riot Games. Pls dont sue me.");
		returnMenu.addActionListener(this);
		this.setBackground(Color.WHITE);
        	this.setLayout(null);
		shopPicture.setBounds(540,0,700,300);
		traitPicture.setBounds(100,-25,500,700);
		starPicture.setBounds(100,400,700,300);
		returnMenu.setBounds(1000,500,200,100);
		returnMenu.setBackground(new Color(246,178,61));
		returnMenu.setOpaque(true);
		returnMenu.setBorder(BorderFactory.createBevelBorder(0));
		returnMenu.setFont(new Font("SansSerif",Font.PLAIN,20));
		this.add(shopPicture);
		this.add(traitPicture);
		this.add(starPicture);
		this.add(returnMenu);
	}
	public void paintComponent(Graphics g){
        	super.paintComponent(g);
        	g.setFont(new Font("SansSerif",Font.BOLD,50));
        	g.drawString("Instructions",450,50);
		g.setFont(new Font("SansSerif",Font.PLAIN,40));
		g.drawString("Buy champions from the shop",10,150);
		g.drawString("Each champion has traits. Having champions",500,300); 
		g.drawString("with the same traits gives bonuses.",500,350);
		g.drawString("Combine 3 of the same champion to make a more powerful version",50,475);
    	}
    	public void actionPerformed(ActionEvent event) {
            if(event.getSource()==returnMenu){
		TFT.LoadMenu();
            }
    }
}

