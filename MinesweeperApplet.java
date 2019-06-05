/* CS 201 - Final Project
 * Minesweeper
 * 
 * MinesweeperApplet.java
 * 
 * Quinlan McGaugh
 * Paul Ruffolo
 */

import java.awt.*;        // abstract window toolkit
import java.awt.event.*;  // event handling
import java.applet.*;     // Applet classes

import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class MinesweeperApplet extends Applet implements ActionListener
{  
	//Instance Variables
	public static int bombTotal = 20;
	public int bombLeft;
	public static int tiledim = 25; 		// tile dimensions
	public static int tileWidth = 25;		// # of columns
	public static int tileHeight = 25;		// # of rows
	public static Label score;
	public Button ng;
	static final Color grey = new Color(200, 200, 200);
	static final Color dgrey = new Color(55, 55, 55);
	static final Color black = Color.black;
	TileCanvas tilegrid;
	Choice difficultyChoice = new Choice();

	public void init() {  // instead of constructor
		//Initializes everything necessary to run the Applet
		bombLeft = bombTotal;
		setFont(new Font("TimesRoman", Font.BOLD, 48));
		score = new Label(Integer.toString(bombTotal));
		ng = new Button("New Game");
		ng.addActionListener(this);

		tilegrid = makeTilePanel();

		this.setLayout(new BorderLayout());
		this.setBackground(dgrey);
		this.add("North", makeTitlePanel());
		this.add("South", makeDifficultyPanel());
		this.add("Center", tilegrid);

		Panel main = new Panel();
		main.setLayout(new BorderLayout());

		newGame();
	}

	//CHECKS IF THE TILE IS IN BOUNDS
	public static boolean inBounds(int r, int c)
	{
		return((r >= 0) && (r < tileHeight) && (c >= 0) && (c < tileWidth));
	}

	//SETS THE TILE VAL FOR EVERY TILE (HOW MANY SURROUNDING BOMBS THERE ARE)
	public void checkTileVal()
	{
		int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
		int[] dc = {-1, 0, 1, -1, 1, -1, 0, 1};
		int count;
		for (int r = 0; r < tileHeight; r++) {
			for (int c = 0; c < tileWidth; c ++) {
				count = 0;
				for(int k = 0; k < 8; k++)
				{
					int r1 = r + dr[k];
					int c1 = c + dc[k];
					if((inBounds(r1, c1) && (TileCanvas.tiles[r1][c1].getBomb() == 1)))
					{
						count++;
					}
				}
				TileCanvas.tiles[r][c].setTileVal(count);
			}
		}
	}

	//CREATES THE ARRAY OF TILES (empty)
	public void setEmptyTiles() {
		for (int row = 0; row < tileHeight; row++) {
			for (int column = 0; column < tileWidth; column ++) {
				TileCanvas.tiles[row][column] = new Tile();
			}
		}
	}

	//SETS THE BOMBS (RANDOMLY)
	public void setGameBombs() {
		int r, c;
		int i = 0;
		while(i < bombTotal)
		{
			r = (int)(Math.random()*(tileWidth - 1));
			c = (int)(Math.random()*(tileWidth - 1));
			if (!(TileCanvas.tiles[r][c].getBomb() == 1))
			{
				i++;
				TileCanvas.tiles[r][c].changeBomb();
			}
		}
	}

	//MAKES THE TILE PANEL AND ADDS THE MOUSE LISTENER
	public TileCanvas makeTilePanel() {
		tilegrid = new TileCanvas(tiledim, tileWidth * tileHeight);
		tilegrid.addMouseListener(tilegrid);
		return tilegrid;

	}
	
	//ADDS THE CHOICE PANEL TO CHANGE DIFFICULTY
	public Choice makeDifficultyPanel() {
		setFont(new Font("TimesRoman", Font.PLAIN, 14));
		difficultyChoice.add("Very Easy");
		difficultyChoice.add("Easy");
		difficultyChoice.add("Medium");
		difficultyChoice.add("Hard");
		return difficultyChoice;
	}

	//DRAWS THE TOP BAR WITH THE SCORE AND NEW GAME BUTTON
	public Panel makeTitlePanel() {
		Panel titlePanel = new Panel();
		Panel b1 = new Panel();
		b1.setBackground(grey);
		titlePanel.setLayout(new BorderLayout());
		titlePanel.setBackground(grey);
		titlePanel.setForeground(Color.blue);
		titlePanel.add("West", score);
		titlePanel.add("Center", b1);
		titlePanel.add("East", ng);
		return titlePanel;
	}

	//Calls the newGame function when the "New Game" button is clicked
	@Override
	public void actionPerformed(ActionEvent e)
	{
		newGame();
	}

	//Helper method for the newGame method, avoids code duplication
	public void setScore(int bt)
	{
		bombTotal = bt;
		score.setText(Integer.toString(bombTotal));
	}
	
	//FUNCTIONALITY FOR NEW GAME BUTTON
	public void newGame()
	{
		if (difficultyChoice.getSelectedItem() == "Very Easy")
		{
			setScore(20);
		}
		else if (difficultyChoice.getSelectedItem() == "Easy")
		{
			setScore(60);
		}
		else if (difficultyChoice.getSelectedItem() == "Medium")
		{
			setScore(100);
		}
		else
		{
			setScore(130);
		}
		TileCanvas.losevar = false;
		setEmptyTiles();
		setGameBombs();
		checkTileVal();
		tilegrid.repaint();
		TileCanvas.remOpen = TileCanvas.boardWidth * TileCanvas.boardHeight - MinesweeperApplet.bombTotal;
	}
}


