package com.storedobject.chart;

public class Chart extends AbstractPart implements Component {

    private Type type = Type.Line;
    private String name;
    CoordinateSystem coordinateSystem;
    private AbstractData<?>[] data;

    public Chart() {
        this(Type.Line);
    }

    public Chart(AbstractData<?>... data) {
        this(null, data);
    }

    public Chart(Type type, AbstractData<?>... data) {
        setType(type);
        this.data = data;
    }

    public void setData(AbstractData<?>... data) {
        this.data = data;
    }

    public AbstractData<?>[] getData() {
        return data;
    }

    private String type() {
        String t = type.toString();
        return Character.toLowerCase(t.charAt(0)) + t.substring(1);
    }

    @Override
    public void encodeJSON(StringBuilder sb) {
        super.encodeJSON(sb);
        ComponentPart.encode(sb, "type", type());
        sb.append(",\"encode\":{");
        String[] axes = type.getAxes();
        for(int i = 0; i < axes.length; i++) {
            if(i > 0) {
                sb.append(',');
            }
            sb.append('"').append(axes[i]).append("\":\"d").append(data[i].getSerial()).append("\"");
        }
        sb.append('}');
    }

    @Override
    public void validate() throws Exception {
        if(data == null || data.length == 0) {
            throw new Exception("Data not set for " + className());
        }
        String[] axes = type.getAxes();
        if(data.length < axes.length) {
            throw new Exception("Data for " + name(axes[data.length]) + " not set for " + className());
        }
        if(coordinateSystem == null && type.requireCoordinateSystem()) {
            throw new Exception("Coordinate system not set for " + className());
        }
    }

    String axisName(int axis) {
        return name(type.getAxes()[axis]);
    }

    static String name(String name) {
        if(name.substring(1).equals(name.substring(1).toLowerCase())) {
            return name;
        }
        StringBuilder n = new StringBuilder();
        n.append(Character.toUpperCase(name.charAt(0)));
        name.chars().skip(1).forEach(c -> {
            if(Character.isUpperCase((char)c)) {
                n.append(' ');
            }
            n.append((char)c);
        });
        return n.toString();
    }

    @Override
    public void addParts(SOChart soChart) {
        if(coordinateSystem != null) {
            coordinateSystem.addParts(soChart);
        } else {
            soChart.addParts(data);
        }
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type == null ? Type.Line : type;
    }

    public Chart plotOn(CoordinateSystem coordinateSystem) {
        if(coordinateSystem == null) {
            if(this.coordinateSystem != null) {
                this.coordinateSystem.remove(this);
            }
        } else if(type.requireCoordinateSystem()) {
            coordinateSystem.add(this);
        }
        return this;
    }

    @Override
    public String getName() {
        return name == null || name.isEmpty() ? ("Chart " + (getSerial() + 1)) : name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
