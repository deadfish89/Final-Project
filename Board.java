import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 

public class Board extends JPanel implements MouseListener{
	
	private ImageIcon bg;
	private ImageIcon annie, jinx, brand, braum, chogath, darius, kassadin, khazix, lux, nautilus, qiyana, riven, sivir, syndra,
	tibbers, varus, veigar, vi, wukong, yasuo;
	private JLabel label;
	private int x = 100, y = 100;
	
	public Board(){
		bg = new ImageIcon("TFT_EarthBoard.jpg");
		
		//epic
		annie = new ImageIcon("annie.png");
		chogath = new ImageIcon("cho'gath.png");
		wukong = new ImageIcon("wukong.png");
		qiyana = new ImageIcon("qiyana.png");
		
		//rare
		brand = new ImageIcon("brand.png");
		lux = new ImageIcon("lux.png");
		nautilus = new ImageIcon("nautilus.png");
		riven = new ImageIcon("riven.png");
		syndra = new ImageIcon("syndra.png");
		varus = new ImageIcon("varus.png");
		veigar = new ImageIcon("veigar.png");
		vi = new ImageIcon("vi.png");
		
		//common
		braum = new ImageIcon("braum.png");
		darius = new ImageIcon("darius.png");
		kassadin = new ImageIcon("kassadin.png");
		khazix = new ImageIcon("kha'zix.png");
		sivir = new ImageIcon("sivir.png");
		jinx = new ImageIcon("jinx.png");
		yasuo = new ImageIcon("yasuo.png");
		
		tibbers = new ImageIcon("tibbers.png");

		//testing
		label = new JLabel(scaleImage(kassadin));
		label.setBounds(x,y,120,120);
		label.addMouseListener(this);
		this.add(label);
		
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(1000,650));
	}
	
	private ImageIcon scaleImage(ImageIcon imageIcon){
		Image image = imageIcon.getImage(); 
		Image newimg = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newimg);  
		return imageIcon;
	}
	
	public void summonChamp(){
		
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(bg.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
		//g.drawImage(kassadin.getImage(), x, y, 100, 100, null);
	}
	
	public void mousePressed(MouseEvent e) {
		//testing
		x += 20;
		y += 20;
       label.setBounds(x,y,120,120);
    }
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
}