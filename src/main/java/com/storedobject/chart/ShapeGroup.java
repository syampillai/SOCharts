package com.storedobject.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Shapes can be grouped together using this class. Also, this itself is a {@link Shape} and you can add other
 * instances of {@link ShapeGroup}s and {@link Shape}s to it to create nested groups.
 *
 * @author Syam
 */
public class ShapeGroup extends Shape {

    private final List<Shape> shapes = new ArrayList<>();

    @Override
    protected final String getType() {
        return "group";
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        int z = -1, myZ = getZ();
        Shape child;
        sb.append("\"children\":[");
        for(int i = 0; i < shapes.size(); i++) {
            if(i > 0) {
                sb.append(',');
            }
            child = shapes.get(i);
            sb.append('{');
            if(myZ >= 0) {
                z = child.getZ();
                if(z < 0) {
                    child.setZ(myZ);
                }
            }
            child.encodeJSON(sb);
            if(myZ >= 0) {
                child.setZ(z);
            }
            sb.append('}');
        }
        sb.append(']');
    }

    /**
     * Add more shapes to this.
     *
     * @param shapes Shapes to add.
     */
    public void add(Shape... shapes) {
        if(shapes != null && shapes.length > 0) {
            this.shapes.addAll(Arrays.asList(shapes));
            this.shapes.removeIf(Objects::isNull);
        }
    }

    /**
     * Remove shapes from this.
     *
     * @param shapes Shapes to remove.
     */
    public void remove(Shape... shapes) {
        if(shapes != null && shapes.length > 0) {
            for(Shape shape: shapes) {
                if(shape != null) {
                    this.shapes.remove(shape);
                }
            }
        }
    }
}
