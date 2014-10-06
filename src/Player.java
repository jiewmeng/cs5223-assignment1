

import java.io.Serializable;

public class Player implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int id;
	public Coordinates coordinates;
	public int newTreasuresFound = 0;
	public int totalTreasuresFound = 0;
}
