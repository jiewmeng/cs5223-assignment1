package remoteInterface;

/**
 * x, y position of a player or treasure
 */
public class Coordinates {
	public int x;
	public int y;
	
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public boolean equals(Object c) {
		Coordinates coord = (Coordinates)c;
		return (this.x == coord.x && this.y == coord.y);
	}
	
}
