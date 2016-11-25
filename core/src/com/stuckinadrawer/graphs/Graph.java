package com.stuckinadrawer.graphs;

import com.stuckinadrawer.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class contains an implementation for a directed graph structure
 */
public class Graph implements Serializable{

    private HashSet<ArrayList<Vertex>> edges;
    private HashSet<Vertex> vertices;

    public Graph(HashSet<ArrayList<Vertex>> edges, HashSet<Vertex> vertices){
        this.vertices = new HashSet<Vertex>();
        for(Vertex vertex: vertices){
            addVertex(vertex);
        }
        this.edges = new HashSet<ArrayList<Vertex>>();
        for(ArrayList<Vertex> edge: edges){
            addEdge(edge);
        }

    }

    public Graph(HashSet<ArrayList<Vertex>> edges){
        this(edges, new HashSet<Vertex>());
    }

    public Graph(){
        this(new HashSet<ArrayList<Vertex>>(), new HashSet<Vertex>());
    }

    public void addEdge(Vertex v1, Vertex v2){
        ArrayList<Vertex> edge = new ArrayList<Vertex>();
        edge.add(v1);
        edge.add(v2);
        addEdge(edge);
    }

    public void addEdge(ArrayList<Vertex> edge){
        for(Vertex vertex: edge){
            vertices.add(vertex);
        }
        this.edges.add(edge);
    }

    public void addVertex(Vertex vertex){
        this.vertices.add(vertex);
    }

    public void addVertices(HashSet<Vertex> vertices){
        for(Vertex vertex: vertices){
            addVertex(vertex);
        }
    }

    public HashSet<Vertex> getVertices(){
        return this.vertices;
    }

    public HashSet<ArrayList<Vertex>> getEdges(){
        return this.edges;
    }

    public HashSet<ArrayList<Vertex>> getIncomingEdges(Vertex vertex){
        HashSet<ArrayList<Vertex>> result = new HashSet<ArrayList<Vertex>>();
        for(ArrayList<Vertex> edge: this.edges){
            if(edge.get(1).equals(vertex)){
                result.add(edge);
            }
        }
        return result;
    }
    public HashSet<ArrayList<Vertex>> getOutgoingEdges(Vertex vertex){
        HashSet<ArrayList<Vertex>> result = new HashSet<ArrayList<Vertex>>();
        for(ArrayList<Vertex> edge: this.edges){
            if(edge.get(0).equals(vertex)){
                result.add(edge);
            }
        }
        return result;
    }

    public HashSet<ArrayList<Vertex>> getIncidentEdges(Vertex vertex){
        HashSet<ArrayList<Vertex>> result = new HashSet<ArrayList<Vertex>>();
        result.addAll(getIncomingEdges(vertex));
        result.addAll(getOutgoingEdges(vertex));
        return result;
    }

    public void deleteEdge(ArrayList<Vertex> edge){
        this.edges.remove(edge);
    }

    public void deleteEdge(Vertex v1, Vertex v2){
        ArrayList<Vertex> edge = new ArrayList<Vertex>();
        edge.add(v1);
        edge.add(v2);
        deleteEdge(edge);
    }


    public void deleteVertex(Vertex vertex){
        for(ArrayList<Vertex> edge: getIncidentEdges(vertex)){
            this.edges.remove(edge);
        }
        this.vertices.remove(vertex);
    }

    public HashSet<Vertex> getNeighbors(Vertex vertex){
        HashSet<Vertex> result = new HashSet<Vertex>();
        result.addAll(getIncomingNeighbors(vertex));
        result.addAll(getOutgoingNeighbors(vertex));
        return result;
    }

    public HashSet<Vertex> getIncomingNeighbors(Vertex vertex){
        HashSet<Vertex> result = new HashSet<Vertex>();
        for(Vertex v: this.vertices){
            if(hasEdge(v, vertex)){
                result.add(v);
            }
        }

        return result;
    }

    public HashSet<Vertex> getOutgoingNeighbors(Vertex vertex){
        HashSet<Vertex> result = new HashSet<Vertex>();
        for(Vertex v: this.vertices){
            if(hasEdge(vertex, v)){
                result.add(v);
            }
        }

        return result;
    }

    public int getDegree(Vertex vertex){
        return getNeighbors(vertex).size();
    }

    public int inDegree(Vertex vertex){
        return getIncomingNeighbors(vertex).size();
    }
    public int outDegree(Vertex vertex){
        return getOutgoingNeighbors(vertex).size();
    }

    public boolean hasVertex(Vertex vertex){
        return this.vertices.contains(vertex);
    }

    public boolean hasEdge(ArrayList<Vertex> edge){
        return this.edges.contains(edge);
    }

    public boolean hasEdge(Vertex v1, Vertex v2){
        ArrayList<Vertex> edge = new ArrayList<Vertex>();
        edge.add(v1);
        edge.add(v2);
        return hasEdge(edge);
    }

    public void setRandomVertexPosition(int width, int height){
        for(Vertex vertex: vertices){
            if(vertex.notPositioned){
                vertex.setPosition(Utils.random(width)-width/2, Utils.random(height)-height/2);
            }

        }
    }

    @Override
    public String toString(){
        String result = "";
        result += "Vertices: ";
        for(Vertex v: vertices){
            result += v.toString()+" ";
        }
        result += "\n Edges: ";
        for(ArrayList<Vertex> edge: edges){
            result+=" [";
            for(Vertex v: edge){
                result+=v.getId()+":"+v.getType();
            }
            result+="] ";
        }
        return result;
    }

    public HashSet<Vertex> getVerticesByType(String type){
        HashSet<Vertex> result = new HashSet<Vertex>();
        for(Vertex v: vertices){
            if(v.getType().equals(type)) result.add(v);
        }
        return result;
    }

    public int getHighestVertexId(){
        int result = 0;
        for(Vertex v : vertices){
            if(v.getId() > result){
                result = v.getId();
            }
        }
        return result;
    }

    public void setVertexPosToZero(){
        for(Vertex vertex: vertices){
                vertex.setPosition(0, 0);
        }
    }

    public void setVertexPosition(int x, int y){
        for(Vertex vertex: vertices){
            if(vertex.notPositioned){
                vertex.setPosition(Utils.random(x-4, x+4), Utils.random(y-4, y+4));
            }

        }
    }

}
