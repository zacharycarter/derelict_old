package com.stuckinadrawer;

import com.stuckinadrawer.graphs.*;

import java.util.ArrayList;
import java.util.HashSet;

public class GraphFactory {

    VertexFactory factory = VertexFactory.getInstance();

    public Graph createRandomGraph(){
        int amountOfVertices = Utils.random(5, 10);
        HashSet<Vertex> vertices = new HashSet<Vertex>();
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
        for(int i = 0; i < amountOfVertices; i++){
            Vertex v = factory.createNewVertex();
            vertices.add(v);
            vertexList.add(v);
        }
        HashSet<ArrayList<Vertex>> edges = new HashSet<ArrayList<Vertex>>();
        int amountOfEdges = Utils.random(amountOfVertices, amountOfVertices+3);
        for(int i = 0; i < amountOfEdges; i++){
            ArrayList<Vertex> edge = new ArrayList<Vertex>();
            Vertex v1 = getRandomVertexFromList(vertexList);
            Vertex v2 = getRandomVertexFromList(vertexList);
            while(v1.equals(v2)){
                v2 = getRandomVertexFromList(vertexList);
            }
            edge.add(v1);
            edge.add(v2);
            edges.add(edge);
        }

        return new Graph(edges, vertices);
    }

    public Graph createStartGraph(){
        Vertex v1 = factory.createNewVertex("ST");
        Graph graph = new Graph();
        graph.addVertex(v1);
        return graph;
    }

    public Vertex getRandomVertexFromList(ArrayList<Vertex> list){
        int index = Utils.random(list.size()-1);
        return list.get(index);
    }


}
