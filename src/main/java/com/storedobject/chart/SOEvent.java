package com.storedobject.chart;

public class SOEvent {
    private String name;
    private String target;

    public SOEvent(String name, String target) {
        this.name = name;
        this.target = target;
    }
    public String getEvent() {
        return name;
    }
    public String getTarget() {
        return target;
    }
    public String key() {
        return String.format("%s%s", this.name, this.target);
    }

    @Override
    public int hashCode(){
        return this.key().hashCode();
    }

    @Override
    public boolean equals(Object that){
        return this.key().equals(((SOEvent)that).key());
    }
}
