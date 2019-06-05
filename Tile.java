/* CS 201 - Final Project
 * Minesweeper
 * 
 * Tile.java
 * 
 * Quinlan McGaugh
 * Paul Ruffolo
 */



public class Tile {

	//INSTANCE VARIABLES
	private int isBomb; // 0 = not bomb, 1 = bomb
	private int minesVal;
	private boolean isOpen;
	private boolean isFlagged;

	//CONSTRUCTOR vvv
	
	public Tile()
	{
		//Default constructor, creates a closed tile w/o a mine
		isBomb = 0;
		isFlagged = false;
		isOpen = false;
		minesVal = 0; //Temporary value, will be calculated upon opening a mine so we do not have to calculate for mines never opened
	}
	
	//GET METHODS vvv

	public int getBomb()
	{
		//Returns a 1 if the tile has a bomb, 0 if it does not
		return this.isBomb;
	}

	public int minesVal()
	{
		//Returns the number of mines surounding the tile
		return this.minesVal;
	}

	public boolean getOpen(){
		//Returns true if the tile is open, false if it is closed
		return this.isOpen;
	}
	
	public boolean getFlag() {
		//Returns true if the tile is flagged, false if it is not
		return this.isFlagged;
	}
	
	//SET METHODS vvv

	//changes status from closed to open
	public void changeOpen(){
		this.isOpen = true;
		if (this.isBomb == 0)
		{
			TileCanvas.remOpen--;
			//System.out.print(TileCanvas.remOpen + "|");
		}
	}
	
	public void changeBomb() {
		//Places a bomb in an empty tile
		this.isBomb = 1;
	}
	
	//changes status to "flagged" if it is currently unflagged, and to
	// "unflagged" if it is already flagged
	public void flag(){
		if(!isOpen)
		{
			if (!isFlagged){
				this.isFlagged = true;
				MinesweeperApplet.bombTotal--;
			}
			else{
				this.isFlagged = false;
				MinesweeperApplet.bombTotal++;
			}
		}
	}
	
	public void setTileVal(int s) {
		//Changed the tiles minesVal to the s parameter
		this.minesVal = s;
	}
}
