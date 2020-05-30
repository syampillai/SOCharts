/*
 *  Copyright 2019-2020 Syam Pillai
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.storedobject.chart;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 * Chart is a Vaadin {@link com.vaadin.flow.component.Component} so that you can add it to any layout component for
 * displaying it. It is a LitComponent wrapper around the "echarts" library.
 * </p>
 * <p>
 * Chart is composed of one more chart {@link Component}s and each chart {@link Component} may have zero or more
 * {@link ComponentPart}s. Examples of chart {@link Component}s are (a) {@link Chart},
 * (b) {@link RectangularCoordinate}, (c) {@link PolarCoordinate} etc.
 * An example of a {@link ComponentPart} is {@link AngleAxis} that is used by the {@link PolarCoordinate}.
 * </p>
 * <p>
 * Typical usage of SOChart is to new it and add it to some layout for displaying it. Any {@link Component} that is
 * added to the {@link Chart} will be be displayed. For example, you can crate a {@link PieChart} and add it to the
 * {@link SOChart} using {@link #add(Component...)}.
 * </p>
 * <pre>
 *     SOChart soChart = new SOChart();
 *     soChart.setSize("600px", "400px");
 *     CategoryData labels = new CategoryData("Banana", "Apple", "Orange", "Grapes");
 *     Data data = new Data(25, 40, 20, 30);
 *     soChart.add(new PieChart(labels, data));
 *     myLayout.add(soChart);
 * </pre>
 *
 * @author Syam
 */
@NpmPackage(value = "echarts", version = "4.8.0")
@Tag("so-chart")
@JsModule("./so/chart/chart.js")
public class SOChart extends com.vaadin.flow.component.Component implements HasSize {

    private final static ComponentEncoder[] encoders = {
            new ComponentEncoder(Title.class),
            new ComponentEncoder(Legend.class),
            new ComponentEncoder("dataset", AbstractData.class),
            new ComponentEncoder(AngleAxis.class),
            new ComponentEncoder(RadiusAxis.class),
            new ComponentEncoder(XAxis.class),
            new ComponentEncoder(YAxis.class),
            new ComponentEncoder("polar", PolarCoordinate.class),
            new ComponentEncoder("grid", RectangularCoordinate.class),
            new ComponentEncoder("series", Chart.class),
    };
    final static AtomicLong id = new AtomicLong(0);
    private final List<String> functions = new ArrayList<>();
    private volatile Map<String, Serializable[]> parameters = new HashMap<>();
    private final List<Component> components = new ArrayList<>();
    private final List<ComponentPart> parts = new ArrayList<>();
    private Legend legend = new Legend();

    /**
     * Constructor.
     */
    public SOChart() {
        getElement().setProperty("idChart", "sochart" + id.incrementAndGet());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        try {
            clear();
            update();
        } catch (Exception ignored) {
        }
    }

    /**
     * Legends will be shown by default. However, you can either disable it using this method or
     * you can create your own customized legends and add it using {@link #add(Component...)}.
     */
    public void disableDefaultLegend() {
        legend = null;
    }

    /**
     * Set the size.
     *
     * @param width Width.
     * @param height Height.
     */
    public void setSize(String width, String height) {
        setWidth(width);
        setHeight(height);
    }

    @Override
    public void setWidth(String width) {
        HasSize.super.setWidth(width);
        getElement().setProperty("width", width);
    }

    @Override
    public void setHeight(String height) {
        HasSize.super.setHeight(height);
        getElement().setProperty("height", height);
    }

    @Override
    public void setMinWidth(String minWidth) {
        HasSize.super.setMinWidth(minWidth);
        getElement().setProperty("minw", minWidth);
    }

    @Override
    public void setMinHeight(String minHeight) {
        HasSize.super.setMinHeight(minHeight);
        getElement().setProperty("minh", minHeight);
    }

    @Override
    public void setMaxWidth(String maxWidth) {
        HasSize.super.setMaxWidth(maxWidth);
        getElement().setProperty("maxw", maxWidth);
    }

    @Override
    public void setMaxHeight(String maxHeight) {
        HasSize.super.setMaxHeight(maxHeight);
        getElement().setProperty("maxh", maxHeight);
    }

    /**
     * This should be invoked only from {@link Component#addParts(SOChart)} method (That method will be called
     * on each {@link Component} whenever chart is getting updated).
     *
     * @param parts Parts to add.
     */
    void addParts(ComponentPart... parts) {
        if(parts != null) {
            this.parts.addAll(Arrays.asList(parts));
        }
    }

    /**
     * Add components to the chart. (Chart will not be updated unless {@link #update()} method is called).
     *
     * @param components Components to add.
     */
    public void add(Component... components) {
        if(components != null) {
            this.components.addAll(Arrays.asList(components));
        }
    }

    /**
     * Remove components from the chart. (Chart will not be updated unless {@link #update()} method is called).
     *
     * @param components Components to remove.
     */
    public void remove(Component... components) {
        if(components != null) {
            for(Component c: components) {
                this.components.remove(c);
            }
        }
    }

