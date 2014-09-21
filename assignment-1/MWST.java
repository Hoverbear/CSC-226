/* MWST.java
   CSC 226 - Fall 2014
   Assignment 1 - Minimum Weight Spanning Tree Template

   This template includes some testing code to help verify the implementation.
   To interactively provide test inputs, run the program with
   java MWST

   To conveniently test the algorithm with a large input, create a text file
   containing one or more test graphs (in the format described below) and run
   the program with
   java MWST file.txt
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
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;

class Edge implements Comparable<Edge> {
  public int weight;
  public Node v; // The Source
  public Node w; // The Destination.

  public Edge(Node src, Node dest, int x) {
    weight = x;
    v = src;
    w = dest;
  }

  public boolean has(Node x) {
    if (this.v == x || this.w == x) {
      return true;
    } else {
      return false;
    }
  }

  public int compareTo(Edge e) {
    return Integer.compare(this.weight, e.weight);
  }
}

class Node {
  public int val;
  public ArrayList<Edge> edges;
  public UnionFind uf;

  public Node(int x) {
    val = x;
    edges = new ArrayList<Edge>();
  }
}

// Java sucks, so we can't just use a struct.
class UnionFind{
  public UnionFind parent; // This is a pointer, but Java sucks.
  public Node key; // Vertex
  public int rank;

  public UnionFind(Node x) {
    parent = this; // In Rust this should be an enum of {Self, Parent(x)}
    key = x;
    rank = 0;
  }

  // Java sucks, so we need to declare this as a static method to use it like `UnionFind.make_set(x)`
  public static UnionFind make_set(Node x) {
    return new UnionFind(x); // Java sucks, so we can't just have `make_set` as the constructor.
  }

  public UnionFind find() { // `this` is the implict first arguement, because Java sucks and we can't represent that.
    UnionFind current = this;
    if (current.parent == current) {
      return current;
    } else {
      return current.parent.find();
    }
  }
  
  public UnionFind find(UnionFind last) { // `this` is the implict first arguement, because Java sucks and we can't represent that.
    UnionFind current = this;
    last.parent = current.parent;
    if (current.parent == current) {
      return current;
    } else {
      return current.parent.find(current);
    }
  }

  public UnionFind union(UnionFind other) { // `this` is the other struct, but Java sucks, so we can't represent that.
    UnionFind bigger, smaller, x, y;
    // Should only union top nodes
    x = this.find(); 
    y = other.find(); 
    // Which should be attached to the other?
    if (x.rank > y.rank) {
      bigger = x;
      smaller = y;
    } else {
      smaller = y;
      bigger = x;
    }
    // Attach the smaller to the larger.
    // TODO: Rank addition.
    smaller.parent = bigger;
    return bigger;
  }
}

//Do not change the name of the MWST class
public class MWST{

  /* mwst(G)
     Given an adjacency matrix for graph G, return the total weight
     of all edges in a minimum weight spanning tree.

     If G[i][j] == 0, there is no edge between vertex i and vertex j
     If G[i][j] > 0, there is an edge between vertices i and j, and the
     value of G[i][j] gives the weight of the edge.
     No entries of G will be negative.
     */
  static int MWST(int[][] G){
    int numVerts = G.length;

    /* Find a minimum weight spanning tree by any method */
    /* (You may add extra functions if necessary) */

    /* ... Your code here ... */

    // Build the Node List, the Edge list, the UF data structure, and the numer of elems in tree.
    Node[] T = new Node[numVerts];
    UnionFind[] U = new UnionFind[numVerts];
    ArrayList<Edge> E = new ArrayList<Edge>();
    E.ensureCapacity(numVerts);
    int in_tree = 1; // One is already in the tree. We're done when this is same as numVerts.
    // The MST
    ArrayList<Edge> M = new ArrayList<Edge>(); // Resizable because we don't know how many there will be.
    M.ensureCapacity(numVerts); // In an ideal case this won't need to resize.
    // We're not given values for the nodes, so we make some.
    for (int i=0; i < numVerts; i++) {
      T[i] = new Node(i);
      U[i] = UnionFind.make_set(T[i]);
      T[i].uf = U[i];
    }
    // Create a set of edges.
    // If we combine the loops there are null errors galore.
    for (int i=0; i < numVerts-1; i++) { // Nothing to do on last item.
      for (int j=i+1; j < numVerts; j++) { // Start on the item after the diagonal.
        if (G[i][j] > 0) {
          // There is an edge.
          Edge e = new Edge(T[i], T[j], G[i][j]);
          E.add(e);
        }
      }  
    }
    Collections.sort(E);
    for (Edge e: E) {
      if (!e.v.uf.find().equals(e.w.uf.find())) {
        M.add(e);
        e.v.uf.union(e.w.uf);
        e.v.uf.union(e.v.uf);
      } else {
      }
    }

    /* Add the weight of each edge in the minimum weight spanning tree
       to totalWeight, which will store the total weight of the tree.
       */
    int totalWeight = 0;
    /* ... Your code here ... */
    for (Edge e : M) {
      totalWeight += e.weight;
    }

    return totalWeight;

  }


  /* main()
     Contains code to test the MWST function. You may modify the
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

      int totalWeight = MWST(G);
      long endTime = System.currentTimeMillis();
      totalTimeSeconds += (endTime-startTime)/1000.0;

      System.out.printf("Graph %d: Total weight is %d\n",graphNum,totalWeight);
    }
    graphNum--;
    System.out.printf("Processed %d graph%s.\nAverage Time (seconds): %.2f\n",graphNum,(graphNum != 1)?"s":"",(graphNum>0)?totalTimeSeconds/graphNum:0);
  }
}
