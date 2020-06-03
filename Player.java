import java.util.ArrayList;

public class Player{
	
	private int gold, xp, level, health, levelUp;
	private ArrayList<Integer> champs = new ArrayList<>();
	boolean inferno, glacial, tank, blademaster, mage, void1, brawler; //need to add a get traits method or smth

	public Player(){
		gold = 0;
		xp = 0;
		level = 1;
		health = 100;
		levelUp = 4;
	}
	
	public void addChamp(int champ){
		champs.add(champ);
	}
	
	public void removeChamp(int champ){
		for (int i=0; i<champs.size(); i++){
			if (champs.get(i)==champ){
				champs.remove(i);
				break;
			}
		}
	}
	
	public int getChamp(int index){
		if (index<=champs.size()-1) return champs.get(i);
		return -1;
	}
	
	public int getNChamps(){
		return champs.size();
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