    /**
     * Remove all components from the chart. (Chart display will not be cleared unless {@link #update()}
     * or {@link #clear()} method is called).
     */
    public void removeAll() {
        components.clear();
    }

    /**
     * Clear the chart. This will remove the chart display. However, it can be
     * rendered again by invoking {@link #update()} as long as {@link #removeAll()} is not called.
     */
    public void clear() {
        js("clearChart");
    }

    @ClientCallable
    private void ready() {
        synchronized (functions) {
            String function;
            while (!functions.isEmpty()) {
                function = functions.remove(0);
                exec(function, parameters.get(function));
            }
            parameters = null;
        }
    }

    private synchronized void js(String function, Serializable... parameters) {
        if(this.parameters == null) {
            exec(function, parameters);
        } else {
            synchronized (functions) {
                if(this.parameters == null) {
                    exec(function, parameters);
                } else {
                    functions.add(function);
                    this.parameters.put(function, parameters);
                }
            }
        }
    }

    private void exec(String function, Serializable... parameters) {
        getElement().callJsFunction(function, parameters);
    }

    /**
     * Update the chart display with current set of components. {@link Component#validate()} method of each component
     * will be invoked before updating the chart display. The chart display may be already there and only the changes
     * and additions will be updated. If a completely new chart is required, {@link #clear()} should be invoked before
     * this.
     *
     * @throws Exception When any of the component is not valid.
     */
    public void update() throws Exception {
        if(components.isEmpty()) {
            clear();
            return;
        }
        for(Component c: components) {
            c.validate();
            c.setSerial(-2);
        }
        parts.clear();
        for(Component c: components) {
            c.addParts(this);
        }
        for(ComponentPart c: parts) {
            c.validate();
            c.setSerial(-2);
        }
        parts.addAll(components);
        if(legend != null && parts.stream().noneMatch(cp -> cp instanceof Legend)) {
            parts.add(legend);
        }
        for(ComponentEncoder ce: encoders) {
            int serial = 0;
            for (ComponentPart cp : parts) {
                if(ce.partType.isAssignableFrom(cp.getClass())) {
                    if(cp.getSerial() == -2) {
                        cp.setSerial(serial++);
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for(ComponentEncoder ce: encoders) {
            ce.encode(sb, parts);
        }
        sb.append('}');
        js("updateChart", customizeJSON(sb.toString()));
        parts.clear();
    }

    /**
     * This method is invoked just before the JSON string that is being constructed in the {@link #update()} method
     * is sent to the client. The returned value by this method will be sent to the "echarts" instance
     * at the client-side. The default implementation just returns the same string. However, if someone wants to do
     * some cutting-edge customization, this method can be used. This JOSN string is used to construct the
     * "option" parameter for the "echarts.setOption(option)" JavaScript method.
     *
     * @param json JSON string constructed by the {@link #update()} method.
     * @return Customized JSON string.
     * @throws Exception If any custom error to be notified so that rendering will not happen.
     */
    @SuppressWarnings("RedundantThrows")
    protected String customizeJSON(String json) throws Exception {
        return json;
    }

    static String encoderLabel(ComponentPart part) {
        Class<? extends ComponentPart> cpClass = part.getClass();
        for(ComponentEncoder ce: encoders) {
            if(cpClass.isAssignableFrom(ce.partType)) {
                return ce.label;
            }
        }
        return null;
    }

    private static class ComponentEncoder {

        private final String label;
        private final Class<? extends ComponentPart> partType;

        private ComponentEncoder(Class<? extends ComponentPart> partType) {
            this(null, partType);
        }

        private ComponentEncoder(String label, Class<? extends ComponentPart> partType) {
            this.partType = partType;
            if(label == null) {
                label = partType.getName();
                label = label.substring(label.lastIndexOf('.') + 1);
                label = Character.toLowerCase(label.charAt(0)) + label.substring(1);
            }
            this.label = label;
        }

        private void encode(StringBuilder sb, List<? extends ComponentPart> components) {
            boolean data = label.equals("dataset");
            boolean first = true;
            int serial = -2;
            for (ComponentPart c: components) {
                if(partType.isAssignableFrom(c.getClass())) {
                    if(c.getSerial() < serial) {
                        break;
                    }
                    serial = c.getSerial();
                    if(first) {
                        first = false;
                        if(sb.length() > 1) {
                            sb.append(',');
                        }
                        sb.append('"').append(label).append("\":");
                        if(data) {
                            sb.append("{\"source\":{");
                        } else {
                            sb.append('[');
                        }
                    } else {
                        sb.append(',');
                    }
                    if(!data) {
                        sb.append('{');
                    }
                    c.encodeJSON(sb);
                    while(sb.charAt(sb.length() - 1) == ',') {
                        sb.setCharAt(sb.length() - 1, ' ');
                    }
                    if(!data) {
                        sb.append('}');
                    }
                }
            }
            if(!first) {
                if(data) {
                    sb.append("}}");
                } else {
                    sb.append(']');
                }
            }
        }
    }
}