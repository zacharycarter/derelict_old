package com.stuckinadrawer;

import com.google.gson.Gson;
import com.stuckinadrawer.graphs.Grammar;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class FileReader {

    public ArrayList<String> getNonTerminals(){
        File dir = new File(".");
        try {
            return loadAlphabetFile(new File(dir.getCanonicalPath() + File.separator + "nonTerminals.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getTerminals(){
        File dir = new File(".");
        try {
            return loadAlphabetFile(new File(dir.getCanonicalPath() + File.separator + "terminals.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> loadAlphabetFile(File file){
        ArrayList<String> strings = new ArrayList<String>();

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line;
        try {
            while((line = br.readLine()) != null){
                strings.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(strings);


        return strings;
    }

    public Grammar loadGrammar(String filename) throws IOException {

        File dir = new File(".");
        File file = new File(dir.getCanonicalPath() + File.separator + filename);

        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        //convert the json string back to object
        Gson gson = new Gson();
        Grammar grammar = gson.fromJson(br, Grammar.class);
        br.close();
        return grammar;

    }

    public void saveGrammar(Grammar grammar, String filename){
        Gson gson = new Gson();
        String grammarString  = gson.toJson(grammar);
        try {
            PrintWriter out = new PrintWriter(filename);
            out.print(grammarString);
            out.close();
            System.out.println("Grammar saved");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }


}
