package astar;

import java.io.*;
import java.util.*;

public class Racetrack {
	ArrayList<ArrayList<Tile>> raceTrack = new ArrayList<ArrayList<Tile>>();
	ArrayList<Coordinate> finishTiles = new ArrayList<Coordinate>();
	int width, height;
	Coordinate startCoor;
	int nodesExpanded = 0;

	public Racetrack (int w, int h) {
		width = w;
		height = h;
	}

	// Run the A* search
	public void aStar () {
		PriorityQueue<Path> openList = new PriorityQueue<Path>();
		openList.add(new Path(startCoor));

		while (!openList.isEmpty()) {
			Path minFPath = openList.poll();

			// Check to see if the path is already at the finish
			if (getTile(minFPath.location).type=='F') {
				Coordinate baseCoor = new Coordinate(startCoor);
				
				for (Coordinate c : minFPath.pathTaken) {
					baseCoor.add(c);
					getTile(baseCoor).type = 'O';
					
					System.out.println(String.valueOf(c.x)+" , "+String.valueOf(c.y));
				}

				return;
			}
			
			nodesExpanded++;
			openList.addAll(expandPath(minFPath));
		}
	}

	// Expands a path's node
	public ArrayList<Path> expandPath (Path p) {
		ArrayList<Path> validPaths = new ArrayList<Path>();
		for (int i=-1; i<2; i++) {
			for (int j=-1; j<2; j++) {
				
				if (Math.max(Math.abs(p.currentMovement().x+i), Math.abs(p.currentMovement().x+i)) > 30) {
					continue; 
				}
				
				Coordinate nextLocation = new Coordinate(p.location);
				nextLocation.add(new Coordinate(i, j));
				nextLocation.add(p.currentMovement());
				
				Coordinate nextMovement = new Coordinate(p.currentMovement());
				nextMovement.add(new Coordinate(i, j));

				Vector<int[]> brokenMovementVector = breakTheMovementVector(nextMovement.x, nextMovement.y);
				Coordinate vectorCoor = new Coordinate(p.location);
				boolean collision = false;
				
				for (int[] iCoor : brokenMovementVector) {
					vectorCoor.x+=iCoor[0];
					vectorCoor.y+=iCoor[1];
					if (vectorCoor.x >= 0 && vectorCoor.y >= 0 && vectorCoor.x < width && vectorCoor.y < height) {
						if (getTile(vectorCoor).type=='#') {
							collision = true;
						}
					}
				}

				if (collision)
					continue;

				Path toAdd = new Path(p, nextMovement);

				int gValue = toAdd.gValue;
				double heuristic = 9999999;

				Coordinate closestFin = new Coordinate(0, 0);
				int minClosestDistance = 9999999;

				for (Coordinate c : finishTiles) {
					int distBetween = Math.max(Math.abs(c.x - toAdd.location.x), Math.abs(c.y - toAdd.location.y));
					if (distBetween < minClosestDistance) {
						minClosestDistance = distBetween;
						closestFin = new Coordinate(c);
					}
				}

				double hX = 0;
				double hY = 0;
				int speedX = toAdd.currentMovement().x;
				int speedY = toAdd.currentMovement().y;
				int distX = closestFin.x - toAdd.location.x;
				int distY = closestFin.y - toAdd.location.y;

				if (distX < 0) {
					distX*=-1;
					speedX*=-1;
				}
				if (distY < 0) {
					distY*=-1;
					speedY*=-1;
				}

				while (distX > 0) {
					if (speedX < 30)
						speedX++;
					distX-=speedX;
					hX++;
				}

				while (distY > 0) {
					if (speedY < 30)
						speedY++;
					distY-=speedY;
					hY++;
				}

				double possibleHeuristic = Math.max(hX, hY);

				if (toAdd.heuristic > possibleHeuristic)
					toAdd.heuristic = possibleHeuristic;

				validPaths.add(new Path(p, nextMovement));
			}
		}
		return validPaths;
	}

