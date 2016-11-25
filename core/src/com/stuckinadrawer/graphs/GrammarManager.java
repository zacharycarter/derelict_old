package com.stuckinadrawer.graphs;

import com.stuckinadrawer.ObjectCloner;
import com.stuckinadrawer.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GrammarManager {

    Graph currentGraph;
    Grammar grammar;
    SinglePushOut pushOut;

    public GrammarManager(){
        pushOut = new SinglePushOut();
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
        resetCurrentGraph();
    }

    public Graph getCurrentGraph() {
        return currentGraph;
    }

    public void resetCurrentGraph(){
        try {
            currentGraph = (Graph) ObjectCloner.deepCopy(grammar.getStartingGraph());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean applyRandomProduction(){
        Production selectedProduction = getRandomProduction();
        if(selectedProduction == null){
            return false;
        }
        applyProduction(selectedProduction);
        return true;
    }

    public void applyProduction(Production selectedProduction){
        HashMap<Vertex, Vertex> morphism = findProductionInGraph(selectedProduction);
        int x, y, num, xVal, yVal;
        xVal = 0;
        yVal = 0;
        num = 0;
        for (Map.Entry pair : morphism.entrySet()) {
            Vertex v = (Vertex) pair.getValue();
            xVal += v.getX();
            yVal += v.getY();
            num++;
        }
        x = xVal/num;
        y = yVal/num;

        Production productionWithoutWildcard = createNewProductionWithoutWildcard(selectedProduction, morphism);
        pushOut.applyProduction(productionWithoutWildcard, currentGraph, morphism);
        currentGraph.setVertexPosition(x, y);
    }

    public void applyAllProductions(){
        boolean applyingProductions = true;
        while(applyingProductions){
            applyingProductions = applyRandomProduction();
        }
    }

    private Production getRandomProduction() {
        System.out.println("looking for random production");
        ArrayList<Production> possibleProductions = new ArrayList<Production>();
        for(Production p: grammar.getProductions()){
            if(findProductionInGraph(p)!=null){
                possibleProductions.add(p);
            }
        }
        if(!possibleProductions.isEmpty()){
            int rand = Utils.random(possibleProductions.size() - 1);
            System.out.println("found "+possibleProductions.size()+"possible prods; chose nr"+rand);
            return possibleProductions.get(rand);
        }
        return null;

    }
    public HashMap<Vertex, Vertex> findProductionInGraph(Production selectedProduction) {
        Graph subGraph = selectedProduction.getLeft();
        SubGraphIsomorphism finder = new SubGraphIsomorphism();
        return finder.findIsomorphism(currentGraph, subGraph);
    }


    private Production createNewProductionWithoutWildcard(Production selectedProduction, HashMap<Vertex, Vertex> morphism) {
        System.out.println("\n ###WILDCARDS### ");
        Production newProduction;
        try {
            newProduction = (Production) ObjectCloner.deepCopy(selectedProduction);
        } catch (Exception e) {
            e.printStackTrace();
            return selectedProduction;
        }

        //remove wildcards from left side
        for(Vertex vertexInLeftSide: newProduction.getLeft().getVertices()){
            if(vertexInLeftSide.getType().equals("*")){
                Vertex assignedVertexInHost = morphism.get(vertexInLeftSide);
                morphism.remove(vertexInLeftSide);
                vertexInLeftSide.setType(assignedVertexInHost.getType());
                morphism.put(vertexInLeftSide, assignedVertexInHost);
                System.out.println("WILDCARD REMOVED FROM: "+vertexInLeftSide.getDescription());
            }
        }

        //remove wildcards from right side
        for(Vertex vertexInRightSide: newProduction.getRight().getVertices()){
            if(vertexInRightSide.getType().equals("*")){
                int morphRight = vertexInRightSide.getMorphism();
                for(Vertex vertexInLeftSide: newProduction.getLeft().getVertices()){
                    if(vertexInLeftSide.getMorphism() == morphRight){
                        System.out.println("WIlDCARD REMOVED FROM "+vertexInRightSide.getDescription());
                        System.out.println("set to: "+vertexInLeftSide.getType());
                        vertexInRightSide.setType(vertexInLeftSide.getType());
                    }
                }


            }
        }

        return newProduction;
    }
}
