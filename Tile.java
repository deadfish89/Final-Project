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
	
	//if mouse is hovering
	public boolean contains(int mX, int mY) {
    	return (mX>x && mX<x+100 && mY>y && mY<y+100);
    }
}
