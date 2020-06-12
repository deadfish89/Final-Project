import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.geom.RoundRectangle2D;

public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	
	private Timer timer, preRound, pause;
	private int nSeconds = 30;
	private boolean win = false, lose = false, inGame = false, paused = false, needReset = false;
	private ImageIcon victory = new ImageIcon("victory.png"), defeat = new ImageIcon("defeat.png");
	private final int time = 16;
	private ImageIcon bg, b;
	
	private int[] origins = new int[7];
	private int[] traits = new int[6]; 
	
	private int nBoardChamps = 0;
	private ArrayList<Champion> boardChamps = new ArrayList<Champion>();
	private ArrayList<Champion> enemyChamps = new ArrayList<>();
	
	private Player player; 
	private Champion[][] board = new Champion[10][3];
	private Champion[][] enemyBoard = new Champion[10][3];
	private Tile[][] field = new Tile[10][3];
	private Tile[][] enemyField = new Tile[10][3];
	private Tile[] benchField = new Tile[10];
	private Champion[] bench = new Champion[10];
	private Champion pickedChamp = null;
	private ArrayList<Champion> champs = new ArrayList<Champion>();
	private ArrayList<Champion> benchChamps = new ArrayList<Champion>();
	private int w, h;
	private int mX, mY, originalX, originalY;
	
	private ArrayList<Auto> autos = new ArrayList<Auto>();
	
	public Board(Player player){
	
		//panel
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(1000,800)); 
		w = 1000; h = 800; //this.getWidth() gives 0 for some reason
		
		this.player = player;
		
		/* temp add enemy champions */
		enemyChamps.add(new Lux(0,100,1));
		//enemyChamps.add(new Wukong(100,100,1));
		//enemyChamps.add(new Jinx(100,300,1));
		for (int i=0; i<enemyChamps.size(); i++){
			enemyBoard[0][i]=enemyChamps.get(i);
		}

		//initialize field
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				field[i][j] = new Tile(i*100, h/2 +j*100 );
				enemyField[i][j] = new Tile(i*100, j*100);
			}
		}	
		
		//initialize benchField
		for (int i=0; i<10; i++){
			benchField[i] = new Tile(i*100, h-100);
		}
		
		//background
		bg = new ImageIcon("TFT_EarthBoard.jpg");
		b = new ImageIcon("bench2.png");
		
		//event listeners
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		//timer
		timer = new Timer(time, this); 
		preRound = new Timer(1000, this);
		pause = new Timer(1000, this);
		preRound.start();
	}
	
	public void newRound(){
		needReset = true;
		for (int i=0; i<nBoardChamps; i++){
			boardChamps.get(i).reset();
		}
		player.gainGold(5+player.getGold()/10);
		player.gainXP(2);
		autos.clear();
	}
	
	public boolean needReset(){
		return needReset;
	}
	
	public void beenReset(){
		needReset = false;
	}
	
	public boolean benchFull(){
		for(int i=0;i < 10;i++){
			if(bench[i]==null)return false;
		}
		return true;
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
		int len = nBoardChamps;
		ArrayList<Champion> visited = new ArrayList<>();
		for (int i=0; i<len; i++){
			boolean isDuplicate = false;
			Champion cur = boardChamps.get(i);
			visited.add(cur);
			
			for (int j=0; j<visited.size(); j++){
				if (cur.getClass().equals(visited.get(i).getClass()))
					isDuplicate = true;
			}
			
			if (!isDuplicate){
			traits[cur.getTrait()]++;
			origins[cur.getOrigin()]++;
			}
		}
		for (int i=0; i<nBoardChamps; i++){
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
	
	public void rangedAttack(Champion attacker, Champion target){
		autos.add(new Auto(attacker, target));
	}

	public Champion findTarget(Champion attacker){
		double dis = 10000;
		Champion target = null;
		for (int i=0; i<enemyChamps.size(); i++){
			Champion cur = enemyChamps.get(i);
			if (cur.isAlive() && Math.sqrt(Math.pow(attacker.getX()-cur.getX(), 2) + Math.pow(attacker.getY()-cur.getY(), 2)) < dis) 
				target = cur;
		}
		return target;
	}
	
	public Champion enemyFindTarget(Champion attacker){
		double dis = 10000;
		Champion target = null;
		for (int i=0; i<nBoardChamps; i++){
			Champion cur = boardChamps.get(i);
			if (cur.isAlive() && Math.sqrt(Math.pow(attacker.getX()-cur.getX(), 2) + Math.pow(attacker.getY()-cur.getY(), 2)) < dis) 
				target = cur;
		}
		return target;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		Font font;
		
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
				
				if (enemyBoard[i][j]!=null && enemyBoard[i][j].isAlive()){
					enemyBoard[i][j].myDraw(g);
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
		
		//ranged auto attacks
		for (int i=0; i<autos.size(); i++){
			autos.get(i).myDraw(g);
		}
		
		//timer 
		if (!inGame || paused){
			g2.setStroke(new BasicStroke(3));
			
			g.setColor(new Color(205,133,63));
			g2.fillRoundRect(w/2-200, 50, 400, 80, 20, 20);
			
			g2.setColor(new Color(218,165,32));
			g2.drawRoundRect(w/2-200, 50, 400, 80, 20, 20);

			g.setColor(new Color(205,133,63));
			g.fillOval(w/2-50, 50, 100, 100);
			
			g2.setColor(new Color(218,165,32));
			g2.setStroke(new BasicStroke(6));
			g2.drawOval(w/2-50, 50, 100, 100);
			String displayTime = "" + nSeconds;
			g2.setColor(Color.WHITE);
			
			font = new Font("SansSerif", Font.PLAIN, 50);
			g2.setFont(font);
			if (nSeconds>=10) g2.drawString(displayTime, w/2-28, 118);
			else g2.drawString(displayTime, w/2-13, 118);
		}
		
		//win or lose
		if (win){
			g.drawImage(victory.getImage(), 100, 200, null);
		}
		else if (lose){
			g.drawImage(defeat.getImage(), 75, 175, null);
		}
	}
	
	//Action Listener
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==timer){
			
			if (!win && !lose){
				//update champions
				for (int i=0; i<nBoardChamps; i++){
					int nAlive = 0;
					for (int j=0; j<enemyChamps.size(); j++){
						if (enemyChamps.get(j).isAlive()) nAlive++;
					}
					if (nAlive>0){
						Champion cur = boardChamps.get(i);
						Champion target = this.findTarget(cur);
						cur.addTime(time);
						if (!cur.isStunned() && cur.isAlive()){
							if (cur.inAutoRange(target)){
								if (cur.hasAuto()){
									if (cur.getIsRanged())
										this.rangedAttack(cur, target);
									else {
										cur.hitsAuto(target);
									}
								}					
							}
							else cur.move(target);
						}
					}else{
						timer.stop();
						win = true;
						paused = true;
						nSeconds = 5;
						pause.start();
					}
				}
			
				//update enemy champions
				for (int i=0; i<enemyChamps.size(); i++){
					int nAlive = 0;
					for (int j=0; j<nBoardChamps; j++){
						if (boardChamps.get(j).isAlive()) nAlive++;
					}
					if (nAlive>0){
						Champion cur = enemyChamps.get(i);
						Champion target = this.enemyFindTarget(cur);
						cur.addTime(time);
						if (!cur.isStunned() && cur.isAlive()){
							if (cur.inAutoRange(target)){
								if (cur.hasAuto()){
									if (cur.getIsRanged())
										this.rangedAttack(cur, target);
									else {
										cur.hitsAuto(target);
									}
								}					
							}
							else cur.move(target);
						}
					}
					else{
						lose = true;
						paused = true;
						nSeconds = 5;
						pause.start();
					}
				}
			}
			else {
				inGame = false;
			}
			
			//update auto attacks
			for (int i=0; i<autos.size(); i++){
				Auto auto = autos.get(i);
				Champion attacker = auto.getAttacker(), target = auto.getTarget();
				if (auto.intersects(target.getHitBox())) {
					autos.remove(auto);
					attacker.hitsAuto(target);
				}
				else auto.move();
			}
		}
		//before the round starts
		else if (e.getSource()==preRound){
			if (nSeconds==0){
				//battle starts
				for (int i=0; i<nBoardChamps; i++){
					Champion cur = boardChamps.get(i);
					cur.setOriginalPos(cur.getX(), cur.getY());
				}
				inGame = true;
				preRound.stop();
				timer.start();
			}
			else nSeconds--;
		}
		else if (e.getSource()==pause){
			if (nSeconds==0){
				//preround starts again
				pause.stop();
				win = false; lose = false;
				inGame = false;
				nSeconds = 30;
				paused = false;
				this.newRound();
				preRound.start();
			}
			else nSeconds--;
		}
		repaint();
	}
	
	//Mouse Listener
	public void mousePressed(MouseEvent e) {
		mX = e.getX();
		mY = e.getY();
		
		if (!inGame && !paused){
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
		}
			
		//if mouse presses a champion on bench
		for (int i=0; i<benchChamps.size(); i++){
			if (benchChamps.get(i).contains(mX, mY)){
				pickedChamp = benchChamps.get(i);
				originalX = pickedChamp.getX(); 
				originalY = pickedChamp.getY();
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
		boolean placed = false;

		//champion released on board
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				if (field[i][j].contains(mX, mY) && pickedChamp!=null){
					onTile = true;
					if (field[i][j].isEmpty()){
						//check if champion was from bench
						for (int k=0; k<10; k++){
							if (benchField[k].contains(originalX, originalY)){
								//if board is not full
								if (nBoardChamps<player.getLevel() && !inGame && !paused){
									this.addToBoard(pickedChamp);
									benchChamps.remove(pickedChamp);
									this.refreshTraits();
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
		
		//champion released on bench
		for (int i=0; i<10; i++){
				if (benchField[i].contains(mX, mY) && pickedChamp!=null){
						
					onTile = true;
					if (benchField[i].isEmpty()){
						pickedChamp.setPos(benchField[i].getX(), benchField[i].getY());
						benchField[i].addChamp();
						bench[i]=pickedChamp;
						
						//if champion was from board
						for (int j=0; j<10; j++){
							for (int k=0; k<3; k++){
								if (field[j][k].contains(originalX, originalY)){
									this.removeFromBoard(pickedChamp);
									benchChamps.add(pickedChamp);
									this.refreshTraits();
								}
							}
						}
					}
					//tile is taken by another champion
					else{
						pickedChamp.setPos(originalX, originalY);
						for (int j=0; j<10; j++){
							if (benchField[j].contains(originalX, originalY)){
								benchField[j].addChamp();
								bench[j] = pickedChamp;
							}
							for (int k=0; k<3; k++){
								if (field[j][k].contains(originalX, originalY)){
									field[j][k].addChamp();
									board[j][k] = pickedChamp;
								}
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

class Auto extends Rectangle{
	
	private int vel;
	private int x, y, dX, dY;
	private double vX, vY;
	private double angle;
	private Champion attacker, target;
	
	public Auto(Champion attacker, Champion target){
		super(attacker.getX()+50, attacker.getY()+50, 5, 5);
		vel = 10;
		this.attacker = attacker;
		this.target = target;
		x = attacker.getX()+50;
		y = attacker.getY()+50;
	}
	
	public void move(){
		dX = target.getX()+50-x;
		dY = target.getY()+50-y;
		angle = Math.atan2(dY, dX)*(180/Math.PI);
		vX = (vel*(90-Math.abs(angle))/90);
		if (angle<=0) vY = Math.abs(vX)-vel;
		else vY = vel-Math.abs(vX);
		
		x += vX;
		y += vY;
		super.setLocation(x, y);
	}
	
	public Champion getAttacker(){
		return attacker;
	}
	
	public Champion getTarget(){
		return target;
	}
	
	public void myDraw(Graphics g) {
		ImageIcon img = new ImageIcon("auto.png"); 
    	g.drawImage(img.getImage(), x, y, null);
    }
}

class Champion implements ActionListener{
	
	protected boolean alive = true;
	
	protected int x, y, originalX, originalY;
	protected ImageIcon image;
	protected int hp=0, lvl=0, ad=0, ap=0, armor = 0, mr = 0, curHP = 0, mana = 0, curMana = 0, range = 0;
	protected double as = 0, originalAS;
	protected int originalHP, originalAD, originalMR, originalAP, originalArmor;
	protected int trait = 0, origin = 0;
	protected boolean isRanged;
	protected int vel = 5;
	protected boolean blademaster, void1, glacial, hextech, demon;
	protected int isHit = 0, isStunned = 0;
	private Timer hit, stunned;
	protected ImageIcon slash = new ImageIcon("slash.jpg"), stun;
	
	protected Rectangle hitBox;
	protected int time = 0;

	public Champion(int x, int y, int level){
		
		this.x = x;
		this.y = y;
		hitBox = new Rectangle(x, y, 100, 100);
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
	}
	
	public void reset(){
		hp = originalHP;
		curMana = 0;
		this.returnToOriginal();
		isStunned = 0;
		isHit = 0;
		alive = true;
	}
	
	public void addTime(int time){
		this.time += time;
	}
	
	public boolean hasAuto(){
		if (time>=1000/as){
			time = 0;
			return true;
		}
		return false;
	}
	
	public void getStunned(double duration){
		isStunned++;
		stun = new ImageIcon("stun1.png");
		stunned = new Timer((int)(1000*duration), this);
		stunned.start();
	}
	
	public boolean isStunned(){
		return (isStunned>0);
	}
	
	public void getHit(){
		isHit++; 
		if ((int)(Math.random()*2)==0) slash = new ImageIcon("slash.png");
		else slash = new ImageIcon("slash2.png");
		hit = new Timer(500, this);
		hit.start();
	}
	
	public void takeDmg(int dmg){
		curHP-=dmg;
		if (curHP<=0){
			alive = false;
		}
	}
	
	public void hitsAuto(Champion target){
		curMana+=10;
		int targetArmor = target.getArmor();
		int damage = 0;
		if (glacial){
			if ((int)(Math.random()*3)==0) target.getStunned(1);
		}
		if (void1){
			targetArmor = 0;
		}
		if (demon){
			target.setMana(target.getCurMana()-20);
		}
		damage = ad-ad*targetArmor/150;
		target.takeDmg(damage);
		if (!isRanged) target.getHit();
		
		if (blademaster) {
			if ((int)(Math.random()*3)==0){
				target.takeDmg(damage);
				target.getHit();
			}
		}
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
	
	public void move(Champion target){
		int dX = target.getX()+50-(x+50);
		int dY = target.getY()+50-(y+50);
		double angle = Math.atan2(dY, dX)*(180/Math.PI);
		int vX = (int)(vel*(90-Math.abs(angle))/90);
		int vY = 0;
		if (angle<0) vY = Math.abs(vX)-vel;
		else vY = vel-Math.abs(vX);
		
		x += vX;
		y += vY;
	}
	
	public void setOriginalPos(int x, int y){
		originalX = x; 
		originalY = y;
	}
	
	public void returnToOriginal(){
		x = originalX;
		y = originalY;
	}
	
	public boolean inAutoRange(Champion target){
		int dX = x-target.getX();
		int dY = y-target.getY();
		return (Math.sqrt(Math.pow(dX,2)+Math.pow(dY,2))<=range);
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
	
	public Rectangle getHitBox(){
		hitBox.setBounds(x, y, 100, 100);
		return hitBox;
	}
	
	public boolean getIsRanged(){
		return isRanged;
	}
	
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
		return alive;
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
		if (isHit>0){
			g.drawImage(slash.getImage(), x, y, null);
		}
		else if (isStunned>0) g.drawImage(stun.getImage(), x, y-30, null);
    }
}

class Annie extends Champion{
	
	public Annie(int x, int y, int level){
		super(x, y, level);
		image = new ImageIcon("annie.png");
		origin = 0; trait = 0;
		isRanged = true;
		originalHP=1500; originalAD=100; originalAP=100; originalAS=1; originalArmor=10; originalMR=10; range = 250;
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
		isRanged = false;
		originalHP=2000; originalAD=150; originalAP=100; originalAS=1; originalArmor=30; originalMR=30; range = 50;
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
		isRanged = false;
		originalHP=2500; originalAD=100; originalAP=100; originalAS=1; originalArmor=30; originalMR = 30; range = 50;
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
		isRanged = true;
		originalHP=1500; originalAD=100; originalAP=100; originalAS=1; originalArmor=10; originalMR=10; range = 300;
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
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR = 10; range = 300;
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
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR = 10; range = 300;
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
		isRanged = false;
		originalHP=2000; originalAD=50; originalAP=75; originalAS=1; originalArmor=30; originalMR=30; range = 50;
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
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR=10; range = 300;
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
		isRanged = true;
		originalHP=1250; originalAD=100; originalAP=75; originalAS=1.5; originalArmor=10; originalMR=10; range = 300;
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
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR=10; range = 300;
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
		isRanged = false;
		originalHP=1500; originalAD=70; originalAP=75;originalAS=1; originalArmor=20; originalMR=20; range = 50;
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
		isRanged = false;
		originalHP=1250; originalAD=150; originalAP=75; originalAS=1; originalArmor=10; originalMR=10; range = 50;
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
		isRanged = false;
		originalHP=1500; originalAD=70; originalAP=75; originalAS=1.5; originalArmor=10; originalMR=10; range = 50;
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
		isRanged = false;
		originalHP=2000; originalAD=50; originalAP=75; originalAS=1; originalArmor=30; originalMR=30; range = 40;
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
		isRanged = false;
		originalHP=1500; originalAD=50; originalAP=50; originalAS=1; originalArmor=15; originalMR=15; range = 40;
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
		isRanged = false;
		originalHP=1250; originalAD=50; originalAP=50; originalAS=1; originalArmor=10; originalMR=20; range = 50;
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
		isRanged = true;
		originalHP=1000; originalAD=70;originalAP=50; originalAS=1.5; originalArmor=10; originalMR=10; range = 300;
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
		isRanged = true;
		originalHP=1000; originalAD=70; originalAP=50; originalAS=1.5; originalArmor=10; originalMR=10; range = 300;
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
		isRanged = false;
		originalHP=1250; originalAD=50; originalAP=50; originalAS=1; originalArmor=10; originalMR=10; range = 50;
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