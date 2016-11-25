package com.stuckinadrawer.graphs;

import com.stuckinadrawer.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SubGraphIsomorphism {


    private Graph subGraph;
    private Graph hostGraph;
    private ArrayList<Vertex> subGraphVertices;
    private ArrayList<Vertex> hostGraphVertices;

    // currentIsomorphism contains vertices in subGraph as keys and
    // assigned vertices in hostGraph as values
    private HashMap<Vertex, Vertex> currentIsomorphism;

    private ArrayList<HashMap<Vertex, Vertex>> isomorphisms = new ArrayList<HashMap<Vertex, Vertex>>();

    private int[][] M;

    public HashMap<Vertex, Vertex> findIsomorphism(Graph hostGraph, Graph subGraph){
        this.hostGraph = hostGraph;
        this.subGraph = subGraph;
        currentIsomorphism = new HashMap<Vertex, Vertex>();
        subGraphVertices = new ArrayList<Vertex>();
        subGraphVertices.addAll(subGraph.getVertices());
        hostGraphVertices = new ArrayList<Vertex>();
        hostGraphVertices.addAll(hostGraph.getVertices());

        M = new int[subGraphVertices.size()][hostGraphVertices.size()];
        System.out.println("\n ###SUBGRAPH MATCHING### ");
        initM();

        search();

        if (isomorphisms.isEmpty()) {
            System.out.println("NO MATCHING SUBGRAPH FOUND!");
            return null;
        } else {
            System.out.println("MATCHING SUBGRAPH FOUND! " + isomorphisms.size()+" different solutions");
            currentIsomorphism = isomorphisms.get(Utils.random(isomorphisms.size()-1));
            for(Map.Entry<Vertex, Vertex> entry : currentIsomorphism.entrySet()){
                System.out.println(entry.getKey().getDescription()+" matched to "+entry.getValue().getDescription());
            }
            return currentIsomorphism;
        }

    }

    private void initM() {
        for(int i = 0; i < M.length; i++){
            for(int j = 0; j < M[i].length; j++){
                M[i][j] = 1;
                Vertex subVertex = subGraphVertices.get(i);
                Vertex hostVertex = hostGraphVertices.get(j);
                if(subGraph.getDegree(subVertex) > hostGraph.getDegree(hostVertex)){
                    M[i][j] = 0;
                }
                if(!subVertex.getType().equals("*") && !subVertex.getType().equals(hostVertex.getType())){
                    M[i][j] = 0;
                }
                System.out.println(subVertex.getId()+":"+subVertex.getType()+" "+ hostVertex.getId()+":"+hostVertex.getType()+"   "+M[i][j]);
            }
        }
    }



    private boolean search(){
        int i = currentIsomorphism.size();

        //check edges
        for(ArrayList<Vertex> edge: subGraph.getEdges()){
            Vertex edgeFirst = edge.get(0);
            Vertex edgeSecond = edge.get(1);
            //if both vertices connected by edge are already assigned to host graph vertices
            if(currentIsomorphism.containsKey(edgeFirst) && currentIsomorphism.containsKey(edgeSecond)){
                //check if assigned vertices are connected too
                if(!hostGraph.hasEdge(currentIsomorphism.get(edgeFirst), currentIsomorphism.get(edgeSecond))){
                    return false;
                }
            }
        }

        //check if all vertices were assigned
        if(i == subGraphVertices.size()) {
            saveCurrentIsomorphism();
            return true;
        }

        for(int j = 0; j < M[i].length; j++){
            int canBeAssigned = M[i][j];
            if(canBeAssigned!=0){ //valid assignment

                Vertex vertexInSubGraph = subGraphVertices.get(i);
                Vertex vertexInHostGraph = hostGraphVertices.get(j);
                if(!currentIsomorphism.containsValue(vertexInHostGraph)){
                    currentIsomorphism.put(vertexInSubGraph, vertexInHostGraph);
                    search();
                    currentIsomorphism.remove(vertexInSubGraph);
                }
            }
        }

        return false;
    }

    private void saveCurrentIsomorphism() {
        HashMap<Vertex, Vertex> IsomorphismToSave = new HashMap<Vertex, Vertex>();
        for(Map.Entry<Vertex, Vertex> entry : currentIsomorphism.entrySet()){
            IsomorphismToSave.put(entry.getKey(), entry.getValue());
        }
        isomorphisms.add(IsomorphismToSave);


    }


}
