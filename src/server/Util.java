package server;

import java.util.Random;
import java.util.Vector;

import remoteInterface.Coordinates;

public class Util {

	public static Vector<Coordinates> initRandomGrid(int gridSize, int numTreasures) {
		Vector<Coordinates> vector = new Vector<Coordinates>();
		Random rand = new Random();
		for (int i = 0; i < numTreasures; i++) {
			Coordinates coord = new Coordinates();
			coord.x = randInt(rand, 0, gridSize - 1);
			coord.y = randInt(rand, 0, gridSize - 1);
			vector.add(coord);
		}
		return vector;
	}
	
	private static int randInt(Random rand, int min, int max) {
		return rand.nextInt((max - min) + 1) + min;
	}
	
	public static void printGridInfo(int gridSize,
			Vector<Coordinates> coordinates, String title) {

		int[][] grid = new int[gridSize][gridSize];

		for (Coordinates coord : coordinates) {
			grid[coord.x][coord.y]++;
		}

		System.out.println("--- " + title + " ---");
		
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				System.out.print(grid[i][j] + " ");
			}
			System.out.println();
		}
		
		for (int i = 0; i < title.length() + 8; i++) {
			System.out.print("-");
		}
		System.out.println();
	}
	
}