@SuppressWarnings("serial")
class TileCanvas extends Canvas implements MouseListener {

	//INSTANCE VARIABLES
	static int boardWidth;
	static int boardHeight;
	static int tilenum;
	static Tile[][] tiles;
	int border = 100;
	static boolean losevar = false;
	static int remOpen;

	//CONSTRUCTOR, Constructs a TileCanvas object
	public TileCanvas(int dimensions, int t) {
		boardWidth = dimensions;
		boardHeight = dimensions;
		tilenum = t;
		tiles = new Tile[boardWidth][boardHeight];
		remOpen = boardWidth * boardHeight - MinesweeperApplet.bombTotal;
	}

	//PAINT METHOD - 
	/* draws every tile, checking if it is
	 * unopened or opened, if it is open, drawing the
	 * number of adjacent bombs, and if it is a 
	 * 0, opening all adjacent tiles	 */
	public void paint(Graphics g) {
		int x = border;
		int y = border;
		int index = 1;

		for (int r = 0; r < boardHeight; r++) {
			for (int c = 0; c < boardWidth; c ++) {
				if (index <= tilenum){
					//Flagged and Unopened
					if ((tiles[r][c].getFlag()) && !(tiles[r][c].getOpen()) && (losevar == false)){
						drawFlag(g, x, y);
					}
					//Not Opened
					else if (!tiles[r][c].getOpen()){
						drawTile(g, x, y);
					}
					//Opened
					else if (tiles[r][c].getOpen()){
						g.setFont(new Font("TimesRoman", Font.BOLD, 14));
						g.setColor(Color.orange);
						g.drawString(Integer.toString(tiles[r][c].minesVal()), x + MinesweeperApplet.tiledim/2 - 3, y + MinesweeperApplet.tiledim/2 + 3);
						g.setColor(MinesweeperApplet.grey);
					}
					//Opened and Bomb
					else if ((tiles[r][c].getBomb() == 1) && (tiles[r][c].getOpen())) {
						g.drawRect(x + 3, y + 3, MinesweeperApplet.tiledim - 3, MinesweeperApplet.tiledim - 3);
						drawLose(g, border);
					}
					//Draws a Bomb if you have lost
					if((tiles[r][c].getBomb() == 1) && (losevar)) {
						drawTile(g, x, y);
						g.setColor(Color.BLACK);
						g.fillOval(x + 8, y + 8, MinesweeperApplet.tiledim/2, MinesweeperApplet.tiledim/2);
						g.setColor(MinesweeperApplet.grey);
						drawLose(g, border);
					}
					//Increase the x index (tile to the right)
					x = x + MinesweeperApplet.tiledim;
					index = index + 1;
				}
			}
			y = y + MinesweeperApplet.tiledim;
			x = border;
			if(remOpen == 0)
			{
				drawWin(g, border);
			}
		}
		MinesweeperApplet.score.setText(Integer.toString(MinesweeperApplet.bombTotal));
	}

	//METHOD FOR DRAWING THE BLACK CIRCLES (bombs)
	public void drawBombs(Graphics g, int x, int y) {
		for (int r = 0; r < boardHeight; r++) {
			for (int c = 0; c < boardWidth; c ++) {
				if(tiles[c][r].getBomb() == 1) {
					g.drawOval(x + 12, y + 12, MinesweeperApplet.tiledim/2, MinesweeperApplet.tiledim/2);
				}
			}
		}		
	}

