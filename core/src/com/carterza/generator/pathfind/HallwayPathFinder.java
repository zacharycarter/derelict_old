package com.carterza.generator.pathfind;

import com.carterza.generator.spacecraft.GridMap;
import com.carterza.generator.spacecraft.Room;
import com.carterza.generator.spacecraft.Spacecraft;
import com.carterza.generator.spacecraft.TileType;
import com.stewsters.util.math.Point2i;
import com.stewsters.util.pathing.twoDimention.shared.FullPath2d;
import com.stewsters.util.pathing.twoDimention.shared.Mover2d;
import com.stewsters.util.pathing.twoDimention.shared.PathNode2d;

import java.util.LinkedList;

public class HallwayPathFinder extends AStarPathFinder2d {

    GridMap gridMap;
    Spacecraft spacecraft;

    public HallwayPathFinder(GridMap map, int maxSearchDistance, boolean allowDiagMovement, Spacecraft spacecraft) {
        super(map, maxSearchDistance, allowDiagMovement);
        this.gridMap = map;
        this.spacecraft = spacecraft;
    }

    @Override
    protected boolean isBlocked(Mover2d mover, int x, int y) {
        // We want to be able to tunnel through ways to create hallways

        if (gridMap.getTile(x, y) == TileType.AETHER || gridMap.getTile(x, y) == TileType.VACUUM)
            return true;
        else
            return false;
    }

    public float getMovementCost(Mover2d mover, int sx, int sy, int tx, int ty, Room startRoom, Room toRoom) {
        Room roomStepingInto = this.spacecraft.findRoomContainingPoint(new Point2i(tx, ty));
        // if(roomStepingInto != null && !roomStepingInto.equals(startRoom) && !roomStepingInto.equals(toRoom))
            // return Float.MAX_VALUE;
        TileType tileType = gridMap.getTile(tx, ty);
        return tileType.digTunnelCost;
    }

    /**
     * @see PathFinder2d#findPath(com.stewsters.util.pathing.twoDimention.shared.Mover2d, int, int, int, int)
     */
    @Override
    public FullPath2d findPath(Mover2d mover, int sx, int sy, int tx, int ty) {
        // easy first check, if the destination is blocked, we can't get there

        if (isBlocked(mover, tx, ty)) {
            return null;
        }

        Room startRoom = spacecraft.findRoom(new Point2i(sx, sy));
        Room toRoom = spacecraft.findRoom(new Point2i(tx, ty));

        // initial state for A*. The closed group is empty. Only the starting
        // tile is in the open list and it's already there
        nodes[sx][sy].cost = 0;
        nodes[sx][sy].depth = 0;
        closed.clear();
        open.clear();
        open.add(nodes[sx][sy]);

        nodes[tx][ty].parent = null;

        // while we haven't exceeded our max search depth
        int maxDepth = 0;
        while ((maxDepth < maxSearchDistance) && (open.size() != 0)) {
            // pull out the first PathNode in our open list, this is determined to
            // be the most likely to be the next step based on our heuristic

            PathNode2d current = getFirstInOpen();
            if (current == nodes[tx][ty]) {
                break;
            }

            removeFromOpen(current);
            addToClosed(current);

            // search through all the neighbours of the current PathNode evaluating

            // them as next steps

            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    // not a neighbour, its the current tile

                    if ((x == 0) && (y == 0)) {
                        continue;
                    }

                    // if we're not allowing diaganol movement then only
                    // one of x or y can be set

                    if (!allowDiagMovement) {
                        if ((x != 0) && (y != 0)) {
                            continue;
                        }
                    }

                    // determine the location of the neighbour and evaluate it

                    int xp = x + current.x;
                    int yp = y + current.y;

                    if (isValidLocation(mover, sx, sy, xp, yp)) {
                        // the cost to get to this PathNode is cost the current plus the movement
                        // cost to reach this node. Note that the heuristic value is only used
                        // in the sorted open list

                        float nextStepCost = current.cost + getMovementCost(mover, current.x, current.y, xp, yp, startRoom, toRoom);
                        PathNode2d neighbour = nodes[xp][yp];
                        map.pathFinderVisited(xp, yp);

                        // if the new cost we've determined for this PathNode is lower than
                        // it has been previously,
                        // there might have been a better path to get to
                        // this PathNode so it needs to be re-evaluated

                        if (nextStepCost < neighbour.cost) {
                            if (inOpenList(neighbour)) {
                                removeFromOpen(neighbour);
                            }
                            if (inClosedList(neighbour)) {
                                removeFromClosed(neighbour);
                            }
                        }

                        // if the PathNode hasn't already been processed and discarded then
                        // reset it's cost to our current cost and add it as a next possible
                        // step (i.e. to the open list)

                        if (!inOpenList(neighbour) && !(inClosedList(neighbour))) {
                            neighbour.cost = nextStepCost;
                            neighbour.heuristic = getHeuristicCost(mover, xp, yp, tx, ty);
                            maxDepth = Math.max(maxDepth, neighbour.setParent(current));
                            addToOpen(neighbour);
                        }
                    }
                }
            }

        }

        // since we'e've run out of search
        // there was no path. Just return null

        if (nodes[tx][ty].parent == null) {
            return null;
        }

        // At this point we've definitely found a path so we can uses the parent

        // references of the nodes to find out way from the target location back

        // to the start recording the nodes on the way.

        FullPath2d path = new FullPath2d();
        PathNode2d target = nodes[tx][ty];
        while (target != nodes[sx][sy]) {
            path.prependStep(target.x, target.y);
            target = target.parent;
        }
        path.prependStep(sx, sy);

        // thats it, we have our path

        return path;
    }
}