import java.util.Arrays;

public class ChampionPool{
	private static int[] champs = new int[19];
	private static int[] commons, rares, epics;
	private static Player player;
	
	public ChampionPool(Player player){
		this.player = player;
		commons = new int[] {12,13,14,15,16,17,18};
		rares = new int[] {4,5,6,7,8,9,10,11};
		epics = new int[] {0,1,2,3};
		champs = new int[] {30,30,30,30,30,30,30,30,30,20,20,20,20,20,20,20,10,10,10,10};
	}
	
	public int getNext(){
		int ret, common = 0, rare = 0, epic = 0, level = player.getLevel();
		switch (level){
			case 1: 
				common = 100; rare = 0; epic = 0;
				break;
			case 2:
				common = 100; rare = 0; epic = 0;
				break;
			case 3:
				common = 75; rare = 25; epic = 0;
				break;
			case 4: 
				common = 60; rare = 40; epic = 0;
				break;
			case 5: 
				common = 50; rare = 45; epic = 5;
				break;
			case 6: 
				common = 40; rare = 50; epic = 10;
				break;
			case 7:
				common = 25; rare = 60; epic = 15;
				break;
			case 8:
				common = 15; rare = 65; epic = 20;
				break;
			case 9:
				common = 10; rare = 60; epic = 30;
				break;
			case 10:
				common = 5; rare = 50; epic = 45;
		}
		
		int rarity = (int)(Math.random()*100)+1;
		while (true){
			if (rarity<=rare+epic){
				if (rarity<=epic){
					ret = epics[(int)(Math.random()*4)];
				}else{
					ret = rares[(int)(Math.random()*8)];
				}
			}else{
				ret = commons[(int)(Math.random()*7)];
			}
			
			if (champs[ret]>0){
				champs[ret]--;
				break;
			}else {
				System.out.println("NO MORE " + ret);
			}
		}
		System.out.println("champion: " + ret);
		return ret;
	}
	
}
