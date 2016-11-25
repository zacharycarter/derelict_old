package com.carterza.debug;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.carterza.generator.graph.ComparablePoint2i;
import com.carterza.generator.graph.Edge;
import com.carterza.generator.graph.Node;
import com.carterza.generator.spacecraft.Spacecraft;
import com.carterza.math.delaunay.graph.Corner;
import com.carterza.math.delaunay.graph.Region;
import com.carterza.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zachcarter on 11/23/16.
 */
public class Debug {

    private ShapeRenderer shapeRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private Box2DDebugRenderer box2DDebugRenderer;

    public Debug(OrthographicCamera camera) {
        this.shapeRenderer = new ShapeRenderer();
        this.shapeRenderer.setProjectionMatrix(camera.combined);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        font = new BitmapFont();
        box2DDebugRenderer = new Box2DDebugRenderer();
    }

    private void drawEdge(Edge edge, Color color) {
        ComparablePoint2i nodeFromCenter = edge.getOne().getId();
        ComparablePoint2i nodeToCenter = edge.getTwo().getId();
        shapeRenderer.setColor(color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(nodeFromCenter.x, nodeFromCenter.y, nodeToCenter.x, nodeToCenter.y);
        shapeRenderer.end();
        if(edge.getSymbol() != null) {
            batch.begin();
            font.draw(batch, edge.getSymbol().toString(), (nodeFromCenter.x+nodeToCenter.x)/2, (nodeFromCenter.y+nodeToCenter.y)/2);
            batch.end();
        }
    }

    private void drawOriginalEdge(Edge edge, Color color) {
        ComparablePoint2i nodeFromCenter = edge.getOne().getId();
        ComparablePoint2i nodeToCenter = edge.getTwo().getId();
        shapeRenderer.setColor(color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(nodeFromCenter.x, nodeFromCenter.y, nodeToCenter.x, nodeToCenter.y);
        shapeRenderer.end();
        if(edge.getSymbol() != null) {
            batch.begin();
            font.draw(batch, edge.getSymbol().toString(), (nodeFromCenter.x+nodeToCenter.x)/2, (nodeFromCenter.y+nodeToCenter.y)/2);
            batch.end();
        }
    }

    private void drawNode(Node node, Color color) {
        shapeRenderer.setColor(color);
        ComparablePoint2i nodeCenter = node.getId();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(nodeCenter.x, nodeCenter.y, 1);
        shapeRenderer.end();
        if(node.hasItem()) {
            font.getData().setScale(.4f);
            font.setColor(Color.CYAN);
            batch.begin();
            font.draw(batch, node.getItem().toString(), nodeCenter.x+5, nodeCenter.y);
            batch.end();
        }
        if(node.getPrecondition() != null) {
            font.getData().setScale(.5f);
            font.setColor(Color.RED);
            batch.begin();
            font.draw(batch, node.getPrecondition().toString(), nodeCenter.x-5, nodeCenter.y);
            batch.end();
        }
    }

    public void render(OrthographicCamera camera, World world) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        Spacecraft spacecraft = world.getWorldMap().getSpacecraft();

        /*font.getData().setScale(.5f);
        font.setColor(Color.CYAN);
        for(Node node : spacecraft.roomGraph.getNodes().values()) {
            drawNode(node, Color.RED);
        }*/

        /*font.getData().setScale(.5f);
        font.setColor(Color.CYAN);
        for(Edge edge : world.getWorldMap().getRemovedEdges()) {
            drawOriginalEdge(edge, Color.RED);
        }*/


        /*font.getData().setScale(.5f);
        font.setColor(Color.PINK);
        for(Node node : world.getWorldMap().getSpacecraft().roomGraph.getNodes().values()) {
            for(Edge edge : node.getNeighbors()) {
                if(edge.getNeighbor(node).getParent() == node)
                    drawParentEdge(node, edge.getNeighbor(node));
                else
                    drawEdge(edge, Color.GREEN);
            }
        }


        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Point2i startRoomCenter = spacecraft.findStart().center();
        shapeRenderer.circle(startRoomCenter.x, startRoomCenter.y, 1);
        shapeRenderer.end();*/

        /*shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Point2i goalRoomCenter = spacecraft.findGoal().center();
        shapeRenderer.circle(goalRoomCenter.x, goalRoomCenter.y, 1);
        shapeRenderer.end();

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Point2i bossRoomCenter = spacecraft.findBoss().center();
        shapeRenderer.circle(bossRoomCenter.x, bossRoomCenter.y, 1);
        shapeRenderer.end();


        List<ComparablePoint2i> solutionPath = world.getWorldMap().getSolutionPath();
        Iterator<ComparablePoint2i> solutionPathIterator = solutionPath.iterator();
        int i = 0;

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line(spacecraft.findStart().center().x, spacecraft.findStart().center().y, solutionPath.get(0).x, solutionPath.get(0).y);
        while(solutionPathIterator.hasNext()) {
            ComparablePoint2i point = solutionPathIterator.next();
            i++;
            if(solutionPathIterator.hasNext()) {
                ComparablePoint2i nextPoint = solutionPath.get(i);
                shapeRenderer.line(point.x, point.y, nextPoint.x, nextPoint.y);
            }
        }
        shapeRenderer.end();*/

        /*
        for (com.voronoi.Edge e : world.getOverworld().getVoronoi().getEdges()) {
            if (e.getSite_left() != null && e.getSite_right() != null) {
                double topY = (e.getSite_left().getY() == Double.POSITIVE_INFINITY) ? 10 : e.getSite_left().getY(); // HACK to draw from infinity
                shapeRenderer.setColor(Color.RED);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.line((float)e.getSite_left().getX(), (float)topY, (float)e.getSite_right().getX(), (float)e.getSite_right().getY());
                shapeRenderer.end();
            }
        }*/

        for(Region r : world.getOverworld().getVoronoiGraph().getRegions()) {
            float[] vertices = new float[r.getCorners().size()*2];
            int v = 0;
            for(Corner c : r.getCorners()) {
                vertices[v] = c.getLocation().getX();
                v++;
                vertices[v] = c.getLocation().getY();
                v++;
            }
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.polygon(vertices);
            shapeRenderer.end();
        }

        batch.begin();
        box2DDebugRenderer.render(world.getPhysics(), camera.combined);
        batch.end();

    }

    public static float[] convertFloats(List<Integer> floats)
    {
        float[] ret = new float[floats.size()];
        Iterator<Integer> iterator = floats.iterator();
        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = iterator.next().intValue();
        }
        return ret;
    }

