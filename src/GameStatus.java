

import java.io.Serializable;
import java.util.Vector;

/**
 * The object that is sent back to clients updating them about the game state
 */
public class GameStatus implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Grid Size
	 */
	public int gridSize;
	
	/**
	 * Number of Treasures
	 */
	public int numTreasures;
	
	/**
	 * Number of Treasures left
	 */
	public int numTreasuresLeft;
	
	/**
	 * Their new location
	 */
//	public Coordinates newLocation;
	
	/**
	 * Treasures found from their latest move
	 */
//	public int newTreasuresFound;
	
	/**
	 * Total treasures they found so far
	 */
//	public int totalTreasuresFound;
	
	/**
	 * Coordinates of all players
	 */
	public int[][] playersGrid;
	
	public Vector<Player> players;
	
	/**
	 * Treasures in the grid
	 */
	public int[][] treasuresGrid;
	
	public IServer primaryServer;
	
	public IServer backupServer;
	
	public boolean isGameStarted = false;
	
	public synchronized void print() {
		System.out.println("////////");
		System.out.println(numTreasuresLeft + " treasures left");
		printTreasuresGrid();
		printPlayersGrid();
		System.out.println("////////");
		System.out.println();
	}
	
	public void printTreasuresGrid() {
		printGrid(this.treasuresGrid, "Treasures");
	}

	public void printPlayersGrid() {
		printGrid(this.playersGrid, "Players");
	}

	private void printGrid(int[][] grid, String title) {

		System.out.println("--- " + title + " ---");

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.format("%4d", grid[i][j]);
			}
			System.out.println();
		}

	}
	
}
