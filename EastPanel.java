import java.awt.*; 
import javax.swing.*;
import java.awt.event.*; 
import java.util.Arrays;

public class EastPanel extends JPanel implements ActionListener{
	
	private Timer timer;
	
	private Board board;
	private ImageIcon bg = new ImageIcon("background.png");
	private int[] traits = new int[6], origins = new int[7];
	private int[] goldTraits = new int[6], silverTraits = new int[6], bronzeTraits = new int[6];
	private int[] goldOrigins = new int[7], silverOrigins = new int[7], bronzeOrigins = new int[7];
	private int[] activeTraits = new int[6], activeOrigins = new int[7];
	private final String[] names = new String[] {"Sorcerer", "Marksman", "Warden", "Brawler", "Assassin", "Blademaster", "Elemental", "Glacial", "Demon", "Imperial", "Void", "Hextech", "Monkey"};
	
	public EastPanel(Board board){
		this.board = board;
		
		this.setPreferredSize(new Dimension(150, 570));
		this.setLayout(new FlowLayout());
		
		Arrays.fill(goldTraits,-1); Arrays.fill(silverTraits, -1); Arrays.fill(bronzeTraits,-1); Arrays.fill(goldOrigins,-1); Arrays.fill(silverOrigins, -1); Arrays.fill(bronzeOrigins, -1);
		
		timer = new Timer(1000, this);
		timer.start();
	}
	
	public void refresh(){
		activeTraits = board.getActiveTraits(); 
		activeOrigins = board.getActiveOrigins();
		traits = board.getTraits();
		origins = board.getOrigins();
		Arrays.fill(goldTraits,-1); Arrays.fill(silverTraits, -1); Arrays.fill(bronzeTraits,-1); Arrays.fill(goldOrigins,-1); Arrays.fill(silverOrigins, -1); Arrays.fill(bronzeOrigins, -1);
		for (int i=0; i<6; i++){
			if (activeTraits[i]==3) goldTraits[i] = i;
			else if (activeTraits[i]==2) silverTraits[i] = i;
			else if (activeTraits[i]==1) bronzeTraits[i] = i;
		}
		for (int i=0; i<7; i++){
			if (activeOrigins[i]==3) goldOrigins[i] = i;
			else if (activeOrigins[i]==2) silverOrigins[i] = i;
			else if (activeOrigins[i]==1) bronzeOrigins[i] = i;
		}
		repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		int y = 33;
		g.drawImage(bg.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
		
		g.setFont(new Font("SansSerif", Font.BOLD, 17));
		//golden traits/origins
		g.setColor(new Color(255,215,0));
		boolean[] visited = new boolean[13];
		for (int i=0; i<13; i++){
			int cur = 0;
			int max = -1, index = -1;
			for (int j=0; j<6; j++){
				cur = goldTraits[j];
				if (cur>-1){
					if (traits[cur] > max && !visited[cur]){
						max = traits[cur];
						index = cur;
						visited[index] = true;
					}
				}
			}
			for (int j=0; j<7; j++){
				cur = goldOrigins[j];
				if (cur>-1){
					if (origins[cur]>max && !visited[cur+6]){
						max = origins[cur];
						index = cur+6;
						visited[index] = true;
					}
				}
			}
			if (index>-1){
				//System.out.println(Arrays.toString(traits));
				//System.out.println(Arrays.toString(origins));
				String text = names[index] + ": " + max;
				g.drawString(text, 20, y);
				y += 35;
			}
		}
		
		//silver traits/origins
		Arrays.fill(visited, false);
		g.setColor(new Color(192,192,192));
		for (int i=0; i<13; i++){
			int max = -1, index = -1;
			for (int j=0; j<6; j++){
				int cur = silverTraits[j];
				if (cur>-1){
					if (traits[cur] > max && !visited[cur]){
						max = traits[cur];
						index = cur;
						visited[index] = true;
					}
				}
			}
			for (int j=0; j<7; j++){
				int cur = silverOrigins[j];
				if (cur>-1){
					if (origins[cur]>max && !visited[cur+6]){
						max = origins[cur];
						index = cur+6;
						visited[index] = true;
					}
				}
			}
			if (index>-1){
				String text = names[index] + ": " + max;
				g.drawString(text, 20, y);
				y += 35;
			}
		}
		
		//bronze traits/origins
		Arrays.fill(visited, false);
		g.setColor(new Color(205,127,50));
		for (int i=0; i<13; i++){
			int max = -1, index = -1;
			for (int j=0; j<6; j++){
				int cur = bronzeTraits[j];
				if (cur>-1){
					if (traits[cur] > max && !visited[cur]){
						max = traits[cur];
						index = cur;
						visited[index] = true;
					}
				}
			}
			for (int j=0; j<7; j++){
				int cur = bronzeOrigins[j];
				if (cur>-1){
					if (origins[cur]>max && !visited[cur+6]){
						max = origins[cur];
						index = cur+6;
						visited[index] = true;
					}
				}
			}
			if (index>-1){
				String text = names[index] + ": " + max;
				g.drawString(text, 20, y);
				y += 35;
			}
		}
		
		//unactivated traits/origins
		g.setColor(Color.WHITE);
		for (int i=0; i<6; i++){
			if (activeTraits[i]==0 && traits[i]>0){
				String text = names[i] + ": " + traits[i];
				g.drawString(text, 20, y);
				y += 35;
			}
		}
		for (int i=0; i<7; i++){
			if (activeOrigins[i]==0 && origins[i]>0){
				String text = names[i+6] + ": " + origins[i];
				g.drawString(text, 20, y);
				y += 35;
			}
		}
	}
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==timer && board.inPrepPhase()) {
			this.refresh();
		}
	}
	
}
