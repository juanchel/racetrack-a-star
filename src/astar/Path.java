package astar;

import java.util.*;

public class Path implements Comparable<Path> {
	public ArrayList<Coordinate> pathTaken;
	public Coordinate location;
	public int gValue;
	public double heuristic=9999999;
	
	
	public Path(Coordinate coor) {
		pathTaken = new ArrayList<Coordinate>();
		location = coor;
		gValue = 0;
	}
	
	public Path(Path p, Coordinate coor) {
		pathTaken = new ArrayList<Coordinate>();
		pathTaken.addAll(p.pathTaken);
		pathTaken.add(coor);
		
		location = new Coordinate(p.location);
		location.add(coor);
		
		gValue = p.gValue+1;
	}
	
	public Coordinate currentMovement () {
		if (pathTaken.isEmpty())
			return new Coordinate(0,0);
		else
			return pathTaken.get(pathTaken.size()-1);
	}

	public int compareTo(Path o) {
		return (int)heuristic+gValue-(int)o.heuristic-o.gValue;
	}

}
