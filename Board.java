import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 
import java.util.ArrayList;

public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	
	private Timer timer;
	private ImageIcon bg, b;
	private ImageIcon annie, jinx, brand, braum, chogath, darius, kassadin, khazix, lux, nautilus, qiyana, riven, sivir, syndra,
	tibbers, varus, veigar, vi, wukong, yasuo;
	
	private Player player;
	private Champion[][] board;
	private Tile[][] field;
	private Tile[] benchField;
	private Champion[] bench;
	private Champion pickedChamp = null;
	private ArrayList<Champion> champs;
	private ArrayList<Champion> benchChamps;
	private int w, h;
	private int mX, mY, originalX, originalY;
	
	public Board(Player player){
		//panel
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(1000,800)); 
		w = 1000; h = 800; //this.getWidth() gives 0 for some reason
		
		this.player = player;
		board = new Champion[10][3];
		champs = new ArrayList<Champion>();
		field = new Tile[10][3];
		benchField = new Tile[10];
		benchChamps = new ArrayList<Champion>();
		bench = new Champion[10];

		//initialize field
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				field[i][j] = new Tile(i*100, h/2 +j*100 );
			}
		}	
		
		//initialize benchField
		for (int i=0; i<10; i++){
			benchField[i] = new Tile(i*100, h-100);
		}
		
		//background
		bg = new ImageIcon("TFT_EarthBoard.jpg");
		b = new ImageIcon("bench2.png");
		
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
		
		for (int i=0; i<10; i++){
				if (bench[i]==null){
					bench[i] = new Champion(champ, i*100, h-100, level);
					benchField[i].addChamp();
					benchChamps.add(bench[i]);
					champs.add(bench[i]);
					break;
				}
		}
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//background
		g.drawImage(bg.getImage(), 0, 98, w, h-196, null); //98 and 196 to fill in the couple pixels of white space 
		for (int i=0; i<10; i++){
			g.drawImage(b.getImage(), i*100, h-100, null);
			g.drawImage(b.getImage(), i*100, 0, null);
			g.drawLine(i*100, h, i*100, h-100);
			g.drawLine(i*100, 0, i*100, 100);
		}
		
		//champions on the board
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (board[i][j]!=null){
					board[i][j].myDraw(g);
				}else{
					field[i][j].myDraw(g);
				}
			}
		}
		
		//champions on the bench
		for (int i=0; i<10; i++){
			if (bench[i]!=null){
				bench[i].myDraw(g);
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
		
		//if mouse presses a champion on board
		for (int i=0; i<champs.size(); i++){
			if (champs.get(i).contains(mX, mY)){
				pickedChamp = champs.get(i);
				originalX = champs.get(i).getX(); 
				originalY = champs.get(i).getY();
				for (int j=0; j<10; j++){
					for (int k=0; k<3; k++){
						if (field[j][k].contains(originalX, originalY)){
							field[j][k].removeChamp();
							board[j][k] = null;
						}
					}
				}
			}
		}
		
		//if mouse presses a champion on bench
		for (int i=0; i<benchChamps.size(); i++){
			if (benchChamps.get(i).contains(mX, mY)){
				System.out.println("works");
				pickedChamp = benchChamps.get(i);
				originalX = benchChamps.get(i).getX(); 
				originalY = benchChamps.get(i).getY();
				for (int j=0; j<10; j++){
					if (benchField[j].contains(originalX, originalY)){
						benchField[j].removeChamp();
						bench[j] = null;
						System.out.println("works2");
					}
				}
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
		boolean onTile = false;

		//if champion is released on an empty tile
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (field[i][j].contains(mX, mY) && pickedChamp!=null){
					onTile = true;
					if (field[i][j].isEmpty()){
						pickedChamp.setPos(field[i][j].getX(), field[i][j].getY());
						field[i][j].addChamp();
						board[i][j]=pickedChamp;
					}
					//tile is taken by another champion
					else{
						System.out.println("yes");
						pickedChamp.setPos(originalX, originalY);
						for (int k=0; k<10; k++){
							if (benchField[k].contains(originalX, originalY)){
								System.out.println("yes");
								benchField[k].addChamp();
								bench[k] = pickedChamp;
							}
						for (int l=0; l<3; l++){
							if (field[k][l].contains(originalX, originalY)){
								field[k][l].addChamp();
								board[k][l] = pickedChamp;
							}
						}
					}
				}
				}
				}
		}
		
		for (int i=0; i<10; i++){
				if (benchField[i].contains(mX, mY) && pickedChamp!=null){
					onTile = true;
					if (benchField[i].isEmpty()){
						pickedChamp.setPos(benchField[i].getX(), benchField[i].getY());
						benchField[i].addChamp();
						bench[i]=pickedChamp;
					}
					//tile is taken by another champion
					else{
						pickedChamp.setPos(originalX, originalY);
						for (int j=0; j<10; j++){
								if (benchField[j].contains(originalX, originalY)){
									benchField[j].addChamp();
									bench[j] = pickedChamp;
							}
						}
					}
				}
		}
		
		//if champion isn't released on any tile
		if (pickedChamp!=null && !onTile){
				pickedChamp.setPos(originalX, originalY);
				for (int i=0; i<10; i++){
					if (benchField[i].contains(originalX, originalY)){
						benchField[i].addChamp();
						bench[i] = pickedChamp;
					}
					for (int j=0; j<3; j++){
						if (field[i][j].contains(originalX, originalY)){
							field[i][j].addChamp();
							board[i][j] = pickedChamp;
						}
					}
				}
			}
		pickedChamp = null;
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

