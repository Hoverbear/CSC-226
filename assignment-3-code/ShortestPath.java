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
import java.util.ArrayList;

class VertexHeapElement {
	public static final int INVALID_WEIGHT = Integer.MAX_VALUE;
	public int v, weight;
	public VertexHeapElement(int v){
		this.v = v;
		this.weight = INVALID_WEIGHT;
	}
}

class VertexHeap {
	//Array based heap using 1-based indexing.
	public VertexHeapElement[] heap;

	//indices[v] contains the index i such that heap[i].v == v
	public int[] indices;

	//Index of last valid element in heap (also number of elements since indexing is 1-based)
	private int heapEnd;

	//Initialize a vertex heap over n vertices, with
	//each vertex's weight initialized to infinity.
	public VertexHeap(int n){
		heap = new VertexHeapElement[n+1];
		indices = new int[n+1];
		heapEnd = n;
		for (int i = 0; i < n; i++){
			heap[i+1] = new VertexHeapElement(i);
			indices[i] = i+1;
		}
	}

	//Return the size of the heap
	public int size(){
		return heapEnd;
	}

	//Print the contents of the heap (on one line)
	public void printContents(String title){
		if (!title.equals(""))
			System.out.printf("%s: ",title);
		for (int i = 1; i <= heapEnd; i++){
			if (heap[i].weight != VertexHeapElement.INVALID_WEIGHT)
				System.out.printf("%d (%d), ",heap[i].v, heap[i].weight);
			else
				System.out.printf("%d (inf), ",heap[i].v);
		}
		System.out.printf("\n");
	}

	private void heapSwap(int i, int j) {
		VertexHeapElement element_i = heap[i];
		VertexHeapElement element_j = heap[j];
		heap[j] = element_i;
		heap[i] = element_j;
		indices[heap[j].v] = j;
		indices[heap[i].v] = i;
	}

	private void bubbleDown(int i) {
		// Children of node is 2*i, 2*i+1
		// 1 based indexing!
		if (2*i > heapEnd) {
			// If there are no children, we're done.
			return;
		}
		// If two children, choose smallest.
		// If one children assume left child is inf.
		int leftIndex = 2*i;
		int leftWeight = heap[leftIndex].weight;
		int rightIndex = 2*i+1;
		int rightWeight = VertexHeapElement.INVALID_WEIGHT;
		if (rightIndex <= heapEnd) {
			rightWeight = heap[rightIndex].weight;
		}
		int minChildIndex = leftIndex;
		if (leftWeight > rightWeight) {
			minChildIndex = rightIndex;
		}
		if (heap[i].weight < heap[minChildIndex].weight) {
			return; // No need to bubble down.
		}
		heapSwap(i, minChildIndex);
		bubbleDown(minChildIndex);
	}

	private void bubbleUp(int i) {
		// PARENT = floor i/2
		// 1 based indexing!
		if (Math.floor(i/2) < 1) {
			// This is the root, we're done.
			return;
		}
		// If two children, choose smallest.
		// If one children assume left child is inf.
		int parentIndex = (int) Math.floor(i/2);
		if (heap[i].weight > heap[parentIndex].weight) {
			return; // No need to bubble up.
		}
		heapSwap(i, parentIndex);
		bubbleUp(parentIndex);
	}

	//Remove and return the minimum element.
	public VertexHeapElement removeMin(){
		VertexHeapElement removeElement = heap[1]; // Minimum
		heapSwap(1, heapEnd);
		heapEnd--;
		bubbleDown(1);
		return removeElement;
	}

	//Return the current weight of a vertex.
	public int getWeight(int vertex){
		int heap_index = indices[vertex];
		VertexHeapElement e = heap[heap_index];
		return e.weight;
	}

	//Set the weight of vertex adjust_vertex to new_weight.
	public void adjust(int adjust_vertex, int new_weight){

		int adjust_index = indices[adjust_vertex];
		VertexHeapElement adjustElement = heap[adjust_index];
		if (new_weight > adjustElement.weight)
			throw new Error(); //This shouldn't happen in Dijkstra's algorithm
		adjustElement.weight = new_weight;
		//Bubble up adjustElement (not implemented)
		// PARENT = floor i/2
		bubbleUp(adjust_index);
	}
}

class Data {
	int index;
	boolean scanned;
	ArrayList<Integer> neighbors;

	Data(int index, ArrayList<Integer> neighbors) {
		this.index = index;
		this.neighbors = neighbors;
		this.scanned = false;
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
		VertexHeap queue = new VertexHeap(numVerts);
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
				queue.adjust(i, 0);
			}
			// Create the data node.
			data[i] = new Data(i, neighbors);
		}

		int min;
		do {
			// Get the minimum.
			min = queue.removeMin().v;
			// System.out.printf("Iterated i:%s w:%s\n", current.index, current.weight);
			// Is it the target? (Index i?)
			if (min == 1) {
				// Done.
				break;
			}
			// Mark scanned
			data[min].scanned = true;
			for (int i : data[min].neighbors) {
				if (data[i].scanned == false) {
					int candidate = queue.getWeight(min) + G[min][i];
					// System.out.printf("New weight is %d\n", candidate);
					if (queue.getWeight(i) > candidate) {
						// System.out.printf("Removing %d with weight %d\n", data[i].index, data[i].weight);
						// System.out.printf("Adding %d with weight %d\n", data[i].index, data[i].weight);
						queue.adjust(i, candidate);
					}
				}
			}
		} while (min != 1);

		/* ... End of code ... */
		return queue.getWeight(1);

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
