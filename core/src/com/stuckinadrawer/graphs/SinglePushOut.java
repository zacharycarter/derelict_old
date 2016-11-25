package com.stuckinadrawer.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SinglePushOut {

    private final VertexFactory vertexFactory;

    public SinglePushOut(){
        this.vertexFactory = VertexFactory.getInstance();
    }

    public void applyProduction(Production p, Graph g, HashMap<Vertex, Vertex> assignments){
        System.out.println("\n ###APPLYING PRODUCTION### ");

        //entferne alle nodes aus Host die in Plinks aber nicht in Prechts enthalten sind
        for(Vertex v: p.getLeft().getVertices()){
            System.out.println("check if "+v.toString()+" needs to be deleted");
            String type = "error";
            int morph = v.getMorphism();
            //morph is -1 falls nicht verwendet, d.h. es kommt auf keinen fall in prechts vor
            Vertex isInPrechts = null;
            if(morph >= 0){
                for(Vertex vrechts : p.getRight().getVertices()){
                    if(morph == vrechts.getMorphism()){
                        isInPrechts = vrechts;
                        type = vrechts.getType();
                        break;
                    }

                }
            }
            System.out.println("deletion of "+v.toString()+" "+ (isInPrechts==null));
            Vertex vertexInHost = assignments.get(v);
            if(isInPrechts == null){
                g.deleteVertex(vertexInHost);
            }else{

                vertexInHost.marked = true;

                vertexInHost.setType(type);

                assignments.remove(v);
                assignments.put(isInPrechts, vertexInHost);

            }

        }

        // entferne alle kanten aus gematchtem graphen
        HashSet<ArrayList<Vertex>> edgesToDelete = new HashSet<ArrayList<Vertex>>();
        for(ArrayList<Vertex> edge: g.getEdges()){
            if(assignments.containsValue(edge.get(0)) && assignments.containsValue(edge.get(1))){
                edgesToDelete.add(edge);
            }
        }

        for(ArrayList<Vertex> edge: edgesToDelete){
            g.deleteEdge(edge);
            String result = "";
            result+=" [";
            for(Vertex v: edge){
                result+=v.getId()+":"+v.getType();
            }
            result+="] ";
            System.out.println("DELETED EDGE: "+result);

        }



        // Füge alle nodes aus Prechts hinzu, die in Host noch nicht enthalten sind

        for(Vertex vRechts : p.getRight().getVertices()){
            int morphRechts = vRechts.getMorphism();
            boolean isInPlinks = false;
            if(morphRechts >= 0){
                for(Vertex vLinks : p.getLeft().getVertices()){
                    if(vLinks.getMorphism() == morphRechts){
                        isInPlinks = true;
                        break;

                    }
                }
            }
            System.out.println(vRechts.toString()+" is In links? "+isInPlinks);

            if(!isInPlinks){
                //add to host
                Vertex newVertex = vertexFactory.createNewVertex(vRechts.getType());
                g.addVertex(newVertex);
                System.out.println("New Vertex: "+newVertex.toString());
                //add to assignments to check for missing edges in Host later
                assignments.put(vRechts, newVertex);
                newVertex.marked = true;

            }
        }

        // füge alle Kanten aus Prechts hinzu, die in Host noch nicht enthalten sind
        for(ArrayList<Vertex> edge: p.getRight().getEdges()){
            Vertex v1Prod = edge.get(0);
            Vertex v2Prod = edge.get(1);

            System.out.println("Checking Edge: [" +v1Prod.toString()+", "+v2Prod.toString()+"] aus PRechts");

            Vertex v1Graph = assignments.get(v1Prod);
            Vertex v2Graph = assignments.get(v2Prod);

            System.out.println("Checking Edge: [" +v1Graph.toString()+", "+v2Graph.toString()+"] aus HOST");

            if(!g.hasEdge(v1Graph, v2Graph)){
                //wenn es in g noch keine edge zwischen den beiden gibt, füg hinzu
                System.out.println("added missing edge");
                g.addEdge(v1Graph, v2Graph);
            }

        }


        System.out.println(g.toString());

    }

}
