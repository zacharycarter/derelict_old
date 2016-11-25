package com.stuckinadrawer.graphs;

import com.stuckinadrawer.FileReader;
import com.stuckinadrawer.Utils;

import java.util.ArrayList;

public class VertexFactory {

    private static VertexFactory instance = new VertexFactory();

    ArrayList<String> terminals;
    ArrayList<String> nonTerminals;

    private static int currentMaxId = 0;

    private VertexFactory(){
        FileReader fr = new FileReader();
        terminals = fr.getTerminals();
        nonTerminals = fr.getNonTerminals();
    }

    public static VertexFactory getInstance(){
        return instance;
    }

    public static void setCurrentMaxId(int id){
        currentMaxId = id;
    }

    public static int getCurrentMaxId() {
        return currentMaxId;
    }

    public Vertex createNewVertex(){
        int index = Utils.random(terminals.size()-1);
        String label = terminals.get(index);
        return new Vertex(currentMaxId++, label);
    }

    public ArrayList<String> getTerminals() {
        return terminals;
    }

    public ArrayList<String> getNonTerminals() {
        return nonTerminals;
    }

    public ArrayList<String> getAllSymbols(){
        ArrayList<String> all = new ArrayList<String>();
        all.add("*");
        all.add("-");
        all.addAll(nonTerminals);
        all.add("-");
        all.addAll(terminals);
        return all;

    }

    public Vertex createNewVertex(String label){
        return new Vertex(currentMaxId++, label);
    }
}
