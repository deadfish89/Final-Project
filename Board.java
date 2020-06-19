import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Board extends JPanel implements ActionListener, MouseListener, MouseMotionListener{
	
	private Timer timer, preRound, pause, hoverTimer = new Timer(1000, this);
	private int nSeconds = 30;
	private boolean win = false, lose = false, inGame = false, paused = false, needReset = false;
	public boolean needUpdate = false;
	private ImageIcon victory = new ImageIcon("victory.png"), defeat = new ImageIcon("defeat.png");
	private final int time = 16;
	private ImageIcon bg, b, bg2;
	
	private int[] origins = new int[7], traits = new int[6], activeOrigins = new int[7], activeTraits = new int[6], enemyOrigins = new int[7], enemyTraits = new int[6];

	
	public static int nBoardChamps = 0;
	public static ArrayList<Champion> boardChamps = new ArrayList<>();
	public static ArrayList<Champion> enemyChamps = new ArrayList<>();
	
	private Player player; 
	private Champion[][] board = new Champion[10][3];
	private Champion[][] enemyBoard = new Champion[10][3];
	private Tile[][] field = new Tile[10][3];
	private Tile[][] enemyField = new Tile[10][3];
	private Tile[] benchField = new Tile[10];
	private Champion[] bench = new Champion[10];
	public static Champion pickedChamp = null, hoverChamp = null;
	private ArrayList<Champion> champs = new ArrayList<Champion>();
	private ArrayList<Champion> benchChamps = new ArrayList<Champion>();
	public static int w, h;
	private int mX, mY, originalX, originalY;
	
	private ArrayList<Auto> autos = new ArrayList<Auto>();
	
	public Board(Player player) throws FileNotFoundException{
	
		//panel
		this.setPreferredSize(new Dimension(850,595)); 
		w = 850; h = 595; //this.getWidth() gives 0 for some reason
		
		this.player = player;
		EnemyTeam.loadTeams();
		enemyChamps = EnemyTeam.getTeam(1,this);
		/* temp add enemy champions 
		enemyChamps.add(new Ashe(0,85,1, this));
		enemyChamps.add(new Braum(85,85,1, this));
		enemyChamps.add(new Jinx(85,170,1, this)); */
		for (int i=0; i<enemyChamps.size(); i++){
			enemyBoard[0][i]=enemyChamps.get(i);
			enemyChamps.get(i).isEnemyChamp();
			enemyChamps.get(i).isOnBoard(true);
		}
		this.refreshTraits();

		//initialize field
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				field[i][j] = new Tile(i*85-1, (h-85)/2 +j*85);
				enemyField[i][j] = new Tile(i*85, j*85);
			}
		}	
		
		//initialize benchField
		for (int i=0; i<10; i++){
			benchField[i] = new Tile(i*85, h-85);
		}
		
		//background
		bg = new ImageIcon("TFT_EarthBoard.jpg");
		bg2 = new ImageIcon("background.png");
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
	
	//reset board for a new a new round
	private void newRound(){
		needReset = true;
		for (int i=0; i<nBoardChamps; i++){
			boardChamps.get(i).reset();
		}
		//player gets gains gold as xp 
		player.gainGold(5+player.getGold()/10);
		player.gainXP(2);
		
		autos.clear();
		refreshTraits();
	}
	
	public boolean needReset(){
		return needReset;
	}
	
	public void beenReset(){
		needReset = false;
	}
	
	//create a new higher leveled champion after combining 3
	public boolean summonChamp(int champ, int level){
		int x = 0, y = 0, index = 0;
		if (benchChamps.size()<10){
			for (int i=0; i<10; i++){
					if (bench[i]==null){
						x = i*85;
						y = h-85;
						index = i;
						break;
					}
			}
		
			Champion temp = SummonChamp.summon(champ,x,y,level,this);
			while(checkLevel(temp)){
				level++;
				temp = SummonChamp.summon(champ,x,y,level,this);
			}
			bench[index] = temp;
			benchField[index].addChamp();
			benchChamps.add(bench[index]);
			champs.add(bench[index]);
			
			repaint();
			return true;
		}
		return false;
	}
	
	//check if there are champions to combine
	public boolean checkLevel(Champion champ){
		int numEqual = 0;
		for(Champion champion:champs){
			if(champ.equals(champion)){
				numEqual++;
			}
		}
		//if there are 3 of the same champions, remove them 
		if(numEqual == 2){
			for(int i = 0;i < 10;i++){
				if(bench[i]!=null){
					if(bench[i].equals(champ)){
						benchChamps.remove(bench[i]);
						benchField[i].removeChamp();
						champs.remove(bench[i]);
						bench[i]=null;
					}
				}
				for(int j = 0;j < 3;j++){
					if(board[i][j]!=null){
						if(board[i][j].equals(champ)){
							this.removeFromBoard(board[i][j]);
							field[i][j].removeChamp();
							champs.remove(board[i][j]);
							board[i][j] = null;
							this.refreshTraits();
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	
	//when champion comes from bench to board
	public void addToBoard(Champion champ){
		if (nBoardChamps<player.getLevel()){
			boardChamps.add(champ);
			pickedChamp.isOnBoard(true);
			nBoardChamps++;
		}
	}
	
	//when champion goes from board to bench
	public void removeFromBoard(Champion champ){
		boardChamps.remove(champ);
		champ.isOnBoard(false);
		nBoardChamps--;
	}
	
	//finds all active traits on both the player and the enemy's board
	public void refreshTraits(){
		Arrays.fill(traits, 0);
		Arrays.fill(origins, 0);
		Arrays.fill(activeTraits, 0);
		Arrays.fill(activeOrigins, 0);
		
		//Player's Champions
		ArrayList<Champion> visited = new ArrayList<>();
		for (int i=0; i<nBoardChamps; i++){
			boolean isDuplicate = false;
			Champion cur = boardChamps.get(i);
			
			//preventing duplicates
			for (int j=0; j<visited.size(); j++){
				if (cur.getClass().equals(visited.get(j).getClass()))
					isDuplicate = true;
			}
			
			//counting traits/origins for unique champions
			if (!isDuplicate){
				traits[cur.getTrait()]++;
				origins[cur.getOrigin()]++;
			}
			visited.add(cur);
		}
		
		for (int i=0; i<nBoardChamps; i++){
			Champion cur = boardChamps.get(i);
			cur.reset();
			int trait = cur.getTrait();
			int origin = cur.getOrigin();
			//activate traits
			switch (trait){
				case 0:
					if (traits[trait]==6){
						cur.setAP(cur.getAP()*2);
						activeTraits[trait] = 3;
					}
					else if(traits[trait]>=3){
						cur.setAP((int)(cur.getAP()*1.3));
						activeTraits[trait] = 2;
					}
					break;
				case 1:
					if (traits[trait]==4){
						cur.setAS(cur.getAS()*1.85);
						activeTraits[trait] = 3;
					}
					else if(traits[trait]>=2){
						cur.setAS(cur.getAS()*1.25);
						activeTraits[trait] = 2;
					}
					break;
				case 2:
					if (traits[trait]==1){
						cur.setArmor((int)(cur.getArmor()*1.3));
						activeTraits[trait] = 1;
					}
					else if(traits[trait]==2){
						cur.setArmor(cur.getArmor()*2);
						activeTraits[trait] = 2;
					}
					else if(traits[trait]==3){
						cur.setArmor(cur.getArmor()*3);
						activeTraits[trait] = 3;
					}
					break;
				case 3:
					if (traits[trait]==3){
						cur.setHP(cur.getHP()*2);
						activeTraits[trait] = 3;
					}
					break;
				case 4:
					cur.setVel(cur.getVel()*2);
					activeTraits[trait] = 3;
					break;
				case 5:
					if (traits[trait]==3){ 
						cur.hasBlademaster(true);
						activeTraits[trait] = 3;
					}
					else cur.hasBlademaster(false);
					break;
			}
			
			//activate origins
			switch(origin){
				case 0:
					if (origins[origin]==7){
						cur.setArmor(cur.getArmor()*2);
						cur.setMR(cur.getMR()*2);
						activeOrigins[origin] = 3;
					}
					else if(origins[origin]>=5){
						cur.setArmor((int)(cur.getArmor()*1.3));
						cur.setMR((int)(cur.getMR()*1.3));
						activeOrigins[origin] = 2;
					}
					break;
				case 1:
					if (origins[origin]==2){
						cur.hasGlacial(true);
						activeOrigins[origin] = 3;
					}else cur.hasGlacial(false);
					break;
				case 2:
					if (origins[origin]==3){
						cur.hasDemon(true);
						activeOrigins[origin] = 3;
					}
					else cur.hasDemon(false);
					break;
				case 3:
					if (origins[origin]==2){
						cur.setAD(cur.getAD()*2);
						cur.setAP(cur.getAP()*2);
						activeOrigins[origin] = 3;
					}
					break;
				case 4:
					if (origins[origin]>=3){
						cur.hasVoid(true);
						activeOrigins[origin] = 3;
					}else cur.hasVoid(false);
					break;
				case 5:
					if (origins[origin]==2) {
						cur.hasHextech(true);
						activeOrigins[origin] = 3;
					}else cur.hasHextech(false);
					break;
				case 6:
					int count = 0;
					for (int j=0; j<enemyChamps.size(); j++){
						if (enemyChamps.get(j).isAlive()) count++;
					}
					if (count>=3) {
						cur.setArmor(cur.getArmor()*2); 
					}
					activeOrigins[origin] = 3;
					break;
			}
			}
			
			//Enemy's Champions
			visited.clear();
			Arrays.fill(enemyTraits, 0);
			Arrays.fill(enemyOrigins, 0);
			for (int i=0; i<enemyChamps.size(); i++){
				boolean isDuplicate = false;
				Champion cur = enemyChamps.get(i);
				
				//preventing duplicates
				for (int j=0; j<visited.size(); j++){
					if (cur.getClass().equals(visited.get(j).getClass()))
						isDuplicate = true;
				}
				
				//counting traits/origins of unique champions
				if (!isDuplicate){
					enemyTraits[cur.getTrait()]++;
					enemyOrigins[cur.getOrigin()]++;
				}
				visited.add(cur);
			}
			for (int i=0; i<enemyChamps.size(); i++){
			Champion cur = enemyChamps.get(i);
			cur.reset();
			int trait = cur.getTrait();
			int origin = cur.getOrigin();
			//activating traits
			switch (trait){
				case 0:
					if (enemyTraits[trait]==6){
						cur.setAP(cur.getAP()*2);
					}
					else if(enemyTraits[trait]>=3){
						cur.setAP((int)(cur.getAP()*1.3));
					}
					break;
				case 1:
					if (enemyTraits[trait]==4){
						cur.setAS(cur.getAS()*1.85);
					}
					else if(enemyTraits[trait]>=2){
						cur.setAS(cur.getAS()*1.25);
					}
					break;
				case 2:
					if (enemyTraits[trait]==1){
						cur.setArmor((int)(cur.getArmor()*1.3));
					}
					else if(enemyTraits[trait]==2){
						cur.setArmor(cur.getArmor()*2);
					}
					else if(enemyTraits[trait]==3){
						cur.setArmor(cur.getArmor()*3);
					}
					break;
				case 3:
					if (enemyTraits[trait]==3){
						cur.setHP(cur.getHP()*2);
					}
					break;
				case 4:
					cur.setVel(cur.getVel()*2);
					break;
				case 5:
					if (enemyTraits[trait]==3){ 
						cur.hasBlademaster(true);
					}
					else cur.hasBlademaster(false);
					break;
			}
			
			//activating origins
			switch(origin){
				case 0:
					if (enemyOrigins[origin]==7){
						cur.setArmor(cur.getArmor()*2);
						cur.setMR(cur.getMR()*2);
					}
					else if(enemyOrigins[origin]>=5){
						cur.setArmor((int)(cur.getArmor()*1.3));
						cur.setMR((int)(cur.getMR()*1.3));
					}
					break;
				case 1:
					if (enemyOrigins[origin]==2){
						cur.hasGlacial(true);
					}else cur.hasGlacial(false);
					break;
				case 2:
					if (enemyOrigins[origin]==3){
						cur.hasDemon(true);
					}
					else cur.hasDemon(false);
					break;
				case 3:
					if (enemyOrigins[origin]==2){
						cur.setAD(cur.getAD()*2);
						cur.setAP(cur.getAP()*2);
					}
					break;
				case 4:
					if (enemyOrigins[origin]>=3){
						cur.hasVoid(true);
					}else cur.hasVoid(false);
					break;
				case 5:
					if (enemyOrigins[origin]==2) {
						cur.hasHextech(true);
					}else cur.hasHextech(false);
					break;
				case 6:
					int count = 0;
					for (int j=0; j<enemyChamps.size(); j++){
						if (boardChamps.get(j).isAlive()) count++;
					}
					if (count>=3) {
						cur.setArmor(cur.getArmor()*2); 
					}
					break;
			}
			}
			
		}
		
	public boolean inPrepPhase(){
		return (!inGame && !paused);
	}
	
	public boolean isInGame(){
		return inGame;
	}

	public int[] getActiveTraits(){
		return activeTraits;
	}
	
	public int[] getActiveOrigins(){
		return activeOrigins;
	}
	
	public int[] getTraits(){
		return traits;
	}
	
	public int[] getOrigins(){
		return origins;
	}
	
	private void rangedAttack(Champion attacker, Champion target){
		autos.add(new Auto(attacker, target));
	}

	//find closeset target for player attacker
	public Champion findTarget(Champion attacker){
		double dis = 10000;
		Champion target = null;
		for (int i=0; i<enemyChamps.size(); i++){
			Champion cur = enemyChamps.get(i);
			double curDis = Math.sqrt(Math.pow(attacker.getX()-cur.getX(), 2) + Math.pow(attacker.getY()-cur.getY(), 2));
			if (cur.isAlive() && curDis < dis){
				dis = curDis;
				target = cur;
			}
		}
		return target;
	}
	
	//find closest target for enemy attacker
	public Champion enemyFindTarget(Champion attacker){
		double dis = 10000;
		Champion target = null;
		for (int i=0; i<nBoardChamps; i++){
			Champion cur = boardChamps.get(i);
			double curDis = Math.sqrt(Math.pow(attacker.getX()-cur.getX(), 2) + Math.pow(attacker.getY()-cur.getY(), 2));
			if (cur.isAlive() && curDis < dis){
				dis = curDis;
				target = cur;
			}
		}
		return target;
	}
	
	//find the furthest opposing champion on the board
	public Champion findFurthest(Champion attacker, boolean isEnemy){
		double dis = -1;
		Champion target = null;
		if (isEnemy){
			for (int i=0; i<nBoardChamps; i++){
				Champion cur = boardChamps.get(i);
				double curDis = Math.sqrt(Math.pow(attacker.getX()-cur.getX(), 2) + Math.pow(attacker.getY()-cur.getY(), 2)); //distance formula
				if (cur.isAlive() && curDis > dis){
					dis = curDis;
					target = cur;
				}
			}
		}else {
			for (int i=0; i<enemyChamps.size(); i++){
				Champion cur = enemyChamps.get(i);
				double curDis = Math.sqrt(Math.pow(attacker.getX()-cur.getX(), 2) + Math.pow(attacker.getY()-cur.getY(), 2)); //distance formula
				if (cur.isAlive() && curDis > dis){
					dis = curDis;
					target = cur;
				}
			}
		}
		return target;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform originalAngle = g2.getTransform();
		Paint originalPaint = g2.getPaint();
		Stroke originalStroke = g2.getStroke();
		
		Font font;
		g.setColor(Color.BLACK);

		//background
		g.drawImage(bg.getImage(), 0, -1, w, h-83, null);
		
		//benches
		for (int i=0; i<10; i++){
			g.drawImage(b.getImage(), i*85, h-85, null);
			if (pickedChamp!=null){
				g.drawLine(i*85, h, i*85, h-85);
			}
		}
		
		//champions on the board
		for (int i=0; i<10; i++){
			for (int j=0; j<3; j++){
				//if there is a champion and it's alive 
				if (board[i][j]!=null && board[i][j].isAlive()){
					board[i][j].myDraw(g);
					board[i][j].drawAbility(g2);
					//reset g2 after drawing ability
					g2.setTransform(originalAngle);
					g2.setPaint(originalPaint);
					g2.setStroke(originalStroke);
				//when a champion is being moved, show empty tiles
				}else if (pickedChamp!=null){
					field[i][j].myDraw(g);
				}
				
				//if there is an enemy champion and it's alive 
				if (enemyBoard[i][j]!=null && enemyBoard[i][j].isAlive()){
					enemyBoard[i][j].myDraw(g);
					enemyBoard[i][j].drawAbility(g2);
					//reset g2 after drawing ability
					g2.setTransform(originalAngle);
					g2.setPaint(originalPaint);
					g2.setStroke(originalStroke);
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
		
		//in prep phase, draw timer/round/number of champions icon
		if (!inGame || paused){
			g2.setStroke(new BasicStroke(3));
			//make drawing transparent
			float alpha = 0.75f;
			int type = AlphaComposite.SRC_OVER; 
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			Composite originalComposite = g2.getComposite();
			
			g2.setComposite(composite);
			g2.setColor(new Color(205,133,63));
			g2.fillRoundRect(w/2-200, 50, 400, 80, 20, 20);
			
			g2.setComposite(originalComposite);
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
			g2.setStroke(originalStroke);
		}
		
		//draw champion being hovered again to make sure it draws over everything else
		if (hoverChamp!=null)
			hoverChamp.myDraw(g);
		
		//victory/defeat screen
		if (win){
			g.drawImage(victory.getImage(), 125, 185, null);
		}
		else if (lose){
			g.drawImage(defeat.getImage(), 125, 185, null);
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
					//if there are still enemies alive
					if (nAlive>0){
						Champion cur = boardChamps.get(i);
						Champion target = this.findTarget(cur);
						//update auto attack timer
						cur.addTime(time);
						//if not stunned and is alive, try to use ability
						if (!cur.isStunned() && cur.isAlive()){
							cur.useAbility();
							//if in auto attack range, autoattack
							if (cur.inAutoRange(target)){
								if (cur.hasAuto()){
									if (cur.getIsRanged())
										this.rangedAttack(cur, target);
									else {
										cur.hitsAuto(target);
									}
								}					
							}
							//not in auto attack range, move closer to target
							else cur.move(target);
						}
						//check if a jinx assisted the kill
						else if (!cur.isAlive()){
							for (int j=0; j<enemyChamps.size(); j++){
								if (enemyChamps.get(j).getName().equals("Jinx")){
									enemyChamps.get(j).checkIfAssisted(cur);
								}
							}
						}
					}else{
						//player wins
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
					//if there are still player champions alive
					if (nAlive>0){
						Champion cur = enemyChamps.get(i);
						Champion target = this.enemyFindTarget(cur);
						//update autoattack timer
						cur.addTime(time);
						//if not stunned and is alive, try to use ability
						if (!cur.isStunned() && cur.isAlive()){
							cur.useAbility();
							//if in auto attack range, autoattack
							if (cur.inAutoRange(target)){
								if (cur.hasAuto()){
									if (cur.getIsRanged())
										this.rangedAttack(cur, target);
									else {
										cur.hitsAuto(target);
									}
								}					
							}
							//not in auto attack range, move closer to target
							else cur.move(target);
						}
						//check if a jinx assisted the kill
						else if (!cur.isAlive()){
							for (int j=0; j<nBoardChamps; j++){
								if (boardChamps.get(j).getName().equals("Jinx")){
									boardChamps.get(j).checkIfAssisted(cur);
								}
							}
						}
					}
					else{
						//player loses
						timer.stop();
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
				//collision detection
				if (auto.intersects(target.getHitBox())) {
					autos.remove(auto);
					attacker.hitsAuto(target);
				}
				else auto.move();
			}
		}
		//before the round starts
		else if (e.getSource()==preRound){
			//battle starts
			if (nSeconds==0){
				//sets every champion's original position
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
		//in between rounds
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
		else if (e.getSource()==hoverTimer){
			//if mouse is in screen, display stats
			if (mX>=0 && mY>=0)
				hoverChamp.displayStats(true);
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
    public void mouseExited(MouseEvent e){
		//give mX and mY impossible values if they leave panel
		if (e.getSource()==this){
			mX=-100;
			mY=-100;
		}
	}
	
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
									this.addToBoard(pickedChamp); //for traits/origins
									pickedChamp.isOnBoard(true);
									benchChamps.remove(pickedChamp);
									this.refreshTraits();
								//else return to original position
								}else {
									pickedChamp.setPos(originalX, originalY);
									benchField[k].addChamp();
									bench[k] = pickedChamp;
									boardFull = true;
								}
							}
						}
						//add champion to board if not full
						if (!boardFull){
							pickedChamp.setOriginalPos(field[i][j].getX(), field[i][j].getY());
							pickedChamp.setPos(field[i][j].getX(), field[i][j].getY());
							field[i][j].addChamp();
							board[i][j]=pickedChamp;
						}
					}
					//tile is taken by another champion
					else{
						//return to original position
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
			//reset hoverTimer if released 
			if (hoverChamp!=null){
				hoverChamp.displayStats(false);
				hoverChamp = pickedChamp;
				hoverTimer.stop();
				hoverTimer.start();
			}	
		}
		
		//champion released on bench
		for (int i=0; i<10; i++){
				if (benchField[i].contains(mX, mY) && pickedChamp!=null){
						
					onTile = true;
					//put on bench if empty
					if (benchField[i].isEmpty()){
						pickedChamp.setPos(benchField[i].getX(), benchField[i].getY());
						benchField[i].addChamp();
						bench[i]=pickedChamp;
						
						//if champion was from board
						for (int j=0; j<10; j++){
							for (int k=0; k<3; k++){
								if (field[j][k].contains(originalX, originalY)){
									this.removeFromBoard(pickedChamp); //for traits/origins
									benchChamps.add(pickedChamp);
									this.refreshTraits();
								}
							}
						}
					}
					//tile is taken by another champion
					else{
						//return to original position
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
			//reset hoverTimer if released 
			if (hoverChamp!=null){
				hoverChamp.displayStats(false);
				hoverChamp = pickedChamp;
				hoverTimer.stop();
				hoverTimer.start();
			}	
		}
		
		//sell champions if they are released on sell button
		if(0<mX && mX<105 && 595<mY && mY<700){
			player.gainGold(pickedChamp.getCost());
			for(int i = 0;i < 10;i++){
				if(benchField[i].contains(originalX,originalY)){
					benchChamps.remove(pickedChamp);
				}
				for(int j = 0;j < 3;j++){
					if(field[i][j].contains(originalX,originalY)){
						this.removeFromBoard(pickedChamp);
						this.refreshTraits();
					}
				}
			}
			pickedChamp.setPos(0,700);
			needUpdate=true;
			System.out.println(needUpdate);
			pickedChamp=null;
		}
		
		//if champion isn't released on any tile
		if (pickedChamp!=null && !onTile){
			//return to it's original position
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

		//dragging champion
		if(pickedChamp!=null) {
			pickedChamp.setPos(e.getX(), e.getY());
		}
		repaint();
	}
	
	public void mouseMoved(MouseEvent e) {
		 mX = e.getX(); mY = e.getY();
		
		//if mouse moves, stop displaying
		if (hoverChamp!=null){
			hoverTimer.stop();
			hoverChamp.displayStats(false);
			hoverChamp = null;
		}
		
		//if mouse hovers a player champion on the board
		for (int i=0; i<nBoardChamps; i++){
			Champion cur = boardChamps.get(i);
			if (cur.contains(mX, mY)){
				hoverTimer.start();
				hoverChamp = cur;
			}
		}
		//if mouse hovers an enemy champion on the board
		for (int i=0; i<enemyChamps.size(); i++){
			Champion cur = enemyChamps.get(i);
			if (cur.contains(mX, mY)){
				hoverTimer.start();
				hoverChamp = cur;
			}
		}
		//if mouse hovers a bench champion 
		for (int i=0; i<benchChamps.size(); i++){
			Champion cur = benchChamps.get(i);
			if (cur.contains(mX, mY)){
				hoverTimer.start();
				hoverChamp = cur;
			}
		}
		repaint();
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
	
	//check if the tile is taken up by a champion
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
    	return (mX>=x && mX<x+85 && mY>=y && mY<y+85);
    }
	
	public void myDraw(Graphics g) {
		g.setColor(Color.BLACK);
    	g.drawRect(x, y, 85, 85);
    }
}

class Auto extends Rectangle{
	
	private int vel;
	private int x, y, dX, dY;
	private double vX, vY;
	private double angle;
	private Champion attacker, target;
	
	public Auto(Champion attacker, Champion target){
		super(attacker.getX()+42, attacker.getY()+42, 5, 5);
		vel = 10;
		this.attacker = attacker;
		this.target = target;
		x = attacker.getX()+42;
		y = attacker.getY()+42;
	}
	
	//bullet moves towards target
	public void move(){
		dX = target.getX()+42-x;
		dY = target.getY()+42-y;
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
	
	protected Board board;
	protected int x, y, originalX, originalY;
	protected ImageIcon image, star = new ImageIcon("star.png");
	protected String name;
	protected int hp=0, level=0, ad=0, ap=0, armor = 0, mr = 0, curHP = 0, mana = 0, curMana = 0, range = 0, cost = 0, originalHP, originalAD, originalMR, originalAP, originalArmor;
	protected double as = 0, originalAS;

	protected int trait = 0, origin = 0, rarity = -1;
	protected boolean isRanged, isEnemy = false, displayStats = false;
	protected int vel = 3;
	protected Rectangle hitBox;
	
	protected boolean alive = true, onBoard = false;
	protected boolean blademaster, void1, glacial, hextech, demon;
	protected int isHit = 0, isStunned = 0, isDamaged = 0;
	protected ArrayList<Integer> damageTaken = new ArrayList<>(), damageType = new ArrayList<>();
	protected ArrayList<Timer> timers = new ArrayList<>();
	protected Timer hit, stunned, damaged;
	private ImageIcon slash = new ImageIcon("slash.jpg"), stun;
	protected int time = 0, nTimers = 0;

	public Champion(int x, int y, int level, Board board){
		this.board = board;
		this.x = x;
		this.y = y;
		this.level = level;
		originalX = x;
		originalY = y;
		hitBox = new Rectangle(x, y, 85, 85);
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
	}
	
	public boolean equals(Champion champion){
		return (name==champion.getName() && level==champion.getLevel());
	}	
	
	//reset champion to original state
	public void reset(){
		hp=originalHP; curHP = hp; ad=originalAD; ap=originalAP; armor =originalArmor; mr = originalMR; as = originalAS;
		curHP = hp;
		curMana = 0;
		this.returnToOriginal();
		isStunned = 0;
		isHit = 0;
		alive = true;
	}
	
	//counts time 
	public void addTime(int time){
		this.time += time;
	}
	
	//Auto attacker timer - the higher the as(attackspeed), the faster the champion attacks
	public boolean hasAuto(){
		if (time>=1000/as){
			time = 0;
			return true;
		}
		return false;
	}
	
	//this champion gets stunned 
	public void getStunned(double duration){
		isStunned++;
		stun = new ImageIcon("stun1.png");
		//duration is number of seconds
		stunned = new Timer((int)(1000*duration), this);
		stunned.start();
	}

	
	//slash animation
	public void getHit(){
		isHit++; 
		if ((int)(Math.random()*2)==0) slash = new ImageIcon("slash.png");
		else slash = new ImageIcon("slash2.png");
		hit = new Timer(500, this);
		hit.start();
	}
	
	//this champion takes damage
	public int takeDmg(int damage, int type){
		//physical damage
		if (type==1){
			damage -= damage*armor/150;
		}
		//magical damage 
		else if (type==2){
			damage -= damage*mr/100;
		}
		curHP-=damage;
		
		//champion dies
		if (curHP<=0){
			alive = false;
		}
		
		//gains 1/15 of the damage taken as mana
		this.setMana(curMana+damage/15);
		
		//damage indicator
		isDamaged++;
		damageTaken.add(damage);
		damageType.add(type);
		timers.add(new Timer(500, this));
		timers.get(nTimers).start();
		nTimers++;
		return damage;
	}
	
	public void heal(int amount){
		curHP += amount;
		if (curHP>hp) curHP = hp;
	}
	
	//when the attack of this hits the target
	public void hitsAuto(Champion target){
		int type = 1;
		//increase mana upon hitting an autoattack
		curMana+=10;
		//if glacial is active for this champion
		if (glacial){
			//20% chance to stun for 0.5 seconds
			if ((int)(Math.random()*5)==0){
				target.getStunned(0.5);
			}
		}
		//if void is active for this champion, deal true damage (true damage ignores armor and magic resist)
		if (void1){
			type = 3;
		}
		//if demon is active for this champion
		if (demon){
			//20% chance to steal 20 mana when auto attacking
			if ((int)(Math.random()*5)==0){
			target.setMana(target.getCurMana()-20);
			this.setMana(curMana+20);
			}
		}
		
		target.takeDmg(ad, type);
		//melee champions have slash animation when they autoattack
		if (!isRanged) target.getHit();
		
		//if blademaster is active for this champion
		if (blademaster) {
			//33% chance to autoattack an extra time
			if ((int)(Math.random()*3)==0){
				this.hitsAuto(target);
			}
		}
	}
	
	
	//is overridden
	public void useAbility(){
		if (curMana>=mana){
			curMana = 0;
		}
	}
	
	//is overridden
	public void usePassive(){
	
	}
	
	//moves towards the target champion
	public void move(Champion target){
		int dX = target.getX()+42-(x+42);
		int dY = target.getY()+42-(y+42);
		double angle = Math.atan2(dY, dX)*(180/Math.PI);
		int vX = (int)(vel*(90-Math.abs(angle))/90);
		int vY = 0;
		if (angle<0) vY = Math.abs(vX)-vel;
		else vY = vel-Math.abs(vX);
		
		x += vX;
		y += vY;
	}
	
	//set original position for when the round resets
	public void setOriginalPos(int x, int y){
		originalX = x; 
		originalY = y;
	}
	
	//return to original position after the round resets
	public void returnToOriginal(){
		x = originalX;
		y = originalY;
	}
	
	//in range to attack
	public boolean inAutoRange(Champion target){
		int dX = x-target.getX();
		int dY = y-target.getY();
		return (Math.sqrt(Math.pow(dX,2)+Math.pow(dY,2))<=range);
	}
	
	//is overridden by jinx
	public void checkIfAssisted(Champion champ){
		
	}

	/* traits and origins */
	public int getTrait(){
		return trait;
	}
	
	public int getOrigin(){
		return origin;
	}
	
	public void hasBlademaster(boolean hasBlademaster){
		blademaster = hasBlademaster;
	}
	
	public void hasGlacial(boolean hasGlacial){
		glacial = hasGlacial;
	}
	
	public void hasVoid(boolean hasVoid){
		void1 = hasVoid;
	}
	
	public void hasHextech(boolean hasHextech){
		hextech = hasHextech;
	}
	
	public void hasDemon(boolean hasDemon){
		demon = hasDemon;
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
	
	public void setMR(int mr){
		this.mr = mr;
	}
	
	public void setMana(int mana){
		if (mana<0) curMana = 0;
		else if (mana>=this.mana){
			curMana = this.mana;
		}
		else{
			curMana = mana;
		}
	}
	
	public void isEnemyChamp(){
		isEnemy = true;
	}
	
	public void isOnBoard(boolean onBoard){
		this.onBoard = onBoard;
	}
	
	/* get methods */
	public String getName(){
		return name;
	}

	public Rectangle getHitBox(){
		hitBox.setBounds(x, y, 85, 85);
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
	
	public int getMR(){
		return mr;
	}
	
	public int getCurHP(){
		return curHP;
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
	
	public boolean isStunned(){
		return (isStunned>0);
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
	
	public int getCost(){
		return cost;
	}

	public int getLevel(){
		return level;
	}
	
	public String printTrait(){
		switch(trait){
			case 0:return "Sorcerer";
			case 1:return "Marksman";
			case 2:return "Warden";
			case 3:return "Brawler";
			case 4:return "Assassin";
			case 5:return "Blademaster";
			default:return null;
		}
	}

	public String printOrigin(){
		switch(origin){
			case 0:return "Elemental";
			case 1:return "Glacial";
			case 2:return "Demon";
			case 3:return "Imperial";
			case 4:return "Void";
			case 5:return "Hextech";
			case 6:return "Monkey";
			default:return null;
		}
	}

	public void displayStats(boolean display){
		displayStats = display;
	}
	
	//if mouse is hovering
	public boolean contains(int mX, int mY) {
    	return (mX>=x && mX<x+85 && mY>=y && mY<y+85);
    }
	
	//custom draw at x and y
	public void myDraw(Graphics g) {
    	g.drawImage(image.getImage(), x, y, null);
		
		if (board.isInGame() && onBoard){
			g.setColor(new Color(10,10,10));
			g.fillRect(x+15,y-12, 60, 11);
			//hp bar
			g.setColor(Color.GREEN);
			g.fillRect(x+15, y-12, curHP*60/hp, 7);
			//mana bar
			g.setColor(Color.BLUE);
			g.fillRect(x+15, y-5, curMana*60/mana, 4);
		}
		
		//draw stars to display level
		for (int i=0; i<level; i++){
			g.drawImage(star.getImage(), x+75+2+i*15, y-15, null);
		}
		//draw slash from melee attack
		if (isHit>0){
			g.drawImage(slash.getImage(), x, y, null);
		}
		//draw stunned icon
		if (isStunned>0) g.drawImage(stun.getImage(), x, y-30, null);
		//draw damage indicators
		if (isDamaged>0) {
			for (int i=0; i<damageTaken.size(); i++){
				if (damageType.get(i)==1) g.setColor(Color.RED);
				else if (damageType.get(i)==2) g.setColor(Color.BLUE);
				else g.setColor(Color.WHITE);
				g.drawString(damageTaken.get(i)+"", x, y-20*i-10);
			}
		}
		
		//if mouse hovers champion for more than a second
		if (displayStats && board.pickedChamp==null){
			int displayX, displayY = y;
			if (x+85+160<=board.w){
				displayX = x+85+10;
			}
			else{
				displayX = x-160;
			}
			
			g.setColor(new Color(189,183,107));
			g.fillRect(displayX, displayY, 150, 80);
			g.setColor(new Color(250,250,210));
			g.drawRect(displayX, displayY, 150, 80);
			g.drawRect(displayX+1, displayY+1, 148, 78);
			g.setColor(Color.BLACK);
			g.setFont(new Font("SansSerif",Font.PLAIN,12));
			g.drawString(name,displayX+5,displayY+15);
			g.drawString("Level: "+level,displayX+70,displayY+15);
			g.drawString(printOrigin(),displayX+5,displayY+27);
			g.drawString(printTrait(),displayX+70,displayY+27);
			g.drawString("HP: "+hp,displayX+5,displayY+39);
			if(mana>1000){
				g.drawString("Mana: None",displayX+70,displayY+39);
			}
			else{
				g.drawString("Mana: "+mana,displayX+70,displayY+39);
			}
			g.drawString("AD: "+ad,displayX+5,displayY+51);
			g.drawString("AP: "+ap,displayX+70,displayY+51);
			g.drawString("Armor: "+armor,displayX+5,displayY+63);
			g.drawString("MR: "+mr,displayX+70,displayY+63);
			g.drawString("AS: "+as,displayX+5,displayY+75);
			if(isRanged){
				g.drawString("Ranged",displayX+70,displayY+75);
			}
			else{
				g.drawString("Melee",displayX+70,displayY+75);
			}
		}
    }
	
	//is overridden
	public void drawAbility(Graphics2D g){

	}
	
}

class Annie extends Champion{
	
	public Annie(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("annie.png");
		name = "Annie";
		origin = 0; trait = 0;
		isRanged = true;
		originalHP=1450; originalAD=100; originalAP=100; originalAS=1; originalArmor=10; originalMR=10; range = 250; mana = 150;cost = 5;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Wukong extends Champion{
	public Wukong(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("wukong.png");
		name = "Wukong";
		origin = 6; trait = 3;
		isRanged = false;
		originalHP=2000; originalAD=150; originalAP=100; originalAS=1; originalArmor=30; originalMR=30; range = 100; mana = 150;cost = 5;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Chogath extends Champion{
	public Chogath(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("cho'gath.png");
		name = "Cho'gath";
		origin = 4; trait = 3;
		isRanged = false;
		originalHP=2500; originalAD=100; originalAP=100; originalAS=1; originalArmor=30; originalMR = 30; range = 100; mana = 150;cost = 5;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Velkoz extends Champion{
	public Velkoz(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("vel'koz.png");
		name = "Vel'koz";
		origin = 4; trait = 0;
		isRanged = true;
		originalHP=1500; originalAD=100; originalAP=100; originalAS=1; originalArmor=10; originalMR=10; range = 300; mana = 150;cost = 5;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Brand extends Champion{
	public Brand(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("brand.png");
		name = "Brand";
		origin = 0; trait = 0;
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR = 10; range = 300; mana = 120;cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}
class Lux extends Champion{
	public Lux(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("lux.png");
		name = "Lux";
		origin = 0; trait = 0;
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR = 10; range = 300;  mana = 120;cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Nautilus extends Champion{
	
	private Timer ability = new Timer(16, this);
	private int count, targetX, targetY, aVel = 5;
	private double distance;
	private Champion target = null;
	private boolean reachedTarget = false;
	
	public Nautilus(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("nautilus.png");
		name = "Nautilus";
		origin = 0; trait = 2;
		isRanged = false;
		originalHP=2000; originalAD=50; originalAP=75; originalAS=1; originalArmor=30; originalMR=30; range = 100;  mana = 120;cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			curMana = 0;
			count = 0;
			reachedTarget = false;
			target = board.findFurthest(this, isEnemy);
			ability.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
		if (e.getSource()==ability){
			
			distance = Math.sqrt(Math.pow(x-targetX, 2) + Math.pow(y-targetY,2));
			targetX = target.getX(); targetY = target.getY();
			
			//reaches target
			if (!reachedTarget && count>=distance/aVel){
				reachedTarget = true;
				target.getStunned(3);
				target.takeDmg(ap*2, 2);
				count = 0;
			}
			
			//has reached target
			if (reachedTarget){
				//makes target go up
				if (count<50){
					targetY-=2;
					target.setPos(targetX, targetY);
				}
				//makes target come down
				else if(count>60){
					targetY+=2;
					target.setPos(targetX, targetY);
				}
				//stops ability when target lands
				if(count>110){
					ability.stop();
				}
			}
			count++;
		}
	}
}

class Syndra extends Champion{
	
	private BufferedImage textureImage;
	private TexturePaint ballTexture;
	private int aX1, aY1, aX2, aY2, aX3, aY3, aVel = 7, nBalls;
	private double vX1, vY1, vX2, vY2, vX3, vY3;
	private Champion target;
	private Timer ability = new Timer(16, this);
	
	public Syndra(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("syndra.png");
		name = "Syndra";
		origin = 2; trait = 0;
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR=10; range = 300;  mana = 100; cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
		
		try{
			textureImage = ImageIO.read(new File("syndraRTexture.jpg"));
		} 
		catch (IOException ex) {
			System.out.println("ERROR");
        }
	}
	
	public void useAbility(){
		if (curMana>=mana){
			curMana = 0;
			nBalls = 3;
			
			//find furthest target
			target = board.findFurthest(this, isEnemy);
			
			//balls start at the 3 balls on the image
			aX1 = x; aY1 = y+55;
			aX2 = x+55; aY2 = y;
			aX3 = x+60; aY3 = y+60;
			
			ability.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
		if (e.getSource()==ability){
			//constantly calculate angle for homing missile effect
			int dX = target.getX()-aX1, dY = target.getY()-aY1;
			double angle = Math.atan2(dY, dX)*(180/Math.PI);
			vX1 = ((aVel*(90-Math.abs(angle))/90));
			if (angle<=0) vY1 = Math.abs(vX1)-aVel;
			else vY1 = aVel-Math.abs(vX1);
			
			dX = target.getX()-aX2; dY = target.getY()-aY2;
			angle = Math.atan2(dY, dX)*(180/Math.PI);
			vX2 = ((aVel*(90-Math.abs(angle))/90));
			if (angle<=0) vY2 = Math.abs(vX2)-aVel;
			else vY2 = aVel-Math.abs(vX2);
			
			dX = target.getX()-aX3; dY = target.getY()-aY3;
			angle = Math.atan2(dY, dX)*(180/Math.PI);
			vX3 = ((aVel*(90-Math.abs(angle))/90));
			if (angle<=0) vY3 = Math.abs(vX3)-aVel;
			else vY3 = aVel-Math.abs(vX3);
			
			aX1+=vX1; aY1+=vY1;
			aX2+=vX2; aY2+=vY2;
			aX3+=vX3; aY3+=vY3;
			
			//when it reaches the target
			if (target.getHitBox().contains(aX1+10, aY1+10)){
				target.takeDmg((int)(1.5*ap), 2);
				nBalls--;
			}
			if (target.getHitBox().contains(aX2+10, aY2+10)){
				target.takeDmg((int)(1.5*ap), 2);
				nBalls--;
			}
			if (target.getHitBox().contains(aX3+10, aY3+10)){
				target.takeDmg((int)(1.5*ap), 2);
				nBalls--;
			}
			//once all balls have reached the target, ability stops
			if (nBalls==0){
				ability.stop();
			}
		}
	}
	
	public void drawAbility(Graphics2D g2){
		if (nBalls>0){
			ballTexture = new TexturePaint(textureImage, new Rectangle(0, 0, 20, 20));
			g2.setPaint(ballTexture);
			g2.fillOval(aX1, aY1, 20, 20);
			g2.fillOval(aX2, aY2, 20, 20);
			g2.fillOval(aX3, aY3, 20, 20);
		}
	}
}

class Varus extends Champion{
	
	private BufferedImage textureImage;
	private TexturePaint arrowTexture;
	private Timer ability = new Timer(16, this);
	private boolean abilityActive = false;
	private int aX, aY, aVel = 7, count = 0;
	private double angle, vX, vY;
	private ArrayList<Champion> gotHit = new ArrayList<>();
	private Champion target;
	
	public Varus(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("varus.png");
		name = "Varus";
		origin = 2; trait = 1;
		isRanged = true;
		originalHP=1250; originalAD=100; originalAP=75; originalAS=1.5; originalArmor=10; originalMR=10; range = 300; mana = 100;cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
		try{
			textureImage = ImageIO.read(new File("VarusQTexture.jpg"));
		} 
		catch (IOException ex) {
			System.out.println("ERROR");
        }
	}
	
	public void useAbility(){
		if (curMana>=mana){
			gotHit.clear();
			curMana = 0;
			count = 0;
			aX = x+42;
			aY = y+42;
			//find furthest target
			target = board.findFurthest(this, isEnemy);
			//calculate angle 
			int dX = target.getX()+42-aX, dY = target.getY()+42-aY;
			angle = Math.atan2(dY, dX)*(180/Math.PI);
			vX = ((aVel*(90-Math.abs(angle))/90));
			if (angle<=0) vY = Math.abs(vX)-aVel;
			else vY = aVel-Math.abs(vX);
			
			abilityActive = true;
			ability.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
		if (e.getSource()==ability){
			aX += vX;
			aY += vY;
			count++;
			
			//collision detection
			if (isEnemy){
				for (int i=0; i<board.nBoardChamps;i++){
					Champion cur = board.boardChamps.get(i);
					boolean hit = false;
					if (cur.getHitBox().contains(aX, aY) || cur.getHitBox().contains(aX, aY+20)){
						for (int j=0; j<gotHit.size(); j++){
							if (gotHit.get(j)==cur){
								hit = true;
							}
						}
						if (!hit){
							cur.takeDmg(4*ap, 2);
							gotHit.add(cur);
						}
					}
				}
			}
			else {
				for (int i=0; i<board.enemyChamps.size(); i++){
					Champion cur = board.enemyChamps.get(i);
					boolean hit = false;
					if (cur.getHitBox().contains(aX, aY) || cur.getHitBox().contains(aX, aY+20)){
						for (int j=0; j<gotHit.size(); j++){
							if (gotHit.get(j)==cur){
								hit = true;
							}
						}
						if (!hit){
							cur.takeDmg(4*ap, 2);
							gotHit.add(cur);
						}
					}
				}
			}
			
			if (count>55){
				aVel--;
			}
			//disappears after about a second
			if (count>62){
				ability.stop();
				abilityActive = false;
				aVel = 7;
			}
		}
	}
	
	public void drawAbility(Graphics2D g2){
		if (abilityActive){
			arrowTexture = new TexturePaint(textureImage, new Rectangle(0, 0, 90, 60));
			g2.setPaint(arrowTexture);
			g2.rotate(Math.toRadians(angle), aX+40, aY+5);
			g2.fillRoundRect(aX, aY, 80, 10, 5, 5);
		}
	}
}

class Veigar extends Champion{
	
	private ImageIcon abilityImage = new ImageIcon("veigarUlt.png");
	private int aX, aY, aVel = 7;
	private double vX, vY;
	private Champion target;
	private Timer ability = new Timer(16, this);
	private boolean abilityActive = false;
	
	public Veigar(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("veigar.png");
		name = "Veigar";
		origin = 2; trait = 0;
		isRanged = true;
		originalHP=1250; originalAD=70; originalAP=75; originalAS=1; originalArmor=10; originalMR=10; range = 300; mana = 100;cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			curMana = 0;
			
			//find closest target
			if (isEnemy){
				target = board.enemyFindTarget(this);
			}
			else{
				target = board.findTarget(this);
			}
			
			aX = x+22; aY = y+22;
			
			abilityActive = true;
			ability.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
		if (e.getSource()==ability){
			//constantly calculate angle for homing missile effect
			int dX = target.getX()-aX, dY = target.getY()-aY;
			double angle = Math.atan2(dY, dX)*(180/Math.PI);
			vX = ((aVel*(90-Math.abs(angle))/90));
			if (angle<=0) vY = Math.abs(vX)-aVel;
			else vY = aVel-Math.abs(vX);
			
			aX += vX;
			aY += vY;
			
			//when it reaches the target
			if (target.getHitBox().contains(aX+20, aY+20)){
				//target also takes 30% of their missing hp as extra damage
				target.takeDmg((int)(5*ap+0.3*(target.getHP()-target.getCurHP())), 2);
				abilityActive = false;
				ability.stop();
			}
		}
	}
	
	public void drawAbility(Graphics2D g2){
		if (abilityActive){
			g2.drawImage(abilityImage.getImage(), aX, aY, null);
		}
	}
}

class Vi extends Champion{
	
	private Timer ability = new Timer(16, this);
	private int aVel = 5, velX, velY, count, targetX, targetY;
	private double angle;
	private Champion target = null;
	private boolean reachedTarget = false;
	
	public Vi(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("vi.png");
		name = "Vi";
		origin = 5; trait = 3;
		isRanged = false;
		originalHP=1500; originalAD=70; originalAP=75;originalAS=1; originalArmor=20; originalMR=20; range = 100; mana = 120;cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			curMana = 0;
			count = 0;
			target = board.findFurthest(this, isEnemy);
			int dX = targetX-x, dY = targetY-y;
			
			//calculate angle to target
			angle = Math.atan2(dY, dX)*(180/Math.PI);
			velX = (int)(aVel*(90-Math.abs(angle))/90); 
			if (angle<=0) velY = Math.abs(velX)-aVel;
			else velY = aVel-Math.abs(velX);
		
			ability.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
		if (e.getSource()==ability){
			
			targetX = target.getX(); targetY = target.getY();
			
			//reaches target
			if (!reachedTarget && this.getHitBox().intersects(target.getHitBox())){
				reachedTarget = true;
				target.getStunned(1);
			}
			
			//has reached target
			if (reachedTarget){
				//makes target go up
				if (count<30){
					targetY-=2;
					target.setPos(targetX, targetY);
				}
				//makes target come down
				else if(count<60){
					targetY+=2;
					target.setPos(targetX, targetY);
				}
				else{
					target.takeDmg(ap*4, 2);
					ability.stop();
				}
				count++;
			}
			//travelling to target
			else{
				x+=velX;
				y+=velY;
			}
		}
	}
	
	public void drawAbility(Graphics2D g2){
		g2.setColor(new Color(153,50,204));
		if (!reachedTarget && ability.isRunning()){
				g2.drawLine(x+42, y+42, targetX+42, targetY+42);
		}
	}
}

class Qiyana extends Champion{
	public Qiyana(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("qiyana.png");
		name = "Qiyana";
		origin = 0; trait = 4;
		isRanged = false;
		originalHP=1250; originalAD=150; originalAP=75; originalAS=1; originalArmor=10; originalMR=10; range = 100; mana = 70;cost = 3;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
}

class Riven extends Champion{
	
	private Timer ability = new Timer(7000, this);
	private boolean abilityActive = false;
	private ImageIcon abilityImage = new ImageIcon("fire.png");
	private int tempAD, tempRange;
	
	public Riven(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("riven.png");
		name = "Riven";
		origin = 3; trait = 5; 
		isRanged = false;
		originalHP=1500; originalAD=70; originalAP=75; originalAS=1.5; originalArmor=10; originalMR=10; range = 100; mana = 80; cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			curMana = 0;
			
			//increase attack damage and range while ability is active
			tempAD = ad;
			tempRange = range;
			ad *= 1.5;
			range*=1.3;
			
			abilityActive = true;
			ability.start();
		}
	}
	
	public void hitsAuto(Champion target){
		int type = 1;
		//cannot gain mana if ability is active
		if (!abilityActive){
			curMana+=10;
		}
		target.getHit();
		//gain 25% lifesteal when ability is active
		if (abilityActive){
			this.heal(target.takeDmg(ad, type)/4);
		}
		if (blademaster) {
			if ((int)(Math.random()*3)==0){
				this.hitsAuto(target);
			}
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
		//after 5 seconds, ability stops
		if (e.getSource()==ability){
			ability.stop();
			abilityActive = false;
			
			ad = tempAD;
			range = tempRange;
		}
	}
	
	public void drawAbility(Graphics2D g2){
		
		if (abilityActive){
			//draw transparent image
			float alpha = 0.75f;
			int type = AlphaComposite.SRC_OVER; 
			AlphaComposite composite = AlphaComposite.getInstance(type, alpha);
			Composite originalComposite = g2.getComposite();
			
			g2.setComposite(composite);
			g2.drawImage(abilityImage.getImage(), x, y, null);
			g2.setComposite(originalComposite);
		}
	}
}
class Braum extends Champion{
	
	private boolean hasShield = false;
	private double shieldAngle;
	private Timer ability = new Timer(2000, this);
	
	public Braum(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("braum.png");
		name = "Braum";
		origin = 1; trait = 2;
		isRanged = false;
		originalHP=2000; originalAD=50; originalAP=75; originalAS=1; originalArmor=30; originalMR=30; range = 100; mana = 80;cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public int takeDmg(int damage, int type){
		
		//only take 20% damage when shield is up
		if (hasShield){
			damage = (int)(damage*0.20);
			curHP-= damage;
		}
		else{
			//physical damage
			if (type==1){
				damage -= damage*armor/150;
			}
			//magical damage 
			else if (type==2){
				damage -= damage*mr/100;
			}
			curHP-=damage;
			
			//champion dies
			if (curHP<=0){
				alive = false;
			}
			
			//gains 1/15 of the damage taken as mana
			this.setMana(curMana+damage/15);
		}
		
		//damage indicator
		isDamaged++;
		damageTaken.add(damage);
		damageType.add(type);
		timers.add(new Timer(500, this));
		timers.get(nTimers).start();
		nTimers++;
		return damage;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			curMana = 0;
			Champion target;
			//find closest enemy
			if (isEnemy){
				target = board.enemyFindTarget(this);
			}
			else{
				target = board.findTarget(this);
			}
			//calculate angle to enemy
			int dX = target.getX()-x, dY = target.getY()-y;
			shieldAngle = Math.atan2(dY, dX)*(180/Math.PI);
			
			hasShield = true;
			ability.start();
		}
		//cannot gain mana while shield is active
		if (hasShield){
			curMana = 0;
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
		//after 2 seconds, shield disappears
		if (e.getSource()==ability){
			ability.stop();
			hasShield = false;
		}
	}
	
	public void drawAbility(Graphics2D g2){
		//draw a shield facing the direction of the enemy
		if (hasShield && alive){
			g2.rotate(Math.toRadians(shieldAngle+90), x+42.5, y+42.5);
			g2.setColor(new Color(135,206,235));
			g2.fillRoundRect(x, y-5, 85, 13, 5, 5);
			
		}
	}
}

class Darius extends Champion{
	
	private Timer ability = new Timer (500, this);
	private int count = 0, cX, cY;
	private final int radius = 115/2;
	
	public Darius(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("darius.png");
		name = "Darius";
		origin = 3; trait = 2;
		isRanged = false;
		originalHP=1500; originalAD=50; originalAP=50; originalAS=1; originalArmor=15; originalMR=15; range = 100; mana = 80;cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			cX = x+42; cY = y+42;
			curMana = 0;
			ability.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
			if (e.getSource()==ability){
				cX = x+42; cY = y+42;
				count++;
				//collision detection
				if (count==2){
					if (isEnemy){
						for (int i=0; i<board.nBoardChamps; i++){
							Champion cur = board.boardChamps.get(i);
							if (cur.isAlive()){
								int curX = cur.getX(), curY = cur.getY();
								if (curX<cX && cX-curX<=85+radius) curX += cX-curX;
								if (curY<cY && cY-curY<=85+radius) curY += cY-curY;
								if (Math.sqrt(Math.pow(curX-cX, 2) + Math.pow(curY-cY, 2)) <= radius){
									cur.takeDmg(ap*3, 2);
									this.heal((int)((hp-curHP)*0.3));
								}
							}
						}
					}
					else{
						for (int i=0; i<board.enemyChamps.size(); i++){
							Champion cur = board.enemyChamps.get(i);
							if (cur.isAlive()){
								int curX = cur.getX(), curY = cur.getY();
								if (curX<cX && cX-curX<=85+radius) curX += cX-curX;
								if (curY<cY && cY-curY<=85+radius) curY += cY-curY;

								if (Math.sqrt(Math.pow(curX-cX, 2) + Math.pow(curY-cY, 2)) <= radius){
									cur.takeDmg(ap*3, 2);
									this.heal((int)((hp-curHP)*0.3));
								}
							}
						}
					}
				}
			}
		}
	}
	
	public void drawAbility(Graphics2D g2){
		//outline (charging up)
		if (count>0){
			g2.setColor(new Color(10, 10, 10));
			g2.drawOval(x-30, y-30, 145, 145);
			g2.drawOval(x, y, 85, 85);
		}
		//swings axe
		if (count==2){
			g2.setStroke(new BasicStroke(30));
			g2.setColor(new Color(220,20,60, 200));
			g2.drawOval(x-15, y-15, 115, 115);
		}
		//stops
		if (count>=3){
			count = 0;
			ability.stop();
		}
	}
}

class Kassadin extends Champion{
	
	public Kassadin(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("kassadin.png");
		name = "Kassadin";
		origin = 4; trait = 5;
		isRanged = false; 
		originalHP=1250; originalAD=50; originalAP=50; originalAS=1; originalArmor=10; originalMR=20; range = 100; mana = 100000;cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void hitsAuto(Champion target){
		int type = 1;
		if (void1) type = 3;
		int targetArmor = target.getArmor();
		//steals mana from target
		target.setMana(target.getCurMana()-15);
		int damage = 0;
		damage = ad-ad*targetArmor/150;
		target.takeDmg(damage, type);
	}
}
class Sivir extends Champion{
	
	private int velX, velY, aVel = 25, nShurikens = 0, aX, aY, count = 0;
	private double shurikenAngle;
	private Timer ability = new Timer(60, this);
	private ImageIcon shuriken = new ImageIcon("shuriken.png");
	private Rectangle shurikenHitBox;
	private ArrayList<Champion> gotHit = new ArrayList<>();
	
	public Sivir(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("sivir.png");
		name = "Sivir";
		origin = 0; trait = 1;
		isRanged = true;
		originalHP=1000; originalAD=70;originalAP=50; originalAS=1.5; originalArmor=10; originalMR=10; range = 300; mana = 100; cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			gotHit.clear();
			count = 0;
			aX = this.x+42; aY = this.y+42;
			//shuriken hitbox
			shurikenHitBox = new Rectangle(aX, aY, 42, 42);
			curMana = 0;
			Champion target = board.findFurthest(this, isEnemy);
			int dX = target.getX()+42-aX, dY = target.getY()+42-aY;

			//calculate angle to target
			shurikenAngle = Math.atan2(dY, dX)*(180/Math.PI);
			velX = (int)(aVel*(90-Math.abs(shurikenAngle))/90); 
			if (shurikenAngle<=0) velY = Math.abs(velX)-aVel;
			else velY = aVel-Math.abs(velX);

			ability.start();
			nShurikens++;
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		else if (e.getSource()==ability){
			//shuriken can hit enemies again on the way back
			if (count==25){
				gotHit.clear();
			}
			//travelling away
			if (count<25){
				aX += velX;
				aY += velY;
				shurikenHitBox.setLocation(aX, aY);
				Champion cur;
				if (isEnemy){
					for (int j=0; j<board.nBoardChamps; j++){
						cur = board.boardChamps.get(j);
						if (cur.getHitBox().intersects(shurikenHitBox) && cur.isAlive()){
							boolean hit = false;
							for (int i=0; i<gotHit.size(); i++){
								if (gotHit.get(i)==cur){
									hit = true;
								}
							}
							if (!hit){
								gotHit.add(cur);
								cur.takeDmg(2*ap, 2);
							}
						}
					}
				}else{
					for (int j=0; j<board.enemyChamps.size(); j++){
						cur = board.enemyChamps.get(j);
						if (cur.getHitBox().intersects(shurikenHitBox) && cur.isAlive()){
							boolean hit = false;
							for (int i=0; i<gotHit.size(); i++){
								if (gotHit.get(i)==cur){
									hit = true;
								}
							}
							if (!hit){
								gotHit.add(cur);
								cur.takeDmg(2*ap, 2);
							}
						}
					}
				}
				count++;
			}
			//coming back
			else{
				aX -= velX;
				aY -= velY;
				shurikenHitBox.setLocation(aX, aY);
				Champion cur;
				if (isEnemy){
					for (int j=0; j<board.nBoardChamps; j++){
						cur = board.boardChamps.get(j);
						if (cur.getHitBox().intersects(shurikenHitBox) && cur.isAlive()){
							boolean hit = false;
							for (int i=0; i<gotHit.size(); i++){
								if (gotHit.get(i)==cur){
									hit = true;
								}
							}
							if (!hit){
								gotHit.add(cur);
								cur.takeDmg(2*ap, 2);
							}
						}
					}
				}else{
					for (int j=0; j<board.enemyChamps.size(); j++){
						cur = board.enemyChamps.get(j);
						if (cur.getHitBox().intersects(shurikenHitBox) && cur.isAlive()){
							boolean hit = false;
							for (int i=0; i<gotHit.size(); i++){
								if (gotHit.get(i)==cur){
									hit = true;
								}
							}
							if (!hit){
								gotHit.add(cur);
								cur.takeDmg(2*ap, 2);
							}
						}
					}
				}
					
				//shuriken comes back to sivir
				if (this.getHitBox().intersects(shurikenHitBox)){
					ability.stop();
					nShurikens--;
				}
				count++;
			}
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
	}
	
	public void drawAbility(Graphics2D g2){
		//spin shuriken as it travels
		if (nShurikens>0){
			g2.rotate(Math.toRadians(45*count), aX+25, aY+25);
			g2.drawImage(shuriken.getImage(), aX, aY, null);
		}
	}
}
class Jinx extends Champion{
	
	private Timer passive = new Timer(2000, this);
	private ArrayList<Champion> assisted = new ArrayList<>();
	private boolean passiveIsActive = false;
	
	public Jinx(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("jinx.png");
		name = "Jinx";
		origin = 5; trait = 1;
		isRanged = true; 
		originalHP=1000; originalAD=70; originalAP=50; originalAS=1.5; originalArmor=10; originalMR=10; range = 300; mana = 100000;cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void reset(){
		hp=originalHP; curHP = hp; ad=originalAD; ap=originalAP; armor =originalArmor; mr = originalMR; as = originalAS;
		curHP = hp;
		curMana = 0;
		this.returnToOriginal();
		isStunned = 0;
		isHit = 0;
		assisted.clear();
		alive = true;
	}
	
	public void hitsAuto(Champion target){
		target.takeDmg(ad, 1);
		//add attacked champion to list of assisted
		assisted.add(target);
	}
	
	//attack speed doubles when killing a champion or assisting on a kill
	public void usePassive(){
		passiveIsActive = true;
		as *= 2;
		passive.start();
	}
	
	//check if Jinx assisted on the kill
	public void checkIfAssisted(Champion champ){
		for (int i=0; i<assisted.size(); i++){
			if (assisted.get(i)==champ){
				if (!passiveIsActive){
					this.usePassive();
				}
				else{
					assisted.remove(i);
				}
			}
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		else if (e.getSource()==passive){
			passive.stop();
			as = originalAS;
			passiveIsActive = false;
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
	}
}

class Yasuo extends Champion{
	
	private Timer ability = new Timer(100, this);
	private int count;
	private Champion target;
	
	public Yasuo(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("yasuo.png");
		name = "Yasuo";
		origin = 0; trait = 5; 
		isRanged = false;
		originalHP=1250; originalAD=50; originalAP=50; originalAS=1; originalArmor=10; originalMR=10; range = 100; mana = 70;cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp;
	}
	
	public void useAbility(){
		if (curMana>=mana){
			count = 0;
			curMana = 0;
			target = board.findFurthest(this, isEnemy);
			//makes target airborne
			int dX = target.getX(), dY = target.getY()-80;
			target.setPos(dX, dY);
			if (dY>100){ y = dY - 100; x = dX;}
			else if (dX>100){ x = dX - 100; y = dY;}
			else { x = dX + 100; y = dY;};
			target.getStunned(2);
			ability.start();
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		else if (e.getSource()==ability){
			//auto attacks target every 0.3 seconds for 4 times in total
			if (count<=12){
				if (count%3==0) this.hitsAuto(target);
			}
			//falling back down
			else{
				target.setPos(target.getX(), target.getY()+10);
				if (count>20) ability.stop();
			}
			count++;
			curMana = 0;
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
		}
	}
}

class Ashe extends Champion{
	
	private int velX, velY, aVel = 20, nArrows = 0, aX, aY;
	private double arrowAngle;
	private Timer ability = new Timer(60, this);
	private ImageIcon arrow = new ImageIcon("asheArrow.png");
	
	public Ashe(int x, int y, int level, Board board){
		super(x, y, level, board);
		image = new ImageIcon("ashe.png");
		name = "Ashe";
		origin = 1; trait = 1;
		isRanged = true;
		originalHP=1000; originalAD=70; originalAP=50; originalAS=1.5; originalArmor=10; originalMR=10; range = 300; mana = 120;cost = 1;
		for (int i=1; i<level; i++){
			originalHP*=1.3;
			originalAD*=1.3;
			originalAP*=1.3;
			originalMR*=1.3;
			originalAS=Math.round(originalAS*130)/100.0;
			originalArmor*=1.3;
			cost+=2;
		}
		hp = originalHP; ad = originalAD; ap = originalAP; mr = originalMR; as = originalAS; armor = originalArmor;
		curHP = hp; 
	}
	
	public void useAbility(){
		if (curMana>=mana){
			aX = this.x+42; aY = this.y+42;
			curMana = 0;
			Champion target = board.findFurthest(this, isEnemy);
			int dX = target.getX()+42-aX, dY = target.getY()+42-aY;
			
			//calculate angle to target
			arrowAngle = Math.atan2(dY, dX)*(180/Math.PI);
			velX = (int)(aVel*(90-Math.abs(arrowAngle))/90); 
			if (arrowAngle<=0) velY = Math.abs(velX)-aVel;
			else velY = aVel-Math.abs(velX);
			
			ability.start();
			nArrows++;
		}
	}
	
	public void actionPerformed(ActionEvent e){
		//slash is gone
		if (e.getSource()==hit){
			isHit--;
			hit.stop();
		}
		//not stunned anymore
		else if (e.getSource()==stunned){
			isStunned--;
			stunned.stop();
		}
		//damage indicator disappears
		else {
			for (int i=0; i<nTimers; i++){
				if (e.getSource()==timers.get(i)){
					damageTaken.remove(i);
					damageType.remove(i);
					nTimers--;
					timers.get(i).stop();
					timers.remove(i);
				}
			}
			if (e.getSource()==ability){
				aX += velX;
				aY += velY;
				//if it goes out of the screen
				if (aX-50>Board.w || aX+50<0 || aY-55>Board.h || aY+55<0){
					nArrows--;
					ability.stop();
				}
				//collision detection
				Champion cur;
				if (isEnemy){
					for (int j=0; j<board.nBoardChamps; j++){
						cur = board.boardChamps.get(j);
						if (cur.contains(aX, aY) && cur.isAlive()){
							cur.getStunned(1.5);
							cur.takeDmg(3*ap, 2);
							nArrows--;
							ability.stop();
						}
					}
				}else{
					for (int j=0; j<board.enemyChamps.size(); j++){
						cur = board.enemyChamps.get(j);
						if (cur.contains(aX, aY) && cur.isAlive()){
							cur.getStunned(1.5);
							cur.takeDmg(3*ap, 2);
							nArrows--;
							ability.stop();
						}
					}
				}
			}
		}
	}
	
	public void drawAbility(Graphics2D g2){
		if (nArrows>0){
			g2.rotate(Math.toRadians(arrowAngle+135), aX+25, aY+27.5);
			g2.drawImage(arrow.getImage(), aX, aY, null);
		}
	}
}
