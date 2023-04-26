package application;

import java.util.*;

public class Graph<T> {
//creating an object of the Map class that stores the edges of the graph  
	HashMap<T, List<T>> map = new HashMap<>();

//the method adds a new vertex to the graph  
	public void addNewVertex(T s) {
		map.put(s, new LinkedList<T>());
	}

//the method adds an edge between source and destination   
	public void addNewEdge(T source, T destination, boolean bidirectional) {
//      
		if (!map.containsKey(source)) {
			addNewVertex(source);
			System.out.println("New source = " + source);
		}
		if (!map.containsKey(destination)) {
			addNewVertex(destination);
			System.out.println("New destination = " + destination);
		}
		map.get(source).add(destination);
		if (bidirectional == true) {
			map.get(destination).add(source);
		}
	}

//the method counts the number of vertices  
	public void countVertices() {
		System.out.println("Total number of vertices: " + map.keySet().size());
	}

//the method counts the number of edges  
	public void countEdges(boolean bidirection) {
//variable to store number of edges      
		int count = 0;
		for (T v : map.keySet()) {
			count = count + map.get(v).size();
		}
		if (bidirection == true) {
			count = count / 2;
		}
		System.out.println("Total number of edges: " + count);
	}

//checks a graph has vertex or not  
	public void containsVertex(T s) {
		if (map.containsKey(s)) {
			System.out.println("The graph contains " + s + " as a vertex.");
		} else {
			System.out.println("The graph does not contain " + s + " as a vertex.");
		}
	}

//checks a graph has edge or not  
//where s and d are the two parameters that represent source(vertex) and destination (vertex)  
	public void containsEdge(T s, T d) {
		if (map.get(s).contains(d)) {
			System.out.println("The graph has an edge between " + s + " and " + d + ".");
		} else {
			System.out.println("There is no edge between " + s + " and " + d + ".");
		}
	}

//prints the adjacencyS list of each vertex  
//here we have overridden the toString() method of the StringBuilder class  
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
//foreach loop that iterates over the keys  
		for (T v : map.keySet()) {
			builder.append(v.toString() + ": ");
//foreach loop for getting the vertices  
			for (T w : map.get(v)) {
				builder.append(w.toString() + " ");
			}
			builder.append("\n");
		}
		return (builder.toString());
	}
}