    private void drawParentEdge(Node parent, Node child) {

        int x1 = parent.getId().x;
        int y1 = parent.getId().y;
        int x2 = child.getId().x;
        int y2 = child.getId().y;

        double sdy = Math.signum(y2-y1), sdx = Math.signum(x2-x1);
        y1 += sdy /2;
        y2 -= sdy /2;
        x1 += sdx /2;
        x2 -= sdx /2;

        int dx = 0, dy = 0;
        dx += (int)(sdy /5);
        dy += (int)(sdx /5);
        x1 += dx; x2 += dx;
        y1 += dy; y2 += dy;

        shapeRenderer.setColor(Color.ORANGE);
        drawArrow(x1, y1, x2, y2);
        shapeRenderer.setColor(Color.YELLOW);
    }

    public void drawArrow(int x1, int y1, int x2,
                          int y2) {
        Matrix4 transformMatrix = shapeRenderer.getTransformMatrix();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.line((float)x1, (float)y1, (float)x2, (float)y2);

        double len = 0.1*Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1)),
                rot = Math.atan2(x2-x1,y1-y2);
        shapeRenderer.identity();
        shapeRenderer.translate((float)x2, (float)y2, 0);
        shapeRenderer.rotate(0,0,1.0f, (float)(MathUtils.radiansToDegrees * rot));
        shapeRenderer.line(0,0,(float)(-len*2/3), (float)(len));
        shapeRenderer.line(0,0,(float)(len*2/3), (float)(len));
        shapeRenderer.identity();
        shapeRenderer.end();

    }

}
