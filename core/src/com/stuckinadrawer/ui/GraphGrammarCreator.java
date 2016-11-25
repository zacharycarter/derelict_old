package com.stuckinadrawer.ui;

import com.stuckinadrawer.FileReader;
import com.stuckinadrawer.graphs.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GraphGrammarCreator extends JFrame {

    private Font bigFont;

    private GrammarManager grammarManager;
    JList<String> productionList;

    SimpleGraphDisplayPanel graphPanelLeft;
    SimpleGraphDisplayPanel graphPanelRight;

    JPanel startGraphPanel;

    public static final String FILE_NAME = "grammar1.txt";

    public GraphGrammarCreator(){
        grammarManager = new GrammarManager();
        setLookAndFeel();
        bigFont = new Font("big", Font.BOLD, 16);
        grammarManager.setGrammar(loadGrammar(FILE_NAME));
        VertexFactory.setCurrentMaxId(grammarManager.getGrammar().getCurrentMaxVertexId());
        initUI();
    }



    private Grammar loadGrammar(String fileName){
        FileReader fr = new FileReader();
        try {
            return fr.loadGrammar(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Grammar();
        }
    }

    private void saveGrammar(String fileName){
        grammarManager.getGrammar().setCurrentMaxVertexId(VertexFactory.getCurrentMaxId());

        FileReader fr = new FileReader();
        fr.saveGrammar(grammarManager.getGrammar(), "./"+fileName);
    }

    private void initUI() {
        setTitle("Graph Grammar Creator");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.PAGE_AXIS));
        JPanel productionsPanel = createProductionsPanel();


        startGraphPanel = createStartGraphPanel();
        center.add(startGraphPanel);
        center.add(Box.createRigidArea(new Dimension(5, 5)));
        center.add(productionsPanel);

        add(center);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


    }

    private JPanel createProductionsPanel() {
        JPanel productionsPanel = new JPanel();
        productionsPanel.setLayout(new BoxLayout(productionsPanel, BoxLayout.PAGE_AXIS));

        // Title
        JLabel label = new JLabel("Productions");
        label.setFont(bigFont);

        // Actual Productions List+Display
        JPanel insidePanel = new JPanel();

        productionList = new JList<String>();
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for(Production p: grammarManager.getGrammar().getProductions()){
            listModel.addElement(p.getName());
        }
        productionList.setModel(listModel);
        JScrollPane scrollPane = new JScrollPane(productionList);
        insidePanel.add(scrollPane);


        graphPanelLeft = new SimpleGraphDisplayPanel(400, 400);
        graphPanelRight = new SimpleGraphDisplayPanel(400, 400);
        insidePanel.add(graphPanelLeft);
        insidePanel.add(graphPanelRight);

        productionList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Production selectedProduction = getSelectedProductionFromList();
                if(selectedProduction!=null){
                    graphPanelLeft.setGraphToDisplay(selectedProduction.getLeft());
                    graphPanelRight.setGraphToDisplay(selectedProduction.getRight());
                }
            }
        });



        // Buttons
        JPanel buttonsPanel = new JPanel();

        JButton btn_new = new JButton("New");
        btn_new.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EditProductionWindow window = new EditProductionWindow(GraphGrammarCreator.this);
                window.startUpdating();
                window.setVisible(true);

            }
        });
        JButton btn_edit = new JButton("Edit");
        btn_edit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Production selectedProduction = getSelectedProductionFromList();
                if(selectedProduction!= null){
                    EditProductionWindow window = new EditProductionWindow(selectedProduction, GraphGrammarCreator.this);
                    window.startUpdating();
                    window.setVisible(true);
                }
            }
        });
        JButton btn_delete = new JButton("Delete");
        btn_delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Production selectedProduction = getSelectedProductionFromList();
                if(selectedProduction!=null){
                    removeProduction(selectedProduction);
                }
            }
        });

        JButton btn_check = new JButton("FindInGraph");
        btn_check.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Production selectedProduction = getSelectedProductionFromList();
                if(selectedProduction!=null){
                    HashMap<Vertex, Vertex> match = grammarManager.findProductionInGraph(selectedProduction);
                    for (Map.Entry pair : match.entrySet()) {
                        Vertex v = (Vertex) pair.getValue();
                        v.marked = true;
                    }
                    startGraphPanel.repaint();
                }
            }
        });

        JButton btn_apply = new JButton("Apply");
        btn_apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Production selectedProduction = getSelectedProductionFromList();
                if(selectedProduction!=null){

                    grammarManager.applyProduction(selectedProduction);
                    Graph startGraph = grammarManager.getCurrentGraph();
                    startGraph.setRandomVertexPosition(800, 400);
                    new ForceBasedLayout().layout(startGraph);
                    startGraphPanel.repaint();
                }

                System.out.println("\nDONE\n\n");
            }
        });

        JButton btn_apply_random = new JButton("Apply Random");
        btn_apply_random.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grammarManager.applyRandomProduction();
                Graph startGraph = grammarManager.getCurrentGraph();

                startGraph.setRandomVertexPosition(800, 400);
                new ForceBasedLayout().layout(startGraph);
                startGraphPanel.repaint();
            }
        });

        JButton btn_redraw = new JButton("Redraw");
        btn_redraw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Graph startGraph = grammarManager.getCurrentGraph();
                startGraph.setRandomVertexPosition(800, 400);
                new ForceBasedLayout().layout(startGraph);
                for(Vertex v:startGraph.getVertices())    {
                    v.marked = false;
                }
                startGraphPanel.repaint();
            }
        });

        buttonsPanel.add(btn_new);
        buttonsPanel.add(btn_edit);
        buttonsPanel.add(btn_delete);
        buttonsPanel.add(btn_check);
        buttonsPanel.add(btn_apply);
        buttonsPanel.add(btn_apply_random);
        buttonsPanel.add(btn_redraw);


        productionsPanel.add(label);
        productionsPanel.add(insidePanel);
        productionsPanel.add(buttonsPanel);

        JButton btn_saveGrammar = new JButton("Save Grammar");
        btn_saveGrammar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveGrammar(FILE_NAME);
            }
        });
        productionsPanel.add(btn_saveGrammar);
        return productionsPanel;
    }





    private JPanel createStartGraphPanel(){
        JPanel startGraphPanel = new JPanel();

        // Title
        JLabel label = new JLabel("StartGraph");
        label.setFont(bigFont);

        startGraphPanel.add(label);
        Graph startGraph = grammarManager.getCurrentGraph();
        new ForceBasedLayout().layout(startGraph);
        SimpleGraphDisplayPanel graphDisplayPanel = new SimpleGraphDisplayPanel(startGraph, 800, 400);
        startGraphPanel.add(graphDisplayPanel);
        return startGraphPanel;
    }

    public Production getSelectedProductionFromList(){
        if(!productionList.isSelectionEmpty()){
            String name = productionList.getSelectedValue();
            for(Production p: grammarManager.getGrammar().getProductions()){
                if(name.equals(p.getName())){
                    return p;
                }
            }

        }
        return null;
    }

    private void setLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to cross-platform
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addProduction(Production production){
        grammarManager.getGrammar().addProduction(production);
        updateListModel();

    }

    public void removeProduction(Production production){
        grammarManager.getGrammar().removeProduction(production);
        updateListModel();
    }

    private void updateListModel(){
        graphPanelRight.clear();
        graphPanelLeft.clear();
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for(Production p: grammarManager.getGrammar().getProductions()){
            listModel.addElement(p.getName());
        }
        productionList.setModel(listModel);
    }

    public static void main(String[] arg){
        new GraphGrammarCreator();
    }

}
