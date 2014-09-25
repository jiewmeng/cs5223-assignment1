package server;

import java.util.Arrays;
import java.util.Random;
import java.util.Vector;

import remoteInterface.Coordinates;
import remoteInterface.IClient;
import remoteInterface.Player;

public class Util {

	public static int[][] initTreasuresGrid(int gridSize, int numTreasures) {

		int[][] grid = new int[gridSize][gridSize];
		Random rand = new Random();

		for (int i = 0; i < numTreasures; i++) {
			grid[randInt(rand, 0, gridSize - 1)][randInt(rand, 0, gridSize - 1)]++;
		}

		return grid;
	}

	private static int randInt(Random rand, int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}

	public static Vector<Player> initPlayers(int gridSize,
			Vector<IClient> clients) {

		Vector<Player> players = new Vector<Player>();
		Vector<Coordinates> coords = new Vector<Coordinates>();

		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				Coordinates coord = new Coordinates(i, j);
				coords.add(coord);
			}
		}

		// Random assign player position
		Random rand = new Random();
		for (int k = 0; k < clients.size(); k++) {
			Player player = new Player();
			player.id = k;
			player.coordinates = coords.remove(randInt(rand, 0,
					coords.size() - 1));
			players.add(player);
		}

		return players;
	}

	public static int[][] initPlayersGrid(int gridSize, Vector<Player> players) {

		int[][] playersGrid = new int[gridSize][gridSize];
		fill2dArray(playersGrid, -1);

		for (Player p : players) {
			playersGrid[p.coordinates.x][p.coordinates.y] = p.id;
		}

		return playersGrid;
	}

	private static void fill2dArray(int[][] arr, int val) {
		for (int i = 0; i < arr.length; i++) {
			Arrays.fill(arr[i], val);
		}
	}

}
