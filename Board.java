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
				if (board[i][j]!=null && board[i][j].isAlive()){
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

class Champion{
	static boolean[] assassin = new boolean[1], blademaster = new boolean[3], sorcerer = new boolean[6], warden = new boolean[3], marksmen = new boolean[4], brawler = new boolean[3];
	static boolean[] elemental = new boolean[7], glacial = new boolean[2], demon = new boolean[3], imperial = new boolean[2], void1 = new boolean[3], hextech = new boolean[2], monkey = new boolean[1];
	static boolean hasVoid, hasDemon, hasHextech, hasGlacial, hasBlademaster;
	
	private boolean isAlive = true;
	
	private int x, y;
	private ImageIcon image;
	private int hp=0, lvl=0, ad=0, ap=0, as = 0, armor = 0, curHP = 0, mana = 0, curMana = 0;
	
	private int count;
	
	public Champion(int champion, int x, int y, int level){
		count = 0;
		this.x = x;
		this.y = y;
				
		switch (champion){
			case 0:
				image = new ImageIcon("annie.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				sorcerer[0] = true;
				elemental[0] = true;
				
				for (int i=0; i<sorcerer.length; i++){
					if (sorcerer[i]) count++;
				}
				if (count==6) ap*=2;
				else if (count>=3) ap*=1.5;
				
				count = 0;
				for (int i=0; i<elemental.length; i++){
					if (elemental[i]) count++;
				}
				if (count==7) count=0;//something
				else if (count>=5) count=0;//something
				break;
			case 1: 
				image = new ImageIcon("wukong.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				brawler[0] = true;
				monkey[0] = true;
				
				for (int i=0; i<brawler.length; i++){
					if (brawler[i]) count++;
				}
				if (count==3) hp*=2;
				
				if (monkey[0]) armor*=2;
				break;
			case 2:
				image = new ImageIcon("cho'gath.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				brawler[1] = true;
				void1[0] = true;
				
				for (int i=0; i<brawler.length; i++){
					if (brawler[i]) count++;
				}
				if (count==3) hp*=2;
				
				count = 0;
				for (int i=0; i<void1.length; i++){
					if (void1[i]) count++;
				}
				if (count>=3) hasVoid = true;
				break;
			case 3:
				image = new ImageIcon("vel'koz.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				sorcerer[0] = true;
				void1[1] = true;
				
				for (int i=0; i<sorcerer.length; i++){
					if (sorcerer[i]) count++;
				}
				if (count==6) ap*=2;
				else if (count>=3) ap*=1.5;
				
				count = 0;
				for (int i=0; i<void1.length; i++){
					if (void1[i]) count++;
				}
				if (count>=3) hasVoid = true;
				break;
			case 4: 
				image = new ImageIcon("brand.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				sorcerer[1] = true;
				elemental[1] = true;
				
				for (int i=0; i<sorcerer.length; i++){
					if (sorcerer[i]) count++;
				}
				if (count==6) ap*=2;
				else if (count>=3) ap*=1.5;
				
				count = 0;
				for (int i=0; i<elemental.length; i++){
					if (elemental[i]) count++;
				}
				if (count==7) count=0;//something
				else if (count>=5) count=0;//something
				break;
			case 5: 
				image = new ImageIcon("lux.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				sorcerer[2] = true;
				elemental[2] = true;
				
				for (int i=0; i<sorcerer.length; i++){
					if (sorcerer[i]) count++;
				}
				if (count==6) ap*=2;
				else if (count>=3) ap*=1.5;
				
				count = 0;
				for (int i=0; i<elemental.length; i++){
					if (elemental[i]) count++;
				}
				if (count==7) count=0;//something
				else if (count>=5) count=0;//something
				break;
			case 6: 
				image = new ImageIcon("nautilus.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				warden[0] = true;
				elemental[3] = true;
				
				for (int i=0; i<warden.length; i++){
					if (warden[i]) count++;
				}
				if (count==3) armor*=3;
			    else if (count==2) armor*=2;
				else if (count==1) armor*=1.5;
				
				count = 0;
				for (int i=0; i<elemental.length; i++){
					if (elemental[i]) count++;
				}
				if (count==7) count=0;//something
				else if (count>=5) count=0;//something
				break;
			case 7:
				image = new ImageIcon("syndra.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				sorcerer[3] = true;
				demon[0] = true;
				
				for (int i=0; i<sorcerer.length; i++){
					if (sorcerer[i]) count++;
				}
				if (count==6) ap*=2;
				else if (count>=3) ap*=1.5;
				
				count = 0;
				for (int i=0; i<demon.length; i++){
					if (demon[i]) count++;
				}
				if (count==3) hasDemon = true;
				break;
			case 8:
				image = new ImageIcon("varus.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				marksmen[0] = true;
				demon[1] = true;
				
				for (int i=0; i<marksmen.length; i++){
					if (marksmen[i]) count++;
				}
				if (count==4) as*=2;
				else if (count>=2) as*=1.5;
				
				count = 0;
				for (int i=0; i<demon.length; i++){
					if (demon[i]) count++;
				}
				if (count==3) hasDemon = true;
				break;
			case 9:
				image = new ImageIcon("veigar.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				sorcerer[4] = true;
				demon[2] = true;
				
				for (int i=0; i<sorcerer.length; i++){
					if (sorcerer[i]) count++;
				}
				if (count==6) ap*=2;
				else if (count>=3) ap*=1.5;
				
				count = 0;
				for (int i=0; i<demon.length; i++){
					if (demon[i]) count++;
				}
				if (count==3) hasDemon = true;
				break;
			case 10: 
				image = new ImageIcon("vi.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				brawler[2] = true;
				hextech[0] = true;
				
				for (int i=0; i<brawler.length; i++){
					if (brawler[i]) count++;
				}
				if (count==3) hp*=2;
				
				count = 0;
				for (int i=0; i<hextech.length; i++){
					if (hextech[i]) count++;
				}
				if (count==2) hasHextech = true;
				break;
			case 11:
				image = new ImageIcon("qiyana.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				assassin[0] = true;
				elemental[4] = true;
				
				for (int i=0; i<assassin.length; i++){
					if (assassin[i]) count++;
				}
				if (count==3) hp*=2;
				
				count = 0;
				for (int i=0; i<elemental.length; i++){
					if (elemental[i]) count++;
				}
				if (count==7) count=0;//something
				else if (count>=5) count=0;//something
				break;
			case 12: 
				image = new ImageIcon("riven.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				blademaster[0] = true;
				imperial[0] = true;
				
				for (int i=0; i<blademaster.length; i++){
					if (blademaster[i]) count++;
				}
				if (count==3) hasBlademaster = true;
				
				count = 0;
				for (int i=0; i<imperial.length; i++){
					if (imperial[i]) count++;
				}
				if (count==2){
					ad*=2;
					ap*=2;
					as*=2;
				}

				break;
			case 13:
				image = new ImageIcon("braum.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				warden[1] = true;
				glacial[0] = true;
				
				for (int i=0; i<warden.length; i++){
					if (warden[i]) count++;
				}
				if (count==3) armor*=3;
			    else if (count==2) armor*=2;
				else if (count==1) armor*=1.5;
				
				count = 0;
				for (int i=0; i<glacial.length; i++){
					if (glacial[i]) count++;
				}
				if (count==2) hasGlacial = true;
				break;
			case 14:
				image = new ImageIcon("darius.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				warden[2] = true;
				imperial[1] = true;
				
				for (int i=0; i<warden.length; i++){
					if (warden[i]) count++;
				}
				if (count==3) armor*=3;
			    else if (count==2) armor*=2;
				else if (count==1) armor*=1.5;
				
				count = 0;
				for (int i=0; i<imperial.length; i++){
					if (imperial[i]) count++;
				}
				if (count==2){
					ad*=2;
					ap*=2;
					as*=2;
				}
				break;
			case 15:
				image = new ImageIcon("kassadin.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				blademaster[1] = true;
				void1[2] = true;
				
				for (int i=0; i<blademaster.length; i++){
					if (blademaster[i]) count++;
				}
				if (count==3) hasBlademaster = true;
				
				count = 0;
				for (int i=0; i<void1.length; i++){
					if (void1[i]) count++;
				}
				if (count>=3) hasVoid = true;
				break;
			case 16:
				image = new ImageIcon("sivir.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				marksmen[2] = true;
				elemental[5] = true;
				
				for (int i=0; i<marksmen.length; i++){
					if (marksmen[i]) count++;
				}
				if (count==4) as*=2;
				else if (count>=2) as*=1.5;
				
				count = 0;
				for (int i=0; i<elemental.length; i++){
					if (elemental[i]) count++;
				}
				if (count==7) count=0;//something
				else if (count>=5) count=0;//something
				break;
			case 17:
				image = new ImageIcon("jinx.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				marksmen[3] = true;
				hextech[1] = true;
				
				for (int i=0; i<marksmen.length; i++){
					if (marksmen[i]) count++;
				}
				if (count==4) as*=2;
				else if (count>=2) as*=1.5;
				
				count = 0;
				for (int i=0; i<hextech.length; i++){
					if (hextech[i]) count++;
				}
				if (count==2) hasHextech = true;
				break;
			case 18:
				image = new ImageIcon("yasuo.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				mana = 100;
				blademaster[2] = true;
				elemental[6] = true;
				
				for (int i=0; i<blademaster.length; i++){
					if (blademaster[i]) count++;
				}
				if (count==3) hasBlademaster = true;
				
				count = 0;
				for (int i=0; i<elemental.length; i++){
					if (elemental[i]) count++;
				}
				if (count==7) count=0;//something
				else if (count>=5) count=0;//something
				break;
			case 19:
				image = new ImageIcon("tibbers.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				as = 1;
				armor = 30;
				break;	
		}
		//for every level they gain 30% of each stat
		for (int i=0; i<lvl; i++){
			hp+=hp*0.3;
			ad+=ad*0.3;
			ap+=ap*0.3;
			as = 1;
		}
		curHP = hp;
	}
	
	public void takeDmg(int dmg){
		curHP-=dmg;
		if (curHP<=0) isAlive = false;
	}
	
	public int autoAttack(){
		curMana+=10;
		return ad;
	}
	
	public int getAS(){
		return as;
	}
	
	public int useAbility(){
		int dmg = 0;
		if (curMana>=mana){
			curMana = 0;
			return dmg;
		}else {
			return -1;
		}
	}
	
	public int getCurMana(){
		return curMana;
	}
	
	public int getMaxMana(){
		return mana;
	}

	public boolean isAlive(){
		return isAlive;
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
    	return (mX>=x && mX<x+100 && mY>=y && mY<y+100);
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
	
	public void removeChamp(){
		isEmpty = true;
	}
	
	//if mouse is hovering
	public boolean contains(int mX, int mY) {
    	return (mX>=x && mX<x+100 && mY>=y && mY<y+100);
    }
	
	public void myDraw(Graphics g) {
    	g.drawRect(x, y, 100, 100);
    }
}
