package remoteInterface;

import java.util.Random;

public enum MoveDirection {
	N, S, E, W, NoMove;

	private static final Random RANDOM = new Random();

	public static MoveDirection getRandDir() {
		return values()[RANDOM.nextInt(values().length)];
	}
}
