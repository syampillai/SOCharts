package com.storedobject.chart;

import java.util.*;

/**
 * Defines a polyline that can be added to {@link SOChart}.
 *
 * @author Syam
 */
public class Polyline extends Shape {

    private static final Double MIN = -10000d;
    private final List<Point> points = new ArrayList<>();
    private boolean clipSmooth;
    private double smoothness = MIN;

    /**
     * Constructor.
     *
     * @param points Points of the polyline.
     */
    public Polyline(Point... points) {
        add(points);
    }

    @Override
    protected String getType() {
        return this instanceof  Polygon ? "polygon" : "polyline";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        points.removeIf(Objects::isNull);
        sb.append("\"shape\":{\"points\":[");
        for(int i = 0; i < points.size(); i++) {
            if(i > 0) {
                sb.append(',');
            }
            sb.append(points.get(i));
        }
        sb.append(']');
        if(smoothness < 0) {
            if(!(smoothness < -10)) {
                sb.append(",\"smooth\":\"spline\""); // Catmull-Rom
            }
        } else { // Bezier
            if(smoothness > 1) {
                smoothness = 1;
            }
            sb.append(",\"smooth\":").append(smoothness);
            clipSmooth = false; // Bug
            sb.append(",\"smoothConstraint\":").append(clipSmooth);
        }
        sb.append('}');
    }

    /**
     * Add points.
     *
     * @param points Points.
     */
    public void add(int index, Point... points) {
        if(index < 0) {
            index = 0;
        }
        if(index >= this.points.size()) {
            add(points);
            return;
        }
        if(points != null) {
            for(Point p: points) {
                this.points.add(index, p);
                ++index;
            }
        }
    }

    /**
     * Add points.
     *
     * @param points Points.
     */
    public void add(int index, Collection<Point> points) {
        if(index < 0) {
            index = 0;
        }
        if(index >= this.points.size()) {
            add(points);
            return;
        }
        if(points != null) {
            for(Point p: points) {
                this.points.add(index, p);
                ++index;
            }
        }
    }

    /**
     * Add points.
     *
     * @param points Points.
     */
    public void add(Point... points) {
        if(points != null) {
            Collections.addAll(this.points, points);
        }
    }

    /**
     * Add points.
     *
     * @param points Points.
     */
    public void add(Collection<Point> points) {
        if(points != null) {
            this.points.addAll(points);
        }
    }

    /**
     * Remove point at a specified index.
     *
     * @param index Index;
     */
    public void remove(int index) {
        if(index >= 0 && index < points.size()) {
            points.remove(index);
        }
    }

    /**
     * Remove points.
     *
     * @param points Points.
     */
    public void remove(Point... points) {
        if(points != null) {
            for(Point p: points) {
                this.points.remove(p);
            }
        }
    }

    /**
     * Remove points.
     *
     * @param points Points.
     */
    public void remove(Collection<Point> points) {
        if(points != null) {
            for(Point p: points) {
                this.points.remove(p);
            }
        }
    }

    /**
     * Switch off all smoothening. This is the default.
     */
    public void useNoSmoothening() {
        this.smoothness = MIN;
    }

    /**
     * Use Bezier interpolation to smoothen the rendering.
     *
     * @param smoothness A value between 0 and 1.
     * @param clip Whether to clip it if the rendering goes outside the bounding box or not.
     */
    public void useBezierSmoothening(double smoothness, boolean clip) {
        this.smoothness = smoothness;
        this.clipSmooth = clip;
    }

    /**
     * Use Catmull-Rom spline smoothening.
     */
    public void useCatmullRomSmoothening() {
        smoothness = -1;
    }
}
