package com.carterza.world;

import com.carterza.event.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachcarter on 11/21/16.
 */
public class WorldUpdate {
    final List<Event> events;

    protected boolean worldProgressed = false;

    public WorldUpdate() {
        this.events = new ArrayList<Event>();
    }

    public boolean needsRefresh() {
        return worldProgressed || events.size() > 0;
    }
}
