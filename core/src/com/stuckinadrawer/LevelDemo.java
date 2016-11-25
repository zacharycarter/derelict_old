package com.stuckinadrawer;

import com.stuckinadrawer.graphs.*;
import com.stuckinadrawer.ui.SimpleGraphDisplayPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

public class LevelDemo extends JFrame{

    GrammarManager grammarManager;

    Graph levelGraph;

    ForceBasedLayout layouter = new ForceBasedLayout();

    MyPanel panel;
    boolean keepUpdating = false;

    public LevelDemo(){
        grammarManager = new GrammarManager();
        grammarManager.setGrammar(loadGrammar());
        levelGraph = grammarManager.getCurrentGraph();
        levelGraph.setVertexPosToZero();
        initUI();
    }

    private Grammar loadGrammar(){
        FileReader fr = new FileReader();
        try {
            return fr.loadGrammar("grammar1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Grammar();
    }

    private void initUI() {
        setTitle("LevelDemo");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);
        setVisible(true);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        panel = new MyPanel(levelGraph, getSize().width, getSize().height);
        add(panel, BorderLayout.CENTER);

        createKeyListener();
    }

    private void createKeyListener() {
        this.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e.getKeyCode());
                switch(e.getKeyCode()){
                    case 32:
                        grammarManager.resetCurrentGraph();
                        levelGraph = grammarManager.getCurrentGraph();
                        panel.setGraphToDisplay(levelGraph);
                        levelGraph.setVertexPosToZero();
                        grammarManager.applyAllProductions();
                        clearMarkings();
                        //levelGraph.setRandomVertexPosition(1000, 1000);
                        layouter.layout(levelGraph);
                        createGroups();
                        panel.repaint();
                        break;
                    case 80:
                        //apply 1 prod
                        clearMarkings();
                        if(grammarManager.applyRandomProduction()){
                           // levelGraph.setRandomVertexPosition(1000, 1000);
                            layouter.layout(levelGraph);
                            panel.repaint();
                        }

                        break;
                    case 76:
                        //layout
                        if(!keepUpdating){
                            keepUpdating = true;
                            UpdateRunnable updateRunnable = new UpdateRunnable();
                            new Thread(updateRunnable).start();
                        }else{
                            keepUpdating = false;
                        }
                        break;
                    case 27:
                        //quit
                        System.exit(0);
                        break;
                    case 73:
                        for(Vertex v: levelGraph.getVertices()){
                            System.out.println(v.getDescription()+": "+v.getX() + " "+v.getY());
                        }
                        break;
                    case 83:
                        //Startgraph
                        grammarManager.resetCurrentGraph();
                        levelGraph = grammarManager.getCurrentGraph();
                        panel.setGraphToDisplay(levelGraph);
                        levelGraph.setVertexPosToZero();
                        break;
                    case 65:
                        //all
                        grammarManager.applyAllProductions();
                        clearMarkings();
                       // levelGraph.setRandomVertexPosition(1000, 1000);
                        layouter.layout(levelGraph);
                        panel.repaint();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

    }

    private void createGroups() {
        //uses morphism field to assign groups;
        //same group = in same room

        int groupID = 1;

        Stack<Vertex> nextVertices = new Stack<Vertex>();

        Vertex currentVertex = findStartVertex();
        currentVertex.setMorphism(groupID);
        groupID++;

        nextVertices.addAll(levelGraph.getOutgoingNeighbors(currentVertex));
        System.out.println("### GROUPING ###");
        int sameID = 0;
        while(!nextVertices.empty()){
            currentVertex = nextVertices.pop();
            currentVertex.setMorphism(groupID);
            System.out.println("\n"+currentVertex.getDescription() + " was assigned");
            HashSet<Vertex> outgoingNeighbours = levelGraph.getOutgoingNeighbors(currentVertex);
            System.out.println("has "+ outgoingNeighbours.size() + " outgoing neighbours");
            if(outgoingNeighbours.size() > 1){
                System.out.println("added all, new id");
                groupID++;
                sameID = 0;
                for(Vertex v: outgoingNeighbours){
                    nextVertices.push(v);
                }
            }else if (outgoingNeighbours.size() == 1){
                System.out.println("added one");
                for(Vertex v: outgoingNeighbours){
                    nextVertices.push(v);
                    if(v.getType().equals("lock") || v.getType().equals("exit") || sameID>=2){
                        groupID++;
                        sameID = 0;
                    }else{
                        sameID++;
                    }
                }
            }else{
                //no outgoing neighbours
                System.out.println("added none, new id");
                groupID++;
                sameID = 0;
            }


        }
        System.out.println("end");

    }



    private Vertex findStartVertex(){
        // get starting vertex
        for(Vertex v: levelGraph.getVertices()){
            if(v.getType().equals("start")){
                return v;
            }
        }
        return null;
    }

    private void clearMarkings() {
        for(Vertex v: levelGraph.getVertices()){
            v.marked = false;
        }

    }

    private class UpdateRunnable implements Runnable{

        @Override
        public void run() {
            while (keepUpdating){
                try {
                    Thread.sleep(1000/60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                layouter.step(levelGraph);
                panel.repaint();
            }
        }
    }

    private class MyPanel extends SimpleGraphDisplayPanel {

        private Point dragging = null;
        private Vertex draggedVertex = null;

        public MyPanel(Graph graph, int width, int height) {
            super(graph, width, height);
            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int mouseX = e.getX() - offsetX;
                    int mouseY = e.getY() - offsetY;
                    Vertex v = getVertexOnPosition(mouseX, mouseY);
                    if (v != null) {
                        System.out.println("Clicked on Vertex: " + v.getDescription());
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    draggedVertex = getVertexOnPosition(e.getX() - offsetX, e.getY() - offsetY);
                    dragging = new Point(e.getX(), e.getY());
                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
            this.addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (draggedVertex != null) {
                        int deltaX = e.getX() - dragging.getX();
                        int deltaY = e.getY() - dragging.getY();

                        draggedVertex.move(deltaX, deltaY);

                        dragging = new Point(e.getX(), e.getY());
                        repaint();
                    }

                }

                @Override
                public void mouseMoved(MouseEvent e) {

                }
            });
        }
    }

    public static void main(String[] arg){
        new LevelDemo();
    }
}
