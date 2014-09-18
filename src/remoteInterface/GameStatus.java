package remoteInterface;

import java.util.Vector;

/**
 * The object that is sent back to clients updating them about the game state
 */
public class GameStatus {
	/**
	 * Their new location
	 */
	public MoveDirection newLocation;
	
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
	public Vector<Coordinates> playerPositions;
	
	/**
	 * Coordinates of all remaining treasures
	 */
	public Vector<Coordinates> treasurePositions;
}