	//Draws a Flag on a flagged tile
	public void drawFlag(Graphics g, int x, int y) {
		g.setColor(Color.ORANGE);
		g.fillRect(x, y, MinesweeperApplet.tiledim, MinesweeperApplet.tiledim);			
		g.setColor(MinesweeperApplet.black);
		g.drawRect(x, y, MinesweeperApplet.tiledim, MinesweeperApplet.tiledim);
		g.setColor(Color.black);
		g.drawString("F", x + MinesweeperApplet.tiledim/2 - 3, y + MinesweeperApplet.tiledim/2 + 3);
		g.setColor(Color.yellow);
	}

	//Draws the Lose screen
	public void drawLose(Graphics g, int border) {
		g.setFont(new Font("TimesRoman", Font.BOLD, 48));
		g.setColor(Color.red);
		g.drawString("YOU LOSE", MinesweeperApplet.tiledim * MinesweeperApplet.tileWidth / 2, border - 10);
		g.setFont(new Font("TimesRoman", Font.BOLD, 14));
	}
	
	//Draws the win screen
	public void drawWin(Graphics g, int border) {
		g.setFont(new Font("TimesRoman", Font.BOLD, 48));
		g.setColor(Color.green);
		g.drawString("YOU WIN!!", MinesweeperApplet.tiledim * MinesweeperApplet.tileWidth / 2, border - 10);
		g.setFont(new Font("TimesRoman", Font.BOLD, 14));
	}

	//Draws an unopened tile
	public void drawTile(Graphics g, int x, int y) {
		g.setColor(MinesweeperApplet.grey);
		g.fillRect(x, y, MinesweeperApplet.tiledim, MinesweeperApplet.tiledim);			
		g.setColor(MinesweeperApplet.black);
		g.drawRect(x, y, MinesweeperApplet.tiledim, MinesweeperApplet.tiledim);
		g.setColor(Color.BLUE);
	}

	//Called every time a mouse is clicked
	public void mousePressed(MouseEvent event) {
		Point p = event.getPoint();
		int x = p.x - border;
		int y = p.y - border;
		int row = y / MinesweeperApplet.tiledim;
		int col = x / MinesweeperApplet.tiledim;

		// checks if clicked in box area
		if(((x > 0) && (x < tilenum) && (y > 0) && (y < tilenum)) && (losevar == false) && (remOpen != 0))
		{
			if (SwingUtilities.isRightMouseButton(event)) {
				tiles[row][col].flag();
			}
			else if ((SwingUtilities.isLeftMouseButton(event)) 
					&& (tiles[row][col].getFlag() == false)) {
				//CHECKS IF UNOPENED
				if (tiles[row][col].getBomb() == 1) {
					losevar = true;
				}
				
				else if (!tiles[row][col].getOpen()) {
					//CHECKS IF EMPTY TILE
					if(tiles[row][col].minesVal() == 0)
					{
						openEmpty(row, col);
					}
					//OPENS TILE
					else {
						tiles[row][col].changeOpen();
					}
				}				
			}
			repaint();
		}   
	}


	//RECURSIVE FUNCTION TO OPEN ALL ADJACENT EMPTY TILES (tiles with "0" surrounding bombs
	public void openEmpty(int r, int c) {
		//if ((r < 0) || (r >= MinesweeperApplet.tileWidth) || (c < 0) || (c >= MinesweeperApplet.tileWidth))
		if (!(MinesweeperApplet.inBounds(r, c)))
			return;
		else if (!tiles[r][c].getOpen()) {
			tiles[r][c].changeOpen();
			if (tiles[r][c].minesVal() == 0) {
				openEmpty(r + 1, c);    			
				openEmpty(r - 1, c);
				openEmpty(r, c - 1);
				openEmpty(r, c + 1);
				openEmpty(r + 1, c - 1);
				openEmpty(r + 1, c + 1);
				openEmpty(r - 1, c - 1);
				openEmpty(r - 1, c + 1);
			}
		}
	}  

	//UNUSED EVENT METHODS
	public void mouseReleased(MouseEvent event) { }
	public void mouseClicked(MouseEvent event) { }
	public void mouseEntered(MouseEvent event) { }
	public void mouseExited(MouseEvent event) { } 
}
