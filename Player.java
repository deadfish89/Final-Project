public class Player{
	
	private int gold, xp, level, health, levelUp;

	public Player(){
		gold = 0;
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