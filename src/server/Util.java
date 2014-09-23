package server;

import java.util.Random;
import java.util.Vector;

import remoteInterface.Coordinates;
import remoteInterface.IClient;
import remoteInterface.Player;

public class Util {

	public static int[][] initRandomGrid(int gridSize,
			int numTreasures) {

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

	public static void printTreasureInfo(int gridSize,
			int[][] treasures) {

		printGrid(treasures, "Treasure Positions");
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

	public static void printPlayerInfo(int gridSize, Vector<Player> players) {

		int[][] grid = new int[gridSize][gridSize];
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				grid[i][j] = -1;
			}
		}

		for (Player player : players) {
			grid[player.coordinates.x][player.coordinates.y] = player.id;
		}

		printGrid(grid, "Player Positions");
	}

	private static void printGrid(int[][] grid, String title) {

		System.out.println("--- " + title + " ---");

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				System.out.format("%4d", grid[i][j]);
			}
			System.out.println();
		}

		for (int i = 0; i < title.length() + 8; i++) {
			System.out.print("-");
		}
		System.out.println();

	}

}
