import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

//Tony Jojan, jojan@usc.edu, CSCI 360

public class marsrover {
	static final double gamma = 0.9;
	static final double epsilon = 0.1;

	static double fetchUtility(int x, int y, int grid_size, double[][] utilities) {
		if (x >= grid_size) {
			x = grid_size - 1;
		} 
		else if (x <= 0) {
			x = 0;
		}
		
		if (y >= grid_size) {
			y = grid_size - 1;
		} 
		else if (y <= 0) {
			y = 0;
		}
		
		return utilities[x][y];
		
	}

	public static void main(String[] args) {
		
		FileReader fileReader;
		
		try {
			fileReader = new FileReader("input.txt");
			BufferedReader reader = new BufferedReader(fileReader);
			
			int grid_size = Integer.parseInt(reader.readLine());
			double[][] reward = new double[grid_size][grid_size];
			double[][] utility = new double[grid_size][grid_size];
			char[][] output = new char[grid_size][grid_size];
			double delta = 0;

			
			for(int i = 0; i < grid_size; i++) {
				for(int j = 0; j < grid_size; j++) {
					reward[i][j] = -1.0;
				}
			}
			for(int i = 0; i < grid_size; i++) {
				for(int j = 0; j < grid_size; j++) {
					utility[i][j] = 0.0;
				}
			}
			
			int num_obstacles = Integer.parseInt(reader.readLine());
			
			for(int i = 0; i <= num_obstacles; i++) {
				
				String current_line = reader.readLine();
				String[] current_array = current_line.split(",");
				
				if(i==num_obstacles) {
					reward[Integer.parseInt(current_array[1])][Integer.parseInt(current_array[0])] = 99.0;
					output[Integer.parseInt(current_array[1])][Integer.parseInt(current_array[0])] = '.';

				}
				else {
					reward[Integer.parseInt(current_array[1])][Integer.parseInt(current_array[0])] = -101.0;
					output[Integer.parseInt(current_array[1])][Integer.parseInt(current_array[0])] = 'o';


				}
			}
			
			double[][] new_utility = new double[grid_size][grid_size];
			
			//value iteration
			do {
				
				delta = 0;
				
				boolean reach_dest = false;
				
				//calculate utility
				for(int i = 0; i < grid_size; i++) {
					for(int j = 0; j < grid_size; j++) {
						
						if(reward[j][i]==99) {
							reach_dest = true;
						} else {
							reach_dest = false;
						}
						
						double north = 0.7 * fetchUtility(j-1, i, grid_size, utility)
								+ 0.1 * fetchUtility(j+1, i, grid_size, utility)
								+ 0.1 * fetchUtility(j, i+1, grid_size, utility)
							+ 0.1 * fetchUtility(j, i-1, grid_size, utility);
						//down
							//up + down + left + right
						double south = 0.7 * fetchUtility(j+1, i, grid_size, utility)
									+ 0.1 * fetchUtility(j-1, i, grid_size, utility)
									+ 0.1 * fetchUtility(j, i-1, grid_size, utility)
									+ 0.1 * fetchUtility(j, i+1, grid_size, utility);
						//left
							//up + down + left + right
						double west = 0.7 * fetchUtility(j, i-1, grid_size, utility)
									+ 0.1 * fetchUtility(j, i+1, grid_size, utility)
									+ 0.1 * fetchUtility(j-1, i, grid_size, utility)
									+ 0.1* fetchUtility(j+1, i, grid_size, utility);
						//right
							//up + down + left + right
						double east = 0.7* fetchUtility(j, i+1, grid_size, utility)
									+ 0.1 * fetchUtility(j, i-1, grid_size, utility)
									+ 0.1 * fetchUtility(j+1, i, grid_size, utility)
									+ 0.1 * fetchUtility(j-1, i, grid_size, utility);
						double a = north;
						double b = south;
						double c = east;
						double d = west;

						double maximumUtility = Math.max(Math.max(north, south), 
														Math.max(east, west));
						//compute policy
						if(output[j][i] != 'o' && output[j][i] != '.') {
							if(maximumUtility == north) {
								output[j][i] = '^';							
							} else if (maximumUtility == south) {
								output[j][i] = 'v';
							} else if(maximumUtility == east) {
								output[j][i] = '>';						
							} else if(maximumUtility == west) {
								output[j][i] = '<';							
							} 
						}

						double previous_utility = utility[j][i];

						new_utility[j][i] = reward[j][i] + gamma * maximumUtility;
						
						//utility of destination should always be 99
						if(reach_dest) {
							new_utility[j][i] = 99;
						}
						
						if(Math.abs(new_utility[j][i] - previous_utility) > delta) {
							delta = Math.abs(new_utility[j][i] - previous_utility);
						}
					}
				} 	
				
				//updating utility with new utilities
				for(int x = 0; x< grid_size; x++) {
					for(int y=0; y < grid_size; y++) {
						utility[x][y] = new_utility[x][y];
					}
				}


			}while(delta >= ((epsilon * (1 - gamma)) / gamma));
			
			//writing output
			PrintWriter writer = new PrintWriter("output.txt");
			for(int x = 0; x < grid_size; x++) {
				for(int y = 0; y < grid_size; y++) {
					writer.print(output[x][y]);
				}
				writer.print("\n");
			}
			reader.close();
			writer.close();
			
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		} 
	}
	

}
