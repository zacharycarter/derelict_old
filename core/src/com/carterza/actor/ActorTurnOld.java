package com.carterza.actor;

/**
 * Created by zachcarter on 11/21/16.
 */
public class ActorTurnOld implements Comparable<ActorTurnOld> {
    private double time;
    long timestamp;
    ActorOld actor;

    public ActorTurnOld(ActorOld actor, double time, long timestamp) {
        this.actor = actor;
        this.time = time;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(ActorTurnOld o) {
        if(this.time != o.time) {
            return Double.compare(this.time, o.time);
        }
        return Long.compare(this.timestamp, o.timestamp);
    }

    public double getTime() {
        return this.time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public ActorOld getActor() {
        return this.actor;
    }
}
