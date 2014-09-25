package remoteInterface;

import java.util.Vector;

/**
 * The object that is sent back to clients updating them about the game state
 */
public class GameStatus {

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
	public Coordinates newLocation;
	
	/**
	 * Treasures found from their latest move
	 */
	public int newTreasuresFound;
	
	/**
	 * Total treasures they found so far
	 */
	public int totalTreasuresFound;
	
	/**
	 * Coordinates of all players
	 */
	public int[][] playersGrid;
	
	public Vector<Player> players;
	
	/**
	 * Treasures in the grid
	 */
	public int[][] treasures;
	
}