	// Calculate all heuristics using Manhattan geometry
	/*
	public void taxicabHeuristic () {
		int maxDistance = width + height;
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				if (getTile(x, y).heuristic < -4) {
					int minDistance = maxDistance;
					for (Coordinate c : finishTiles) {
						int currentDistance = Math.max(Math.abs(c.x-x), Math.abs(c.y-y));
						if (minDistance > currentDistance) {
							minDistance = currentDistance;
						}
					}
					getTile(x, y).heuristic = minDistance;
				}
			}
		}
	}

	// Calculates a heuristic that would be accurate if there were no off-road tiles
	public void noOffroadHeuristic () {
		for (int y=0; y<height; y++) {
			for (int x=0; x<width; x++) {
				if (getTile(x, y).heuristic < -4) {
					float minDistance = 1048576;
					for (Coordinate c : finishTiles) {
						int dFromStart = Math.max(Math.abs(startCoor.x-x), Math.abs(startCoor.y-y));
						int dFromGoal = Math.max(Math.abs(c.x-x), Math.abs(c.x-x));
						int maxPossibleSpeed = 0;
						int stepsFromGoal = 0;

						while (dFromStart > 0) {
							dFromStart-=maxPossibleSpeed;
							if (maxPossibleSpeed < 30)
								maxPossibleSpeed++;
						}

						while (dFromGoal > maxPossibleSpeed) {
							dFromGoal-=maxPossibleSpeed;
							stepsFromGoal++;
							if (maxPossibleSpeed < 30)
								maxPossibleSpeed++;
						}

						float tempHeuristic = (float)stepsFromGoal + (float)dFromGoal/(float)maxPossibleSpeed;
						if (minDistance > tempHeuristic)
							minDistance = tempHeuristic;
					}
					getTile(x, y).heuristic = minDistance;
				}
			}
		}
	}
	 */

	// Add a row into the stored race track tiles
	public void addRow (String r, int y) {
		ArrayList<Tile> row = new ArrayList<Tile>();
		for (int x=0; x<width; x++) {
			row.add(new Tile(r.charAt(x)));
			if (r.charAt(x)=='S') {
				startCoor = new Coordinate(x, y);
			} else if (r.charAt(x)=='F') {
				finishTiles.add(new Coordinate(x, y));
			}
		}
		raceTrack.add(row);
	}

	public void savePath () {
		PrintWriter outFile;
		try {
			outFile = new PrintWriter(new FileWriter("path.txt"));
			for (ArrayList<Tile> row : raceTrack) {
				for (Tile t : row) {
					outFile.print(t.type);
				}
				outFile.print('\n');
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*
	public void printHeuristics () {
		PrintWriter outFile;
		try {
			outFile = new PrintWriter(new FileWriter("ez.txt"));
			
			for (ArrayList<Tile> row : raceTrack) {
				for (Tile t : row) {
					String s;
					if (t.heuristic > -.5) {
						s = String.valueOf(t.heuristic);
						s = s.substring(0, 3);
					} else {
						s = "---";
					}
					
					
					outFile.print(s);
					outFile.print(' ');
				}
				outFile.print('\n');
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/
	
	// Get a tile at a location
	public Tile getTile (int x, int y) {
		return raceTrack.get(y).get(x);
	}
	public Tile getTile(Coordinate c) {
		return raceTrack.get(c.y).get(c.x);
	}

	// Breaks a vector down to multiple unit vectors along axes
	private static Vector<int[]> breakTheMovementVector(int i, int j){
		Vector<int[]> result = new Vector<int[]>();
		int signOfI;
		int signOfJ;
		double ii = new Double(Math.abs(i));
		double jj = new Double(Math.abs(j));
		if(i==0 && j==0)
			return result;
		if (i > 0) signOfI = 1; else signOfI = -1;
		if (j > 0) signOfJ = 1; else signOfJ = -1;
		if (j == 0){
			for (int f = 0; f < ii; f++){
				int[] point = {signOfI, 0};
				result.add(point);
			}
			return result;
		}
		if (i == 0){
			for (int f = 0; f < jj; f++){
				int[] point = {0, signOfJ};
				result.add(point);
			}
			return result;
		}
		Double a = 1.0;
		Double b = 1.0;
		double cross = ((ii/jj) * (a-.5) + .5);
		while (b <= ii || a <= jj){
			if (b > cross){
				int[] point = {0, signOfJ};
				result.add(point);
				a++;
				cross = ((ii/jj) * (a-.5) + .5);
			}else if (b == cross){
				int[] point = {signOfI, signOfJ};
				result.add(point);
				a++;
				b++;
				cross = ((ii/jj) * (a-.5) + .5);
			}else if (b < cross){
				int[] point = {signOfI, 0};
				result.add(point);
				b++;
			}
		}
		return result;
	}
}
