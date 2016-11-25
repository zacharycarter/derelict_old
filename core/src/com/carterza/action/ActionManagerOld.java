package com.carterza.action;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by zachcarter on 11/22/16.
 */
public class ActionManagerOld {
    private static Queue<ActionOld> actionQueue;

    static {
        actionQueue = new LinkedList<ActionOld>();
    }

    public static Queue<ActionOld> getActionQueue() {
        return actionQueue;
    }
}
