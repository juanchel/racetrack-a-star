package astar;

public class Coordinate {
	public int x;
	public int y;
	public Coordinate (int a, int b) {
		x=a;
		y=b;
	}
	public Coordinate (Coordinate c) {
		x=c.x;
		y=c.y;
	}
	public void add(Coordinate c) {
		x+=c.x;
		y+=c.y;
	}
}
