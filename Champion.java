import javax.swing.*;
import java.awt.*;

public class Champion{
	int x,y;
	ImageIcon image;
	int hp=0, lvl=0, ad=0, ap=0;
	
	public Champion(int champion, int x, int y, int level){
		this.x = x;
		this.y = y;
		
		switch (champion){
			case 0:
				image = new ImageIcon("annie.png");
				hp = 1500;
				ad = 100;
				ap = 150;
				break;
			case 1: 
				image = new ImageIcon("wukong.png");
				break;
			case 2:
				image = new ImageIcon("cho'gath.png");
				break;
			case 3:
				image = new ImageIcon("vel'koz.png");
				break;
			case 4: 
				image = new ImageIcon("brand.png");
				break;
			case 5: 
				image = new ImageIcon("lux.png");
				break;
			case 6: 
				image = new ImageIcon("nautilus.png");
				break;
			case 7:
				image = new ImageIcon("syndra.png");
				break;
			case 8:
				image = new ImageIcon("varus.png");
				break;
			case 9:
				image = new ImageIcon("veigar.png");
				break;
			case 10: 
				image = new ImageIcon("vi.png");
				break;
			case 11:
				image = new ImageIcon("qiyana.png");
				break;
			case 12: 
				image = new ImageIcon("riven.png");
				break;
			case 13:
				image = new ImageIcon("braum.png");
				break;
			case 14:
				image = new ImageIcon("darius.png");
				break;
			case 15:
				image = new ImageIcon("kassadin.png");
				break;
			case 16:
				image = new ImageIcon("sivir.png");
				break;
			case 17:
				image = new ImageIcon("jinx.png");
				break;
			case 18:
				image = new ImageIcon("yasuo.png");
				break;
			case 19:
				image = new ImageIcon("tibbers.png");
				break;	
		}
		//for every level they gain 30% of each stat
		for (int i=0; i<lvl; i++){
			hp+=hp*0.3;
			ad+=ad*0.3;
			ap+=ap*0.3;
		}
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