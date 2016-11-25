package com.carterza.map;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Polygon;
import com.carterza.math.delaunay.Voronoi;
import com.carterza.math.delaunay.graph.Corner;
import com.carterza.math.delaunay.graph.GraphEditor;
import com.carterza.math.delaunay.graph.Region;
import com.carterza.math.delaunay.graph.VoronoiGraph;
import com.carterza.math.delaunay.sampling.PointSampling;
import com.carterza.math.delaunay.sampling.PoissonDiscSampling;
import com.carterza.utilities.random.FastRandom;
import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Rect2i;
import org.terasology.math.geom.Vector2f;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by zachcarter on 11/25/16.
 */
public class Overworld {

    private Voronoi voronoi;
    private VoronoiGraph voronoiGraph;
    private Random random;

    public Overworld() {
        this.random = new Random();
    }

    public void generate() {
        Rect2i bounds = Rect2i.createFromMinAndMax(0,0,128,64);
        PointSampling sampling = new PoissonDiscSampling();

        Rect2f doubleBounds = Rect2f.createFromMinAndSize(0, 0, bounds.width(), bounds.height());

        final com.carterza.utilities.random.Random rng = new FastRandom(random.nextLong());

        List<Vector2f> points = sampling.create(Rect2f.createFromMinAndMax(bounds.minX(), bounds.minY(), bounds.maxX(), bounds.maxY()), 20, rng);

        Voronoi v = new Voronoi(points, doubleBounds);

        // Lloyd relaxation makes regions more uniform
        v = GraphEditor.lloydRelaxation(v);

        this.voronoiGraph = new VoronoiGraph(bounds, v);

        Map<Region, Color> regionColorMap = new HashMap<Region, Color>();
        for(Region r : voronoiGraph.getRegions()) {
            regionColorMap.put(r, new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1));
        }

        Color currentColor;
        Pixmap pixmap = new Pixmap(128, 64, Pixmap.Format.RGBA8888);
        for(int x = 0; x < pixmap.getWidth(); x++) {
            for(int y = 0; y < pixmap.getHeight(); y++) {
                currentColor = regionColorMap.get(findRegionContainingPoint(x,y));
                if(currentColor != null)
                    pixmap.setColor(currentColor);
                    pixmap.drawPixel(x, y);
            }
        }

        PixmapIO.writePNG(new FileHandle(new File("overworld.png")), pixmap);
    }

    private Region findRegionContainingPoint(int x, int y) {
        for(Region r : voronoiGraph.getRegions()) {
            float[] verts = new float[r.getCorners().size()*2];
            int c = 0;
            for(Corner corner : r.getCorners()) {
                verts[c] = corner.getLocation().getX();
                c++;
                verts[c] = corner.getLocation().getY();
                c++;
            }
            Polygon p = new Polygon(verts);
            if(p.contains(x, y)) return r;
        }
        return null;
    }

    public VoronoiGraph getVoronoiGraph() {
        return voronoiGraph;
    }
}
