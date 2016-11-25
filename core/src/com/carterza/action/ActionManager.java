package com.carterza.action;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by zachcarter on 11/22/16.
 */
public class ActionManager {
    private static Queue<Action> actionQueue;

    static {
        actionQueue = new LinkedList<Action>();
    }

    public static Queue<Action> getActionQueue() {
        return actionQueue;
    }
}
