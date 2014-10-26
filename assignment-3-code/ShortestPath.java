/* ShortestPath.java
   CSC 226 - Fall 2014
   Assignment 3 - Template for Dijkstra's Algorithm

   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java ShortestPath

   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
	java ShortestPath file.txt
   where file.txt is replaced by the name of the text file.

   The input consists of a series of graphs in the following format:

    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>

   Entry A[i][j] of the adjacency matrix gives the weight of the edge from
   vertex i to vertex j (if A[i][j] is 0, then the edge does not exist).
   Note that since the graph is undirected, it is assumed that A[i][j]
   is always equal to A[j][i].

   An input file can contain an unlimited number of graphs; each will be
   processed separately.


   B. Bird - 08/02/2014
*/

import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;
import java.util.PriorityQueue;
import java.util.ArrayList;

class Data implements Comparable<Data> {
	int index, weight;
	boolean scanned;
	ArrayList<Integer> neighbors;

	Data(int index, int weight, ArrayList<Integer> neighbors) {
		this.index = index;
		this.weight = weight;
		this.neighbors = neighbors;
		this.scanned = false;
	}

	@Override
	public int compareTo(Data other) {
		return Integer.compare(this.weight, other.weight);
	}
}

//Do not change the name of the ShortestPath class
public class ShortestPath{
	/* ShortestPath(G)
		Given an adjacency matrix for graph G, return the total weight
		of a minimum weight path from vertex 0 to vertex 1.

		If G[i][j] == 0, there is no edge between vertex i and vertex j
		If G[i][j] > 0, there is an edge between vertices i and j, and the
		value of G[i][j] gives the weight of the edge.
		No entries of G will be negative.
	*/
	static int ShortestPath(int[][] G) {
		int numVerts = G.length;
		int totalWeight = 0;
		/* ... Your code here ... */
		PriorityQueue<Data> queue = new PriorityQueue<Data>(numVerts);
		Data[] data = new Data[numVerts];
		for (int i=0; i < numVerts; i++) {
			// Get Neighbors
			ArrayList<Integer> neighbors = new ArrayList<Integer>(numVerts);
			for (int j=0; j < numVerts; j++) {
				if (G[i][j] != 0) {
					neighbors.add(j);
				}
			}
			// The source is 0 distance, rest are inf.
			int weight;
			if (i == 0) {
				weight = 0;
			} else {
				weight = Integer.MAX_VALUE;
			}
			// Create the data node.
			data[i] = new Data(i, weight, neighbors);
			// Add to the queue.
			queue.add(data[i]);
		}

		Data current;
		do {
			// Get the minimum.
			current = queue.poll();
			// System.out.printf("Iterated i:%s w:%s\n", current.index, current.weight);
			// Is it the target? (Index i?)
			if (current.index == 1) {
				// Done.
				break;
			}
			// Mark scanned
			current.scanned = true;
			for (int i : current.neighbors) {
				if (data[i].scanned == false) {
					int candidate = current.weight + G[current.index][i];
					if (data[i].weight > candidate) {
						// System.out.printf("Removing %d with weight %d\n", data[i].index, data[i].weight);
						queue.remove(data[i]);
						data[i].weight = candidate;
						// System.out.printf("Adding %d with weight %d\n", data[i].index, data[i].weight);
						queue.add(data[i]);
					}
				}
			}
		} while (current != null);

		/* ... End of code ... */
		return current.weight;

	}

	/* main()
	   Contains code to test the ShortestPath function. You may modify the
	   testing code if needed, but nothing in this function will be considered
	   during marking, and the testing process used for marking will not
	   execute any of the code below.
	*/
	public static void main(String[] args){
		Scanner s;
		if (args.length > 0){
			try{
				s = new Scanner(new File(args[0]));
			} catch(java.io.FileNotFoundException e){
				System.out.printf("Unable to open %s\n",args[0]);
				return;
			}
			System.out.printf("Reading input values from %s.\n",args[0]);
		}else{
			s = new Scanner(System.in);
			System.out.printf("Reading input values from stdin.\n");
		}

		int graphNum = 0;
		double totalTimeSeconds = 0;

		//Read graphs until EOF is encountered (or an error occurs)
		while(true){
			graphNum++;
			if(graphNum != 1 && !s.hasNextInt())
				break;
			System.out.printf("Reading graph %d\n",graphNum);
			int n = s.nextInt();
			int[][] G = new int[n][n];
			int valuesRead = 0;
			for (int i = 0; i < n && s.hasNextInt(); i++){
				for (int j = 0; j < n && s.hasNextInt(); j++){
					G[i][j] = s.nextInt();
					valuesRead++;
				}
			}
			if (valuesRead < n*n){
				System.out.printf("Adjacency matrix for graph %d contains too few values.\n",graphNum);
				break;
			}
			long startTime = System.currentTimeMillis();

			int totalWeight = ShortestPath(G);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;

			System.out.printf("Graph %d: Minimum weight of a 0-1 path is %d\n",graphNum,totalWeight);
		}
		graphNum--;
		System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
	}
}
