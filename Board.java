import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	
	private Timer timer;
	private ImageIcon bg;
	private ImageIcon annie, jinx, brand, braum, chogath, darius, kassadin, khazix, lux, nautilus, qiyana, riven, sivir, syndra,
	tibbers, varus, veigar, vi, wukong, yasuo;
	
	private Player player;
	private Champion[][] board;
	private Tile[][] field;
	private Champion pickedChamp = null;
	private ArrayList<Champion> champs;
	private int w = this.getWidth(), h = this.getHeight();
	private int mX, mY, originalX, originalY;
	
	public Board(Player player){
		this.player = player;
		board = new Champion[10][3];
		champs = new ArrayList<Champion>();
		field = new Tile[10][3];

		//initialize field
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				field[i][j] = new Tile(i*100, h/2+j*100);
			}
		}	
		
		//background
		bg = new ImageIcon("TFT_EarthBoard.jpg");
		
		//panel
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(1000,650));
		
		//timer
		//myTimer = new Timer(100, this ); 
		//myTimer.start();
		
		//event listeners
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	private ImageIcon scaleImage(ImageIcon imageIcon){
		Image image = imageIcon.getImage(); 
		Image newimg = image.getScaledInstance(100, 100,  java.awt.Image.SCALE_SMOOTH);
		imageIcon = new ImageIcon(newimg);  
		return imageIcon;
	}
	
	public void summonChamp(int champ, int level){
		boolean summoned = false;
		
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (board[i][j]==null && !summoned){
					board[i][j] = new Champion(champ, i*100, h/2+j*100, level);
					champs.add(board[i][j]);
					field[i][j].addChamp();
					summoned = true;
					break;
				}
			}
		}
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//background
		g.drawImage(bg.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
		
		//champions on the board
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (board[i][j]!=null){
					board[i][j].myDraw(g);
				}
			}
		}
		
		//dragging champion
		if (pickedChamp!=null){
			pickedChamp.myDraw(g);
		}
	}
	
	//Action Listener
	public void actionPerformed(ActionEvent e){
		
	}
	
	//Mouse Listener
	public void mousePressed(MouseEvent e) {
		mX = e.getX();
		mY = e.getY();
		
		//if mouse presses a champion
		for (int i=0; i<champs.size(); i++){
			if (champs.get(i).contains(mX, mY) && pickedChamp == null){
				pickedChamp = champs.get(i);
				originalX = champs.get(i).getX(); 
				originalY = champs.get(i).getY();
			}
		}
		repaint();
    }
	
	public void mouseClicked(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
	
    public void mouseReleased(MouseEvent e){
		mX = e.getX();
		mY = e.getY();
		
		//if champion is released on an empty tile
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (field[i][j].contains(mX, mY) && pickedChamp!=null){
					if (field[i][j].isEmpty()){
						pickedChamp.setPos(field[i][j].getX(), field[i][j].getY());
						field[i][j].addChamp();
						board[i][j]=pickedChamp;
						pickedChamp=null;
					}else{
						pickedChamp.setPos(originalX, originalY);
					}
				}
			}
		}
		repaint();
	}
	
	//Mouse Motion Listener
	public void mouseDragged(MouseEvent e) {

		if(pickedChamp!=null) {
			pickedChamp.setPos(e.getX(), e.getY());
		}
		repaint();
	}
	
	public void mouseMoved(MouseEvent arg0) {}
	
}

class Champion{
	int x,y;
	ImageIcon image;
	int hp=0, lvl=0, ad=0, ap=0;
	
	public Champion(int champion, int x, int y, int level){
		this.x = x;
		this.y = y;
		
		switch (champion){
			case 0:
				image = new ImageIcon("annie.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				break;
			case 1: 
				image = new ImageIcon("wukong.png");
				break;
			case 2:
				image = new ImageIcon("cho'gath.png");
				break;
			case 3:
				image = new ImageIcon("qiyana.png");
				break;
			case 4: 
				image = new ImageIcon("brand.png");
				break;
			case 5: 
				image = new ImageIcon("lux.png");
				break;
			case 6: 
				image = new ImageIcon("nautilus.png");
				break;
			case 7:
				image = new ImageIcon("riven.png");
				break;
			case 8:
				image = new ImageIcon("syndra.png");
				break;
			case 9:
				image = new ImageIcon("varus.png");
				break;
			case 10:
				image = new ImageIcon("veigar.png");
			case 11: 
				image = new ImageIcon("vi.png");
				break;
			case 12:
				image = new ImageIcon("braum.png");
				break;
			case 13:
				image = new ImageIcon("darius.png");
				break;
			case 14:
				image = new ImageIcon("kassadin.png");
				break;
			case 15:
				image = new ImageIcon("kha.zix.png");
				break;
			case 16:
				image = new ImageIcon("sivir.png");
				break;
			case 17:
				image = new ImageIcon("jinx.png");
				break;
			case 18:
				image = new ImageIcon("yasuo.png");
				break;
			case 19:
				image = new ImageIcon("tibbers.png");
				break;	
		}
		//for every level they gain 30% of each stat
		for (int i=0; i<lvl; i++){
			hp+=hp*0.3;
			ad+=ad*0.3;
			ap+=ap*0.3;
		}
	}
	
	public void setPos(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	//if mouse is hovering
	public boolean contains(int mX, int mY) {
    	return (mX>x && mX<x+100 && mY>y && mY<y+100);
    }
	
	//custom draw at x and y
	public void myDraw(Graphics g) {
    	g.drawImage(image.getImage(), x, y, null);
    }
}

class Tile{
	
	private int x, y;
	private boolean isEmpty;
	
	public Tile(int x,int y){
		this.x = x;
		this.y = y;
		isEmpty = true;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	public boolean isEmpty(){
		return isEmpty;
	}
	
	public void addChamp(){
		isEmpty = false;
	}
	
	//if mouse is hovering
	public boolean contains(int mX, int mY) {
    	return (mX>x && mX<x+100 && mY>y && mY<y+100);
    }
}
