/* NetworkFlow.java
   CSC 226 - Fall 2014
   Assignment 4 - Max. Flow Template

   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
	java NetworkFlow

   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
	java NetworkFlow file.txt
   where file.txt is replaced by the name of the text file.

   The input consists of a series of directed graphs in the following format:

    <number of vertices>
	<adjacency matrix row 1>
	...
	<adjacency matrix row n>

   Entry A[i][j] of the adjacency matrix gives the capacity of the edge from
   vertex i to vertex j (if A[i][j] is 0, then the edge does not exist).
   For network flow computation, the 'source' vertex will always be vertex 0
   and the 'sink' vertex will always be vertex 1.

   An input file can contain an unlimited number of graphs; each will be
   processed separately.


   B. Bird - 07/05/2014
*/

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;

//Do not change the name of the NetworkFlow class
public class NetworkFlow{

	/* MaxFlow(G)
	   Given an adjacency matrix describing the structure of a graph and the
	   capacities of its edges, return a matrix containing a maximum flow from
	   vertex 0 to vertex 1 of G.
	   In the returned matrix, the value of entry i,j should be the total flow
	   across the edge (i,j).
	*/
	static int[][] MaxFlow(int[][] G){
		int numVerts = G.length;
		/* ... Your code here ... */
		int sink =  1;
		int source = 0;
		int[][] flow = new int[G.length][G.length];

		Deque<Integer> path = find(G, 0, 1);
		while (path != null) {
			// System.out.println("---Found path size " + path);
			int minPath = maxFlowAlongPath(G, path);
			// System.out.println("Max " + minPath);
			int prev = path.pop(); // The Source.
			while (!path.isEmpty()) {
				int i = path.pop();
				// System.out.println("	Prev: " + prev + " Index: " + i + " Weight: " + G[prev][i] + " Back: " + G[i][prev]);
				G[prev][i] -= minPath;
				G[i][prev] += minPath;
				flow[prev][i] += minPath;
				prev = i;
			}
			// Find a new Path.
			path = find(G, 0, 1);
		}
		return flow;
	}

	static int maxFlowAlongPath(int[][] g, Deque<Integer> path) {
		int minCapacity = Integer.MAX_VALUE;
		// System.out.println("It's " + path.peekFirst());
		Deque<Integer> temp = new ArrayDeque<Integer>(path);
		int prev = temp.removeFirst(); // The Source
		// System.out.println("	Prev: " + prev);
		while (!temp.isEmpty()) {
			int candidate = temp.removeFirst();
			// System.out.println("	Cand: " + candidate);
			if (g[prev][candidate] < minCapacity) {
				minCapacity = g[prev][candidate];
			}
			prev = candidate;
		}
		return minCapacity;
	}

	static ArrayList<Integer> getLinks(int[][] g, int x) {
		ArrayList<Integer> links = new ArrayList<Integer>();
		for (int i=0; i < g.length; i++) {
			if (g[x][i] > 0) {
				// System.out.println("	Found Link " + i);
				links.add(i);
			}
		}
		return links;
	}

	static Deque<Integer> find(int[][] g, int source, int destination) {
		// Does BFS
		int current;
		int[] parents = new int[g.length];
		Arrays.fill(parents, -1);
		boolean[] viewed = new boolean[g.length];
		Queue<Integer> queue = new LinkedList<Integer>();
		queue.add(source);
		while (!queue.isEmpty()) {
			// Find a new unviewed node.
			current = queue.remove();

			// If it's already marked, ditch it, otherwise mark it.
			if (viewed[current] == true) { continue; }
			else { viewed[current] = true; }

			// System.out.println("Browsing " + current);
			// System.out.println("parents " + Arrays.toString(parents));
			// System.out.println("viewed " + Arrays.toString(viewed));
			// If we're done, finish.
			if (current == destination) {
				// Want to "trace back" the path.
				int step = destination;
				Deque<Integer> path = new ArrayDeque<Integer>();
				// System.out.println("parents " + Arrays.toString(parents));
				while (true) {
					// System.out.println("" + step);
					path.addFirst(step); // Destination is the last item
					if (step == source) { // Source is the first item
						break;
					} else {
						// System.out.println("Adding " + step + " Weight " + g[parents[step]][step]);
						step = parents[step];
					}
				}
				return path; // Found the destination.
			} else {
				ArrayList<Integer> links = getLinks(g, current);
				for (int i : links) {
					// System.out.println("Links " + i);
					if (viewed[i] == false) {
						queue.add(i);
						parents[i] = current;
					}
				}
			}
		}
		return null; // None found.
	}


	public static boolean verifyFlow(int[][] G, int[][] flow){

		int n = G.length;

		//Test that the flow on each edge is less than its capacity.
		for (int i = 0; i < n; i++){
			for (int j = 0; j < n; j++){
				if (flow[i][j] < 0 || flow[i][j] > G[i][j]){
					System.err.printf("ERROR: Flow from vertex %d to %d is out of bounds.\n",i,j);
					return false;
				}
			}
		}

		//Test that flow is conserved.
		int sourceOutput = 0;
		int sinkInput = 0;
		for (int j = 0; j < n; j++)
			sourceOutput += flow[0][j];
		for (int i = 0; i < n; i++)
			sinkInput += flow[i][1];

		if (sourceOutput != sinkInput){
			System.err.printf("ERROR: Flow leaving vertex 0 (%d) does not match flow entering vertex 1 (%d).\n",sourceOutput,sinkInput);
			return false;
		}

		for (int i = 2; i < n; i++){
			int totalIn = 0, totalOut = 0;
			for (int j = 0; j < n; j++){
				totalIn += flow[j][i];
				totalOut += flow[i][j];
			}
			if (totalOut != totalIn){
				System.err.printf("ERROR: Flow is not conserved for vertex %d (input = %d, output = %d).\n",i,totalIn,totalOut);
				return false;
			}
		}
		return true;
	}

	public static int totalFlowValue(int[][] flow){
		int n = flow.length;
		int sourceOutput = 0;
		for (int j = 0; j < n; j++)
			sourceOutput += flow[0][j];
		return sourceOutput;
	}

	/* main()
	   Contains code to test the MaxFlow function. You may modify the
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

			int[][] G2 = new int[n][n];
			for (int i = 0; i < n; i++)
				for (int j = 0; j < n; j++)
					G2[i][j] = G[i][j];
			int[][] flow = MaxFlow(G2);
			long endTime = System.currentTimeMillis();
			totalTimeSeconds += (endTime-startTime)/1000.0;

			if (flow == null || !verifyFlow(G,flow)){
				System.out.printf("Graph %d: Flow is invalid.\n",graphNum);
			}else{
				int value = totalFlowValue(flow);
				System.out.printf("Graph %d: Max Flow Value is %d\n",graphNum,value);
			}

		}
		graphNum--;
		System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
	}
}
