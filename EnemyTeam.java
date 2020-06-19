import java.util.*;
import java.io.*;
 
public class EnemyTeam{
	static Scanner scan;
	static String[][]teams = new String[1000][1000];
	static int[]size = new int[1000];
	static void loadTeams()  throws FileNotFoundException{
		int r = 1;
		scan = new Scanner(new File("Teams.txt"));
		while(scan.hasNextLine()){
			String []temp = scan.nextLine().split(" ");
			size[r] = temp.length;
			for(int i = 0;i < temp.length;i++){
				teams[r][i] = temp[i];
				
			}
			r++;
		}
	}
	static ArrayList<Champion> getTeam(int round,Board board){
		ArrayList<Champion> temp = new ArrayList<Champion>();
		int t = (int)(Math.random()*size[round]);
		String []champs = teams[round][t].split("/");
		for(int i = 0;i < champs.length;i++){
			String []stats = champs[i].split(",");
			temp.add(SummonChamp.summon(Integer.parseInt(stats[0]),Integer.parseInt(stats[1]),Integer.parseInt(stats[2]),Integer.parseInt(stats[3]),board));
		}
		return temp;
	}
}