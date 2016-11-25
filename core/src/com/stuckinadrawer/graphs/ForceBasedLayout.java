package com.stuckinadrawer.graphs;

public class ForceBasedLayout {

    private static final int REPULSION = 205000;
    private static final int SPRING_LENGTH = 80;
    private static final double STEP_SIZE = 0.0005;

    /**
     * This Class positions the Nodes of a Graph with a force based Layout
     * Spring-Based attractive forces based on Hooke's Law are applied to pull directly connected Nodes together,
     * while a repulsive force like those of electrically charged particles based on Coulomb's Law are used to separate all Nodes
     */

    public void step(Graph graph){

        //calculate force on all vertices
        for(Vertex vertex1: graph.getVertices()){
            vertex1.forceX = 0;
            vertex1.forceY = 0;
            for(Vertex vertex2: graph.getVertices()){
                if(!vertex2.equals(vertex1)){
                    double deltaX = vertex2.getX() - vertex1.getX();
                    double deltaY = vertex2.getY() - vertex1.getY();
                    double delta2 = deltaX * deltaX + deltaY * deltaY;

                    //Add some jitter if distance² is very small
                    if(delta2 < 0.1){
                    //    System.out.println("Too Close! Jitter!");
                        deltaX = 1 * Math.random() + 0.1;
                        deltaY = 1 * Math.random() + 0.1;
                        delta2 = deltaX * deltaX + deltaY * deltaY;
                    }

                    //Coulomb's Law -- repulsion varies inversely with square of distance
                    if (delta2 < 100*100){
                        vertex1.forceX -= (REPULSION / delta2) * deltaX;
                        vertex1.forceY -= (REPULSION / delta2) * deltaY;
                    }


                    //Hooke's Law -- spring force along edges
                    if(graph.hasEdge(vertex1, vertex2) || graph.hasEdge(vertex2, vertex1)){
                        double distance = Math.sqrt(delta2);
                        vertex1.forceX += (distance - SPRING_LENGTH) * deltaX;
                        vertex1.forceY += (distance - SPRING_LENGTH) * deltaY;
                    }


                }
            }

        }

        //apply calculated forces
        for(Vertex vertex: graph.getVertices()){
            vertex.applyForce(STEP_SIZE);

        }

    }



    public void layout(Graph graph){
        for(int i = 0; i < 1000; i++){
            step(graph);
        }
    }


}
