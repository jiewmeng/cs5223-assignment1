package remoteInterface;

import java.util.Random;

public enum MoveDirection {
	
	N ("Moving north"), 
	S ("Moving south"), 
	E ("Moving east"), 
	W ("Moving west"), 
	NO_MOVE ("Not moving");

	private final String desc;
	private static final Random RANDOM = new Random();

	MoveDirection(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void print() {
		System.out.println(this.getDesc());
	}
	
	public static MoveDirection getRandDir() {
		return values()[RANDOM.nextInt(values().length)];
	}
}
