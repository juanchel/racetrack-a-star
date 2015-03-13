package astar;

import java.io.*;

public class App {
	
	public static void main(String[] args) {
		String trackName;
		BufferedReader fileBuffer;
		
		Racetrack raceTrack;
		int trackWidth, trackHeight;
		
		try {
			trackName = "ez.txt";
			fileBuffer = new BufferedReader(new FileReader(trackName));
			
			String[] tempSplit;
			
			tempSplit = fileBuffer.readLine().split(" ");
			trackWidth = Integer.valueOf(tempSplit[1]);
			
			tempSplit = fileBuffer.readLine().split(" ");
			trackHeight = Integer.valueOf(tempSplit[1]);
			
			raceTrack = new Racetrack(trackWidth, trackHeight);
			
			for (int i=0; i<trackHeight; i++) {
				raceTrack.addRow(fileBuffer.readLine(), i);
			}
		} catch (IOException e) {
			System.out.println("Error: Invalid file");
			return;
		}
		
		System.out.println("File successfully read");
		
		raceTrack.aStar();
		
		System.out.println("Path Found");
		
		raceTrack.savePath();
	}
	
}
