import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Menu extends JPanel implements ActionListener {
    private JLabel title,bottomtext;
    private JButton play,instruction;
    private ImageIcon logo;
    public Menu(){
        logo = new ImageIcon("TitleLogo.png");
        play = new JButton("Play");
        instruction = new JButton("Instructions");
        title = new JLabel(logo);
        bottomtext = new JLabel("All champions and art belong to Riot Games. Pls dont sue me.");
        play.addActionListener(this);
        instruction.addActionListener(this);
        this.setBackground(Color.WHITE);
        this.setLayout(null);
        title.setBounds(0,0,1500,400);
        play.setBounds(500,550,500,100);
        instruction.setBounds(500,750,500,100);
        bottomtext.setBounds(550,900,500,100);
        this.add(title);
        this.add(play);
        this.add(instruction);
        this.add(bottomtext);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setFont(new Font("Open Sans",Font.BOLD,80));
        g.drawString("Single Player",500,475);
    }
    public void actionPerformed(ActionEvent event) {
            if(event.getSource()==play){
		TFT.LoadGame();
            }
            if(event.getSource()==instruction){
                TFT.LoadInstructions();
            }
    }
}
