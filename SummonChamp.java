public class SummonChamp{
	public static Champion summon(int champ,int x,int y,int level,Board board){
		Champion temp = null;
		switch (champ){
			case 0:
				temp = new Annie(x, y, level, board);
				break;
			case 1:
				temp = new Wukong(x, y, level, board);
				break;
			case 2:
				temp = new Chogath(x, y, level, board);
				break;
			case 3:
				temp = new Velkoz(x, y, level, board);
				break;
			case 4:
				temp = new Brand(x, y, level, board);
				break;
			case 5:
				temp = new Lux(x, y, level, board);
				break;
			case 6:	
				temp = new Nautilus (x, y, level, board);
				break;
			case 7:
				temp = new Syndra(x, y, level, board);
				break;
			case 8:
				temp = new Varus(x, y, level, board);
				break;
			case 9:
				temp = new Veigar(x, y, level, board);
				break;
			case 10:
				temp = new Vi(x, y, level, board);
				break;
			case 11:
				temp = new Qiyana(x, y, level, board);
				break;
			case 12:
				temp = new Riven(x, y, level, board);
				break;
			case 13:
				temp = new Braum(x, y, level, board);
				break;
			case 14:
				temp = new Darius(x, y, level, board);
				break;
			case 15:
				temp = new Kassadin(x, y, level, board);
				break;
			case 16:	
				temp = new Sivir(x, y, level, board);
				break;
			case 17:
				temp = new Jinx(x, y, level, board);
				break;
			case 18:
				temp = new Yasuo(x, y, level, board);
				break;
			case 19:
				temp = new Ashe(x, y, level, board);
				break;
		}
		return temp;
	}
}