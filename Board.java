import javax.swing.*;
import java.awt.event.*; 
import java.util.ArrayList;
import java.util.Arrays;

public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	
	private Timer timer;
	private ImageIcon bg, b;
	private ImageIcon annie, jinx, brand, braum, chogath, darius, kassadin, khazix, lux, nautilus, qiyana, riven, sivir, syndra,
	tibbers, varus, veigar, vi, wukong, yasuo;
	
	private int[] origins;
	private int[] traits; 
	
	private int nBoardChamps;
	private ArrayList<Champion> boardChamps;
	
	private Player player;
	private Champion[][] board;
	private Champion[][] enemyBoard;
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
		
		origins = new int[7];
		traits = new int[6];
		
		boardChamps = new ArrayList<Champion>();
		nBoardChamps = 0;
		
		this.player = player;
		board = new Champion[10][3];
		enemyBoard = new Champion[10][3];
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
	
	
	public void summonChamp(int champ, int level){
		int x = 0, y = 0, index = 0;
		for (int i=0; i<10; i++){
				if (bench[i]==null){
					x = i*100;
					y = h-100;
					index = i;
					break;
				}
		}
		
		Champion temp = null;
		switch (champ){
			case 0:
				temp = new Annie(x, y, level);
				break;
			case 1:
				temp = new Wukong(x, y, level);
				break;
			case 2:
				temp = new Chogath(x, y, level);
				break;
			case 3:
				temp = new Velkoz(x, y, level);
				break;
			case 4:
				temp = new Brand(x, y, level);
				break;
			case 5:
				temp = new Lux(x, y, level);
				break;
			case 6:	
				temp = new Nautilus (x, y, level);
				break;
			case 7:
				temp = new Syndra(x, y, level);
				break;
			case 8:
				temp = new Varus(x, y, level);
				break;
			case 9:
				temp = new Veigar(x, y, level);
				break;
			case 10:
				temp = new Vi(x, y, level);
				break;
			case 11:
				temp = new Qiyana(x, y, level);
				break;
			case 12:
				temp = new Riven(x, y, level);
				break;
			case 13:
				temp = new Braum(x, y, level);
				break;
			case 14:
				temp = new Darius(x, y, level);
				break;
			case 15:
				temp = new Kassadin(x, y, level);
				break;
			case 16:	
				temp = new Sivir(x, y, level);
				break;
			case 17:
				temp = new Jinx(x, y, level);
				break;
			case 18:
				temp = new Yasuo(x, y, level);
				break;
		}
		bench[index] = temp;
		benchField[index].addChamp();
		benchChamps.add(bench[index]);
		champs.add(bench[index]);
		
		repaint();
	}
	
	public void addToBoard(Champion champ){
		if (nBoardChamps<player.getLevel()){
			boardChamps.add(champ);
			nBoardChamps++;
		}
	}
	
	public void removeFromBoard(Champion champ){
		boardChamps.remove(champ);
		nBoardChamps--;
	}
	
	public void refreshTraits(){
		Arrays.fill(traits, 0);
		Arrays.fill(origins, 0);
		int len = boardChamps.size();
		Champion[] visited = new Champion[len];
		for (int i=0; i<len; i++){
			boolean isDuplicate = false;
			Champion cur = boardChamps.get(i);
			visited[i] = cur;
			
			for (int j=0; j<len; j++){
				if (cur.getClass().equals(visited[j].getClass()))
					isDuplicate = true;
			}
			
			if (!isDuplicate){
			traits[cur.getTrait()]++;
			origins[cur.getOrigin()]++;
			}
		}
		for (int i=0; i<boardChamps.size(); i++){
			Champion cur = boardChamps.get(i);
			int trait = cur.getTrait();
			int origin = cur.getOrigin();
			switch (trait){
				case 0:
					if (traits[trait]==6) cur.setAP(cur.getAP()*2);
					else if(traits[trait]>=3) cur.setAP((int)(cur.getAP()*1.3));
					break;
				case 1:
					if (traits[trait]==4) cur.setAS(cur.getAS()*1.75);
					else if(traits[trait]>=2) cur.setAS(cur.getAS()*1.25);
					break;
				case 2:
					if (traits[trait]==1) cur.setArmor((int)(cur.getArmor()*1.3));
					else if(traits[trait]==2) cur.setArmor(cur.getArmor()*2);
					else if(traits[trait]==3) cur.setArmor(cur.getArmor()*3);
					break;
				case 3:
					if (traits[trait]==3) cur.setHP(cur.getHP()*2);
					break;
				case 4:
					cur.setVel(cur.getVel()*2);
					break;
				case 5:
					if (traits[trait]==3) 
					break;
			}
			
			switch(origin){
				case 0:
					if (origins[origin]==7) cur.setAP(cur.getAP()*2); //change to elemental effect
					else if(origins[origin]>=5) cur.setAP((int)(cur.getAP()*1.3)); //change to elemental effect
					break;
				case 1:
					if (origins[origin]==2) cur.hasGlacial();
					break;
				case 2:
					if (origins[origin]==3) cur.hasDemon();
					break;
				case 3:
					if (origins[origin]==2){
						cur.setAD(cur.getAD()*2);
						cur.setAP(cur.getAP()*2);
					}
					break;
				case 4:
					if (origins[origin]>=3) cur.hasVoid();
					break;
				case 5:
					if (origins[origin]==2) cur.hasHextech();
					break;
				case 6:
					cur.setArmor(cur.getArmor()*2); //change to if the enemy has more than 3 champions alive get double armor
			}
			
			}
		}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		//background
		g.drawImage(bg.getImage(), 0, 98, w, h-196, null); //98 and 196 to fill in the couple pixels of white space 
		
		//benches
		for (int i=0; i<10; i++){
			g.drawImage(b.getImage(), i*100, h-100, null);
			g.drawImage(b.getImage(), i*100, 0, null);
			if (pickedChamp!=null){
				g.drawLine(i*100, h, i*100, h-100);
			}
		}
		
		//champions on the board
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (board[i][j]!=null && board[i][j].isAlive()){
					board[i][j].myDraw(g);
				}else if (pickedChamp!=null){
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
				pickedChamp = benchChamps.get(i);
				originalX = benchChamps.get(i).getX(); 
				originalY = benchChamps.get(i).getY();
				for (int j=0; j<10; j++){
					if (benchField[j].contains(originalX, originalY)){
						benchField[j].removeChamp();
						bench[j] = null;
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
		boolean boardFull = false;

		//if champion is released on an empty tile
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (field[i][j].contains(mX, mY) && pickedChamp!=null){
					onTile = true;
					if (field[i][j].isEmpty()){
						//check if champion was from bench
						for (int k=0; k<10; k++){
							if (benchField[k].contains(originalX, originalY)){
								System.out.println("LEVEL: " + player.getLevel());
								//if board is full
								if (nBoardChamps<player.getLevel()){
									System.out.println("LEVEL: " + player.getLevel());
									this.addToBoard(pickedChamp);
								}else {
									pickedChamp.setPos(originalX, originalY);
									benchField[k].addChamp();
									bench[k] = pickedChamp;
									boardFull = true;
								}
							}
						}
						if (!boardFull){
							pickedChamp.setPos(field[i][j].getX(), field[i][j].getY());
							field[i][j].addChamp();
							board[i][j]=pickedChamp;
						}
					}
					//tile is taken by another champion
					else{
						pickedChamp.setPos(originalX, originalY);
						for (int k=0; k<10; k++){
							if (benchField[k].contains(originalX, originalY)){
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
					//if champion was from board
					for (int j=0; j<10; j++){
							for (int k=0; k<3; k++){
								if (field[j][k].contains(originalX, originalY)){
									this.removeFromBoard(pickedChamp);
								}
							}
						}
						
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

class Champion{
	
	protected boolean isAlive = true;
	
	protected int x, y;
	protected ImageIcon image;
	protected int hp=0, lvl=0, ad=0, ap=0, armor = 0, mr = 0, curHP = 0, mana = 0, curMana = 0;
	protected double as = 0;
	protected int originalHP, originalAD, originalMR, originalAP, originalArmor;
	protected double originalAS;
	protected int trait = 0;
	protected int origin = 0;
	protected int vel = 5;
	
	protected boolean blademaster, void1, glacial, hextech, demon;
	
	protected int count;
	
	public Champion(int x, int y, int level){
		count = 0;
		this.x = x;
		this.y = y;
	}
	
	public void stun(int duration){
		
	}
	
	public void takeDmg(int dmg){
		curHP-=dmg;
		if (curHP<=0) isAlive = false;
	}
	
	public void autoAttack(Champion target){
		curMana+=10;
		int targetArmor = target.getArmor();
		if (blademaster) {
			if ((int)(Math.random()*3)==0) this.autoAttack(target);
		}
		if (glacial){
			if ((int)(Math.random()*5)==0) target.stun(1);
		}
		if (void1){
			targetArmor=0;
		}
		if (demon){
			target.setMana(target.getCurMana()-20);
		}
		target.takeDmg(ad-ad*targetArmor/150);
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
	
	/* traits and origins */
	public int getTrait(){
		return trait;
	}
	
	public int getOrigin(){
		return origin;
	}
	
	public void hasBlademaster(){
		blademaster = true;
	}
	
	public void hasGlacial(){
		glacial = true;
	}
	
	public void hasVoid(){
		void1 = true;
	}
	
	public void hasHextech(){
		hextech = true;
	}
	
	public void hasDemon(){
		demon = true;
	}
	
	/* set methods */
	public void setVel(int vel){
		this.vel = vel;
	}
	
	public void setHP(int hp){
		this.hp = hp;
		curHP = hp;
	}
	
	public void setAD(int ad){
		this.ad = ad;
	}
	
	public void setAP(int ap){
		this.ap = ap;
	}
	
	public void setAS(double as){
		this.as = as;
	}
	
	public void setArmor(int armor){
		this.armor = armor;
	}
	
	public void setMana(int mana){
		if (mana<0) mana = 0;
		curMana = mana;
	}
	
	/* get methods */
	public double getAS(){
		return as;
	}
	
	public int getAD(){
		return ad;
	}
	
	public int getAP(){
		return ap;
	}
	
	public int getArmor(){
		return armor;
	}
	
	public int getHP(){
		return hp;
	}
	
	public int getVel(){
		return vel;
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
	
	/* positioning and display */
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

class Annie extends Champion{
	
	public Annie(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("annie.png");
		origin = 0; trait = 0;
		originalHP=1500; originalAD=100; originalAP=100; originalAS=1; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Wukong extends Champion{
	public Wukong(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("wukong.png");
		origin = 6; trait = 3;
		originalHP=2000; originalAD=150; originalAP=100; originalAS=1; originalArmor=30; originalMR=30;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Chogath extends Champion{
	public Chogath(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("cho'gath.png");
		origin = 4; trait = 3;
		originalHP=2500; originalAD=100; originalAP=100; originalAS=1; originalArmor=30; originalMR = 30;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Velkoz extends Champion{
	public Velkoz(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("vel'koz.png");
		origin = 4; trait = 0;
		originalHP=1500; originalAD=100; originalAP=100; originalAS=1; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Brand extends Champion{
	public Brand(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("brand.png");
		origin = 0; trait = 0;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR = 10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Lux extends Champion{
	public Lux(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("lux.png");
		origin = 0; trait = 0;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR = 10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Nautilus extends Champion{
	public Nautilus(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("nautilus.png");
		origin = 0; trait = 2;
		originalHP=2000; originalAD=50; originalAP=75; originalAS=1; originalArmor=30; originalMR=30;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Syndra extends Champion{
	public Syndra(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("syndra.png");
		origin = 2; trait = 0;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Varus extends Champion{
	public Varus(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("varus.png");
		origin = 0; trait = 1;
		originalHP=1250; originalAD=100; originalAP=75; originalAS=1.5; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
}
class Veigar extends Champion{
	public Veigar(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("veigar.png");
		origin = 2; trait = 0;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Vi extends Champion{
	public Vi(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("vi.png");
		origin = 5; trait = 3;
		originalHP=1500; originalAD=70; originalAP=75;originalAS=1; originalArmor=20; originalMR=20;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Qiyana extends Champion{
	public Qiyana(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("qiyana.png");
		origin = 0; trait = 4;
		originalHP=1250; originalAD=150; originalAP=75; originalAS=1; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Riven extends Champion{
	public Riven(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("riven.png");
		origin = 3; trait = 5; 
		originalHP=1500; originalAD=70; originalAP=75; originalAS=1.5; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Braum extends Champion{
	public Braum(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("braum.png");
		origin = 1; trait = 2;
		originalHP=2000; originalAD=50; originalAP=75; originalAS=1; originalArmor=30; originalMR=30;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Darius extends Champion{
	public Darius(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("darius.png");
		origin = 3; trait = 2;
		originalHP=1500; originalAD=50; originalAP=50; originalAS=1; originalArmor=15; originalMR=15;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Kassadin extends Champion{
	public Kassadin(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("kassadin.png");
		origin = 4; trait = 5;
		originalHP=1250; originalAD=50; originalAP=50; originalAS=1; originalArmor=10; originalMR=20;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Sivir extends Champion{
	public Sivir(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("sivir.png");
		origin = 0; trait = 1;
		originalHP=1000; originalAD=70;originalAP=50; originalAS=1.5; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Jinx extends Champion{
	public Jinx(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("jinx.png");
		origin = 5; trait = 1;
		originalHP=1000; originalAD=70; originalAP=50; originalAS=1.5; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Yasuo extends Champion{
	public Yasuo(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("yasuo.png");
		origin = 0; trait = 5; 
		originalHP=1250; originalAD=50; originalAP=50; originalAS=1; originalArmor=10; originalMR=10;
		for (int i=1; i<lvl; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS*=1.3;
			originalArmor*=1.3;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
