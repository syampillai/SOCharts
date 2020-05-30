package com.storedobject.chart;

public abstract class AbstractChart extends Chart {

    public AbstractChart(Type type, AbstractData<?>... data) {
        super(type);
        super.setType(type);
        AbstractData<?>[] d = new AbstractData[type.getAxes().length];
        super.setData(d);
        if(data != null) {
            for (int i = 0; i < data.length; i++) {
                if(i == d.length) {
                    break;
                }
                d[i] = data[i];
            }
        }
    }

    @Override
    public final void setType(Type type) {
    }

    @Override
    public void validate() throws Exception {
        super.validate();
        AbstractData<?>[] d = getData();
        for(int i = 0; i < d.length; i++) {
            if(d[i] == null) {
                throw new Exception("Data for " + axisName(i) + " not set for " + className());
            }
        }
    }

    @Override
    public final void setData(AbstractData<?>... data) {
        throw new RuntimeException();
    }

    protected void setData(AbstractData<?> data, int index) {
        AbstractData<?>[] d = getData();
        if(index >= 0 && index < d.length) {
            d[index] = data;
        }
    }
}
