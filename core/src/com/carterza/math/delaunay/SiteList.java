/*
 * Copyright 2014 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.carterza.math.delaunay;

import java.util.ArrayList;
import java.util.List;

import org.terasology.math.geom.Circle;
import org.terasology.math.geom.Rect2f;
import org.terasology.math.geom.Vector2f;

final class SiteList {

    private final List<Site> sites = new ArrayList<Site>();
    private int currentIndex;
    private boolean sorted;

    public SiteList() {
        sorted = false;
    }

    public int push(Site site) {
        sorted = false;
        sites.add(site);
        return sites.size();
    }

    public int getLength() {
        return sites.size();
    }

    public Site next() {
        if (!sorted) {
            throw new IllegalStateException("Sites have not been sorted");
        }
        if (currentIndex < sites.size()) {
            return sites.get(currentIndex++);
        } else {
            return null;
        }
    }

    public Rect2f getSitesBounds() {
        if (!sorted) {
            Site.sortSites(sites);
            currentIndex = 0;
            sorted = true;
        }
        float xmin;
        float xmax;
        float ymin;
        float ymax;
        if (sites.isEmpty()) {
            return Rect2f.createFromMinAndSize(0, 0, 0, 0);
        }
        xmin = Float.MAX_VALUE;
        xmax = Float.MIN_VALUE;
        for (Site site : sites) {
            if (site.getX() < xmin) {
                xmin = site.getX();
            }
            if (site.getX() > xmax) {
                xmax = site.getX();
            }
        }
        // here's where we assume that the sites have been sorted on y:
        ymin = sites.get(0).getY();
        ymax = sites.get(sites.size() - 1).getY();

        return Rect2f.createFromMinAndMax(xmin, ymin, xmax, ymax);
    }

    public List<Vector2f> siteCoords() {
        List<Vector2f> coords = new ArrayList<Vector2f>();
        for (Site site : sites) {
            coords.add(site.getCoord());
        }
        return coords;
    }

    /**
     * @return the largest circle centered at each site that fits in its region;
     * if the region is infinite, return a circle of radius 0.
     */
    public List<Circle> circles() {
        List<Circle> circles = new ArrayList<Circle>();
        for (Site site : sites) {
            float radius = 0;
            Edge nearestEdge = site.nearestEdge();

            //!nearestEdge.isPartOfConvexHull() && (radius = nearestEdge.sitesDistance() * 0.5);
            if (!nearestEdge.isPartOfConvexHull()) {
                radius = nearestEdge.sitesDistance() * 0.5f;
            }
            circles.add(new Circle(site.getX(), site.getY(), radius));
        }
        return circles;
    }

    public List<List<Vector2f>> regions(Rect2f plotBounds) {
        List<List<Vector2f>> regions = new ArrayList<List<Vector2f>>();
        for (Site site : sites) {
            regions.add(site.region(plotBounds));
        }
        return regions;
    }
}
