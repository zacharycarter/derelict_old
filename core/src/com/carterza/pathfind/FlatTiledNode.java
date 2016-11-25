package com.carterza.pathfind;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

/** A node for a {@link FlatTiledGraph}.
 *
 * @author davebaol */
public class FlatTiledNode extends TiledNode<FlatTiledNode> {

    public FlatTiledNode (int x, int y, int type, int connectionCapacity) {
        super(x, y, type, new Array<Connection<FlatTiledNode>>(connectionCapacity));
    }

    @Override
    public int getIndex () {
        return x * FlatTiledGraph.sizeY + y;
    }

}
