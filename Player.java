import java.util.ArrayList;

public class Player{
	
	private int gold, xp, level, health, levelUp;
	private ArrayList<Integer> champs = new ArrayList<>();

	public Player(){
		gold = 1000;
		xp = 0;
		level = 1;
		health = 100;
		levelUp = 4;
	}

	public void gainGold(int gold){
		this.gold+=gold;
	}
	
	public void spendGold(int gold){
		this.gold-=gold;
	}
	
	public void gainXP(int xp){
		this.xp+=xp;
		if (xp>=levelUp){
			xp-=levelUp;
			level++;
		}
	}
	
	public void takeDamage(int damage){
		health-=damage;
	}
	
	public int getGold(){
		return gold;
	}
	
	public int getXP(){
		return xp;
	}
	
	public int getLevel(){
		return level;
	}
	
	public int getHealth(){
		return health;
	}
}
