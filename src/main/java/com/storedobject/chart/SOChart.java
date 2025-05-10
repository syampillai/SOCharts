/*
 *  Copyright Syam Pillai
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

import com.storedobject.helper.ID;
import com.storedobject.helper.LitComponentWithSize;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.shared.Registration;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
 * Typical usage of the SOChart is to new it and add it to some layout for displaying it. Any {@link Component} that is
 * added to the {@link Chart} will be displayed. For example, you can create a {@link PieChart} and add it to the
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
@NpmPackage(value = "echarts", version = "5.6.0")
@Tag("so-chart")
@JsModule("./so/chart/chart.js")
public class SOChart extends LitComponentWithSize {

    final static ComponentEncoder[] encoders = {
            new ComponentEncoder("*", DefaultColors.class),
            new ComponentEncoder("*", DefaultTextStyle.class),
            new ComponentEncoder(Title.class),
            new ComponentEncoder(Legend.class),
            new ComponentEncoder(Tooltip.class),
            new ComponentEncoder("angleAxis", AngleAxis.AngleAxisWrapper.class),
            new ComponentEncoder("radiusAxis", RadiusAxis.RadiusAxisWrapper.class),
            new ComponentEncoder("xAxis", XAxis.XAxisWrapper.class),
            new ComponentEncoder("yAxis", YAxis.YAxisWrapper.class),
            new ComponentEncoder("polar", PolarCoordinate.class),
            new ComponentEncoder("radar", RadarCoordinate.class),
            new ComponentEncoder("grid", RectangularCoordinate.class),
            new ComponentEncoder("series", Chart.class),
            new ComponentEncoder("dataZoom", AbstractDataZoom.class),
            new ComponentEncoder("visualMap", VisualMap.class),
            new ComponentEncoder("graphic", Shape.class),
            new ComponentEncoder(Toolbox.class),
    };
    private final List<ComponentGroup> componentGroups = new ArrayList<>();
    private final List<Component> components = new ArrayList<>();
    private final List<ComponentPart> parts = new ArrayList<>();
    private final List<AbstractDataProvider<?>> dataSet = new ArrayList<>();
    private final List<AbstractDataProvider<?>> extraData = new ArrayList<>();
    private Legend legend = new Legend();
    private Tooltip tooltip = new Tooltip();
    private boolean neverUpdated = true;
    private DefaultColors defaultColors;
    private AbstractColor defaultBackground;
    private DefaultTextStyle defaultTextStyle;
    private String theme;
    private Language language;
    private boolean svg = false;
    private final Map<Integer, Integer> dataLengthMap = new HashMap<>();
    private final Map<Integer, Integer> datasetIndexMap = new HashMap<>();
    private boolean debugData = false;
    private boolean debugOptions = false;
    private boolean debugEvents = false;
    private final AtomicInteger eventId = new AtomicInteger(0);
    private final EventHandles eventHandles = new EventHandles();

    /**
     * Constructor.
     */
    public SOChart() {
        getElement().setProperty("idChart", "sochart" + ID.newID());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        try {
            if(neverUpdated) {
                update();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<? extends ComponentPart> partType(String type) {
        for(ComponentEncoder ce: encoders) {
            if(ce.label.equals(type)) {
                return ce.partType;
            }
        }
        return null;
    }

    /**
     * Enables or disables debugging functionality at the client-side. Debug messages will be printed to
     * the JavaScript console if enabled.
     *
     * @param debugData    if true, enables debugging for data sets used.
     * @param debugOptions if true, enables debugging for options and configurations of the chart.
     * @param debugEvents  if true, enables debugging for events and their handling.
     */
    public void debug(boolean debugData, boolean debugOptions, boolean debugEvents) {
        this.debugData = debugData;
        this.debugOptions = debugOptions;
        this.debugEvents = debugEvents;
        if(!neverUpdated) {
            executeJS("debug", debugData, debugOptions, debugEvents);
        }
    }

    /**
     * This method is invoked when SOChart wants to show some errors. The default implementation shows a notification
     * for 1 minute. However, you can override this method to customize the error handling.
     * @param message The error message to be shown.
     */
    @ClientCallable
    public void onError(String message) {
        Notification.show(message, 60000, Notification.Position.MIDDLE, true);
    }

    @ClientCallable
    private void onMouseEvent(int id, String componentType, int componentIndex, String componentSubtype, String seriesId,
                         String targetType, String value) {
        EventHandler eventHandler = eventHandles.get(id);
        if(eventHandler == null) {
            return;
        }
        ChartEvent event = new ChartEvent(this, eventHandler.type, eventHandler.componentPart);
        event.addData("type", partType(componentType));
        event.addData("serial", componentIndex);
        String part = componentSubtype;
        if(part == null || part.isEmpty()) {
            part = targetType;
        }
        event.addData("part", part);
        event.addData("value", value);
        eventHandler.listener.onEvent(event);
    }

    /**
     * Adds an event listener for a specific component part. The part should generate events from echarts.
     *
     * @param componentPart The component part that can generate an event in echarts.
     * @param listener the functional interface that handles the click events
     * @return a Registration object that can be used to remove the added click listener
     */
    public Registration addListener(ComponentPart componentPart, ChartEventType eventType, ChartEventListener listener) {
        if(componentPart.getEventParameters(eventType) == null) {
            return null;
        }
        EventHandler eh = new EventHandler(listener, eventType, componentPart);
        return () -> eventHandles.removeHandler(eh);
    }

    /**
     * Adds an event listener to the chart that triggers when a mouse event occurs on the chart where no chart
     * element exists (empty areas).
     *
     * @param listener the event listener to handle the chart events
     * @return a Registration object that can be used to remove the listener
     */
    public Registration addListener(ChartEventType eventType, ChartEventListener listener) {
        EventHandler eh = new EventHandler(listener, eventType, null);
        return () -> eventHandles.removeHandler(eh);
    }

    /**
     * Get the list of default colors. A list is returned, and you may add any number of
     * colors to that list. Those colors will be used sequentially and circularly. However, please note that
     * if the list contains less than 11 colors, more colors will be added to it automatically from the
     * following to make count 11:<BR>
     * ['#0000ff', '#c23531', '#2f4554', '#61a0a8', '#d48265', '#91c7ae',
     * '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570', '#c4ccd3']
     *
     * @return List of default colors.
     */
    public List<AbstractColor> getDefaultColors() {
        if(defaultColors == null) {
            defaultColors = new DefaultColors();
        }
        return defaultColors;
    }

    /**
     * Set the default background color.
     *
     * @param background Background color.
     */
    public void setDefaultBackground(Color background) {
        this.defaultBackground = background;
    }

    /**
     * Get the default text style. You may invoke this method and override default values if required. However,
     * please note that setting padding, border (not text border) and alignment properties do not have any effect.
     *
     * @return Default text style.
     */
    public TextStyle getDefaultTextStyle() {
        if(defaultTextStyle == null) {
            defaultTextStyle = new DefaultTextStyle();
        }
        return defaultTextStyle.textStyle;
    }

    /**
     * A tooltip will be shown by default. However, you can either disable it using this method or
     * you can create your own customized tooltips and add it using {@link #add(Component...)}.
     */
    public void disableDefaultTooltip() {
        tooltip = null;
    }

    /**
     * Get the default tooltip. You can customize it.
     *
     * @return THe default tooltip. Will return null if it was disabled via {@link #disableDefaultTooltip()}.
     */
    public Tooltip getDefaultTooltip() {
        return tooltip;
    }

    /**
     * Legends will be shown by default. However, you can either disable it using this method or
     * you can create your own customized legends and add it using {@link #add(Component...)}.
     */
    public void disableDefaultLegend() {
        legend = null;
    }

    /**
     * Get the default legend. You can customize it.
     *
     * @return THe default legend. Will return null if it was disabled via {@link #disableDefaultLegend()}.
     */
    public Legend getDefaultLegend() {
        return legend;
    }

    /**
     * Set the dark theme.
     */
    public void setDarkTheme() {
        this.theme = "dark";
        updateThemeAndLocale();
    }

    /**
     * Switch on SVG rendering instead of canvas rendering.
     */
    public void setSVGRendering() {
        svg = true;
        updateThemeAndLocale();
    }

    /**
     * Set language.
     *
     * @param language {@link Language}.
     */
    public void setLanguage(Language language) {
        this.language = language;
        updateThemeAndLocale();
    }

    private void updateThemeAndLocale() {
        if(!neverUpdated) {
            executeJS("setThemeAndLocale", theme, language(), svg ? "svg" : "canvas");
        }
    }

    private String language() {
        if(language == null) {
            return null;
        }
        return language.toString().replace('_', '-');
    }

    /**
     * This should be invoked only from {@link Component#addParts(SOChart)} method (That method will be called
     * on each {@link Component} whenever chart is getting updated).
     *
     * @param parts Parts to add.
     */
    void addParts(ComponentPart... parts) {
        if(parts != null) {
            for(ComponentPart cp: parts) {
                if(cp != null) {
                    this.parts.add(cp);
                }
            }
        }
    }

    /**
     * Add data to the chart. This method is normally not required to be used because the {@link Chart}s that are
     * added will automatically add its respective data too. This is used only when some extra data that is
     * used in the {@link Chart}s directly for some other display purposes.
     *
     * @param data Data to add.
     */
    public void addData(AbstractDataProvider<?> data) {
        if(data != null && !extraData.contains(data)) {
            extraData.add(data);
        }
    }

    /**
     * Remove extra data added via {@link #addData(AbstractDataProvider)}.
     *
     * @param data Data to remove.
     */
    public void removeData(AbstractDataProvider<?> data) {
        extraData.remove(data);
    }

    /**
     * Get all the data involved in this chart component. This is for internal use only and will be available while
     * rendering the chart only.
     *
     * @return Data involved in this chart.
     */
    List<AbstractDataProvider<?>> dataSet() {
        return dataSet;
    }

    /**
     * Add component groups.
     *
     * @param componentGroups Component groups to add.
     */
    public void add(ComponentGroup... componentGroups) {
        if(componentGroups != null) {
            for(ComponentGroup componentGroup: componentGroups) {
                if(componentGroup != null) {
                    this.componentGroups.add(componentGroup);
                }
            }
        }
    }

    /**
     * Remove component groups.
     *
     * @param componentGroups Component groups to remove.
     */
    public void remove(ComponentGroup... componentGroups) {
        if(componentGroups != null) {
            for(ComponentGroup componentGroup: componentGroups) {
                if(componentGroup != null) {
                    this.componentGroups.remove(componentGroup);
                    componentGroup.removeParts(this);
                }
            }
        }
    }

    /**
     * Add components to the chart. (Chart will not be updated unless {@link #update()} method is called).
     *
     * @param components Components to add.
     */
    public void add(Component... components) {
        if(components != null) {
            for(Component c: components) {
                if(c != null) {
                    this.components.add(c);
                }
            }
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
                if(c != null) {
                    this.components.remove(c);
                }
            }
        }
    }

    /**
     * Remove all components, associated data and event handlers from the chart. (Chart display will not be cleared
     * unless {@link #update()} or {@link #clear()} method is called).
     */
    public void removeAll() {
        eventHandles.clear();
        componentGroups.clear();
        components.clear();
        dataSet.clear();
        datasetIndexMap.clear();
        dataLengthMap.clear();
    }

    /**
     * Clear the chart. This will remove the chart display. However, it can be
     * rendered again by invoking {@link #update()} as long as {@link #removeAll()} is not called.
     */
    public void clear() {
        if(neverUpdated) {
            return;
        }
        executeJS("clearChart");
    }

    /**
     * Update the chart display with the current set of components. {@link Component#validate()} method of each component
     * will be invoked before updating the chart display. The chart display may be already there, and only the changes
     * and additions will be updated. If a completely new display is required, {@link #clear()} should be invoked before
     * this. (Please note that an "update" will automatically happen when a {@link SOChart} is added to its parent
     * layout for the first time).
     * <p>Note: If this is not the first update, data changes will not be transmitted to the client. So, if you
     * really want to update the whole data too, you should use the {@link #update(boolean)} method with the parameter
     * set to <code>false</code>. However, it is better to transmit data separately via one of the data update methods
     * ({@link #updateData(AbstractDataProvider...)} and {@link #updateData(HasData...)}) or use the
     * {@link DataChannel} for updating data once the first rendering was already done.</p>
     *
     * @throws ChartException When any of the component is not valid.
     * @throws Exception If the JSON customizer raises any exception.
     */
    public void update() throws ChartException, Exception {
        update(true);
    }

    /**
     * <p>
     * This method is same as {@link #update()} but based on the parameter passed, no data may be passed to the
     * client-side. So, it is useful only if it is a partial update. Old set of data passed will be used for the
     * display changes if parameter is <code>true</code>.
     * </p>
     * <p>
     * Why is this method required? If the data set is huge, it will be accountable for the majority of the
     * communication overhead, and it will be useful if we can update the display with other changes if no data is
     * changed.
     * </p>
     * <p>
     * Even after eliminating the overhead of data, we can eliminate other components that are not changed
     * via the method {@link #remove(Component...)}.
     * </p>
     *
     * @param skipData Skip data or not. This parameter will be ignored if this is the first-time update. However,
     *                any data never sent to the client will be sent anyway.
     * @throws ChartException When any of the component is not valid.
     * @throws Exception If the JSON customizer raises any exception.
     */
    public void update(boolean skipData) throws ChartException, Exception {
        if(neverUpdated && skipData) {
            skipData = false;
        }
        if(!skipData) {
            executeJS("clearData");
        }
        for(ComponentGroup cg: componentGroups) {
            cg.validate();
            cg.addParts(this);
        }
        if(components.isEmpty()) {
            clear();
            return;
        }
        for(Component component: components) {
            component.validate();
        }
        components.forEach(c -> c.setSerial(-2));
        parts.clear();
        components.forEach(c -> c.addParts(this));
        List<AbstractDataProvider<?>> data = new ArrayList<>();
        dataSet.clear();
        parts.stream().filter(p -> p instanceof AbstractDataProvider).map(p -> (AbstractDataProvider<?>)p)
                .forEach(data::add);
        parts.removeIf(p -> p instanceof AbstractDataProvider);
        Set<AbstractDataProvider<?>> collectData = new HashSet<>();
        parts.stream().filter(p -> p instanceof HasData).map(p -> (HasData)p).forEach(p -> p.declareData(collectData));
        collectData.removeIf(Objects::isNull);
        data.addAll(collectData);
        int dserial = data.stream().mapToInt(ComponentPart::getSerial).max().orElse(1);
        dserial = Math.max(dserial, 1);
        while(!data.isEmpty()) {
            AbstractDataProvider<?> d = data.removeFirst();
            if(!(d instanceof InternalDataProvider)) {
                dataSet.add(d);
            }
            dserial = getDataSerial(skipData, dserial, d);
            data.removeIf(ad -> ad.getSerial() == d.getSerial());
            extraData.removeIf(ad -> ad.getSerial() == d.getSerial());
        }
        for(AbstractDataProvider<?> extra: extraData) {
            dataSet.add(extra);
            dserial = getDataSerial(skipData, dserial, extra);
        }
        for(ComponentPart c: parts) {
            c.setSerial(-2);
        }
        parts.addAll(components);
        if(defaultColors != null && !defaultColors.isEmpty()) {
            parts.add(defaultColors);
        }
        if(defaultTextStyle != null) {
            parts.add(defaultTextStyle);
        }
        if(legend != null && parts.stream().noneMatch(cp -> cp instanceof Legend)) {
            parts.add(legend);
        }
        if(tooltip != null && parts.stream().noneMatch(cp -> cp instanceof Tooltip)) {
            parts.add(tooltip);
        }
        for(ComponentEncoder ce: encoders) {
            int serial = 0;
            for (ComponentPart cp : parts) {
                if(ce.partType.isAssignableFrom(cp.getClass())) {
                    if(cp.getSerial() == -2) {
                        cp.setSerial(serial++);
                    } else if(cp.getSerial() < 0) {
                        throw new ChartException("Get/set serial not properly implemented in " + cp.className());
                    }
                }
            }
        }
        for(Component c: components) {
            c.validate();
        }
        for(ComponentPart c: parts) {
            c.validate();
        }
        parts.sort(Comparator.comparing(ComponentPart::getSerial));
        StringBuilder sb = new StringBuilder();
        sb.append("{\"dataset\":[");
        List<List<Integer>> dataOrder = new ArrayList<>();
        for (AbstractDataProvider<?> d : dataSet) {
            int length = dataLengthMap.get(d.getSerial());
            List<Integer> order = dataOrder.stream().filter(o -> o.getFirst() == length).findFirst().orElse(null);
            if(order == null) {
                order = new ArrayList<>();
                order.add(length);
                dataOrder.add(order);
            }
            order.add(d.getSerial());
        }
        boolean firstIndex = true;
        for(List<Integer> order: dataOrder) {
            if(firstIndex) {
                firstIndex = false;
            } else {
                sb.append(',');
            }
            sb.append("{\"source\":{");
            sb.append(order.stream().skip(1).map(i -> "\"d" + i + "\":" + i)
                    .collect(Collectors.joining(",")));
            sb.append("}}");
        }
        sb.append("]");
        if(defaultBackground != null) {
            sb.append(",\"backgroundColor\":").append(defaultBackground);
        }
        for (ComponentEncoder ce : encoders) {
            ce.encode(this, sb, parts);
        }
        ComponentPart.addComma(sb);
        addCustomEncoding((ComponentPart) null, sb);
        ComponentPart.removeComma(sb);
        sb.append('}');
        executeJS("updateChart", !skipData, customizeJSON(sb.toString()), theme, language(),
                svg ? "svg" : "canvas", debugData, debugOptions, debugEvents);
    }

    private void chartUpdated() {
        dataSet.clear();
        parts.clear();
        defaultColors = null;
        defaultBackground = null;
        defaultTextStyle = null;
        neverUpdated = false;
    }

    private int getDataSerial(boolean skipData, int dserial, AbstractDataProvider<?> d) throws Exception {
        if(skipData) {
            if(d.getSerial() <= 0) {
                d.validate();
                d.setSerial(dserial++);
                initData(d);
            }
        } else {
            if(d.getSerial() <= 0) {
                d.validate();
                d.setSerial(dserial++);
            }
            initData(d);
        }
        return dserial;
    }

    /**
     * This method is invoked after rendering each {@link ComponentPart} type so that you can add more such components.
     * <p>Note: Please note that if you are adding any custom code, it should merge properly to the already
     * generated part in the buffer. You may use {@link #customizeJSON(String)} to check the outcome.</p>
     * <p>Warning: If you add custom code via this mechanism, it may not be compatible with the future releases
     * because the functionality you add via this mechanism may be supported directly in the future releases.</p>
     *
     * @param componentPartClass The component part class that is currently added. You may add more of these.
     * @param buffer The buffer that contains the code that is already generated.
     */
    @SuppressWarnings("unused")
    protected void addCustomEncoding(Class<? extends ComponentPart> componentPartClass, StringBuilder buffer) {
    }

    /**
     * This method is invoked after rendering each {@link ComponentPart}. You can add custom code into the buffer
     * that already contains the encoding for the part. Also, you can add at the root-level (not related to any
     * particular {@link ComponentPart}). For this, this method is invoked with the component part parameter
     * set to null.
     * <p>Note: Please note that if you are adding any custom code, it should merge properly to the already
     * generated part in the buffer. You may use {@link #customizeJSON(String)} to check the outcome.</p>
     * <p>Warning: If you add custom code via this mechanism, it may not be compatible with the future releases
     * because the functionality you add via this mechanism may be supported directly in the future releases.</p>
     *
     * @param componentPart The component part to which custom code can be added. If this is null, then code added will
     *                      go to the root-level.
     * @param buffer The buffer that contains the code that is already generated.
     */
    @SuppressWarnings("unused")
    protected void addCustomEncoding(ComponentPart componentPart, StringBuilder buffer) {
    }

    /**
     * This method is invoked just before the JSON string that is being constructed in the {@link #update()} method
     * is sent to the client. The returned value by this method will be sent to the "echarts" instance
     * at the client-side. The default implementation just returns the same string. However, if someone wants to do
     * some cutting-edge customization, this method can be used. This JSON string is used to construct the
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

    /**
     * This method is invoked just before the JSON string that is being constructed to send data
     * to the client. The returned value by this method will be sent to the "echarts" instance
     * at the client-side. The default implementation just returns the same string. However, if someone wants to do
     * some cutting-edge customization, this method can be used.
     *
     * @param json JSON string constructed by the {@link #update()} method.
     * @param data The data part for which this JSON string was created.
     * @return Customized JSON string.
     * @throws Exception If any custom error to be notified so that rendering will not happen.
     */
    @SuppressWarnings("RedundantThrows")
    protected String customizeDataJSON(String json, @SuppressWarnings("unused") AbstractDataProvider<?> data) throws Exception {
        return json;
    }

    private void initData(AbstractDataProvider<?> data) throws Exception {
        updateData("init", data);
    }

    private void updateData(String command, AbstractDataProvider<?> data) throws Exception {
        StringBuilder b = new StringBuilder();
        data.encodeJSON(b);
        String d = customizeDataJSON(b.toString(), data);
        int count = countData(d);
        if(count < 0) {
            throw new ChartException("Invalid data from " + data.className() + ": " + d);
        }
        dataLengthMap.put(data.getSerial(), count);
        executeJS(command + "Data", data.getSerial(), "{\"d\":" + d + "}", datasetIndex(data));
    }

    private int datasetIndex(AbstractDataProvider<?> data) {
        int count = dataLengthMap.getOrDefault(data.getSerial(), 0);
        return datasetIndexMap.computeIfAbsent(count, k -> datasetIndexMap.size());
    }

    private static int countData(String d) {
        int i = d.indexOf('[');
        d = d.substring(i + 1);
        i = d.lastIndexOf(']');
        d = d.substring(0, i);
        int depth = 0, count = 1;
        boolean inQuote = false;
        char quoteChar = 0;
        char c;
        for (i = 0; i < d.length(); i++) {
            c = d.charAt(i);
            if (!inQuote) {
                if (c == '[' || c == '{' || c == '(') {
                    depth++;
                } else if (c == ']' || c == '}' || c == ')') {
                    depth--;
                } else if (c == '\'' || c == '"') {
                    inQuote = true;
                    quoteChar = c;
                } else if (depth == 0 && c == ',') {
                    count++;
                }
            } else {
                if (c == quoteChar && d.charAt(i - 1) != '\\') {
                    inQuote = false;
                }
            }
            if (depth < 0) {
                return -1;
            }
        }
        if (depth != 0) {
            return -1;
        }
        return count;
    }

    /**
     * Update the chart with modified data from the data provider.
     *
     * @param data Data to be updated.
     * @throws Exception if custom error is raised by {@link #customizeDataJSON(String, AbstractDataProvider)}.
     */
    public void updateData(AbstractDataProvider<?>... data) throws Exception {
        if(data != null) {
            for(AbstractDataProvider<?> d: data) {
                if(d != null) {
                    if(d.getSerial() > 0) {
                        updateData("update", d);
                    }
                }
            }
        }
    }

    /**
     * Update the chart with modified data from the data providers of a given set of data owners.
     *
     * @param dataOwners Data owners whose data to be updated.
     * @throws Exception if custom error is raised by {@link #customizeDataJSON(String, AbstractDataProvider)}.
     */
    public void updateData(HasData... dataOwners) throws Exception {
        if(dataOwners != null) {
            Set<AbstractDataProvider<?>> dataSet = new HashSet<>();
            for(HasData hd: dataOwners) {
                if(hd != null) {
                    hd.declareData(dataSet);
                }
            }
            for(AbstractDataProvider<?> d: dataSet) {
                if(d != null && d.getSerial() > 0) {
                    updateData("update", d);
                }
            }
        }
    }

    /**
     * Adhoc update of data - typically called internally from {@link DataChannel} instances.
     *
     * @param data Data to push or append.
     * @param command Command - should be "push" or "append".
     */
    void updateData(String data, String command) {
        if(neverUpdated) {
            return;
        }
        executeJS(command + "Data", data);
    }

    /**
     * Component encoder. (For internal use only).
     *
     * @author Syam
     */
    static class ComponentEncoder {

        final String label;
        final Class<? extends ComponentPart> partType;

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

        private Class<? extends ComponentPart> partClass() {
            if(!Axis.AxisWrapper.class.isAssignableFrom(partType)) {
                return partType;
            }
            if(XAxis.XAxisWrapper.class.isAssignableFrom(partType)) {
                return XAxis.class;
            }
            if(YAxis.YAxisWrapper.class.isAssignableFrom(partType)) {
                return YAxis.class;
            }
            if(AngleAxis.AngleAxisWrapper.class.isAssignableFrom(partType)) {
                return AngleAxis.class;
            }
            if(RadiusAxis.RadiusAxisWrapper.class.isAssignableFrom(partType)) {
                return RadiusAxis.class;
            }
            return partType;
        }

        private void encode(SOChart soChart, StringBuilder sb, List<? extends ComponentPart> components) {
            int renderingIndex = 0;
            int serial = -2;
            for (ComponentPart c: components) {
                if(partType.isAssignableFrom(c.getClass())) {
                    if(c.getSerial() < serial) {
                        break;
                    }
                    if(c.getSerial() == serial) {
                        continue;
                    }
                    c.setRenderingIndex(renderingIndex);
                    serial = c.getSerial();
                    if(renderingIndex == 0) {
                        if(sb.length() > 1) {
                            sb.append(",\n");
                        }
                        if("*".equals(label)) { // Self-rendering type
                            ++renderingIndex;
                            c.encodeJSON(sb);
                            return; // Only 1 expected.
                        }
                        sb.append('"').append(label).append("\":");
                        sb.append('[');
                    } else {
                        sb.append(',');
                    }
                    ++renderingIndex;
                    sb.append('{');
                    c.encodeJSON(sb);
                    ComponentPart.addComma(sb);
                    soChart.addCustomEncoding(c, sb);
                    ComponentPart.removeComma(sb);
                    if(c instanceof Chart chart) {
                        AbstractDataProvider<?> mainData = chart.mainData();
                        if(mainData != null) {
                            ComponentPart.addComma(sb);
                            sb.append("\"datasetIndex\":").append(soChart.datasetIndex(mainData));
                        }
                    }
                    sb.append('}');
                }
            }
            ComponentPart.addComma(sb);
            if(renderingIndex > 0) {
                soChart.addCustomEncoding(partClass(), sb);
            } else {
                int start = sb.length();
                sb.append('"').append(label).append("\":");
                sb.append('[');
                int end = sb.length();
                soChart.addCustomEncoding(partClass(), sb);
                if(sb.length() == end) {
                    sb.delete(start, end);
                    ComponentPart.removeComma(sb);
                    return;
                }
            }
            ComponentPart.removeComma(sb);
            sb.append(']');
        }
    }

    /**
     * Get the default color used by the chart component.
     *
     * @param index Index of the color.
     * @return Color.
     */
    public static Color getDefaultColor(int index) {
        return new Color(DefaultColors.colors[index % DefaultColors.colors.length]);
    }

    private static class DefaultColors extends ArrayList<AbstractColor> implements ComponentPart {

        private static final String[] colors = new String[] {
                "0000ff", "c23531", "2f4554", "61a0a8", "d48265", "91c7ae", "749f83",
                "ca8622", "bda29a", "6e7074", "546570", "c4ccd3"
        };
        private int serial;

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public void validate() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            sb.append("\"color\":[");
            int count = 0;
            boolean first = true;
            for(AbstractColor c: this) {
                if(c == null) {
                    continue;
                }
                if(first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(c);
                ++count;
            }
            AbstractColor c;
            for(int i = 0; count < 11; i++) {
                c = new Color(colors[i]);
                if(this.contains(c)) {
                    continue;
                }
                if(first) {
                    first = false;
                } else {
                    sb.append(',');
                }
                sb.append(c);
                ++count;
            }
            sb.append(']');
        }

        @Override
        public final int getSerial() {
            return serial;
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }
    }

    private static class DefaultTextStyle implements ComponentPart {

        private int serial;
        private final TextStyle textStyle = new TextStyle();

        @Override
        public long getId() {
            return 0;
        }

        @Override
        public void validate() {
        }

        @Override
        public void encodeJSON(StringBuilder sb) {
            TextStyle.OuterProperties op = new TextStyle.OuterProperties();
            textStyle.save(op);
            sb.append("\"textStyle\":{");
            ComponentPart.encode(sb, null, textStyle);
            sb.append('}');
        }

        @Override
        public final int getSerial() {
            return serial;
        }

        @Override
        public void setSerial(int serial) {
            this.serial = serial;
        }
    }

    private final class EventHandler {

        private final int id;
        private final ChartEventListener listener;
        private final ChartEventType type;
        private final ComponentPart componentPart;

        private EventHandler(ChartEventListener listener, ChartEventType type, ComponentPart componentPart) {
            int i = eventId.incrementAndGet();
            if(componentPart == null) {
                i = -i;
            }
            id = i;
            this.listener = listener;
            this.type = type;
            this.componentPart = componentPart;
            eventHandles.put(id, this);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof EventHandler eh && eh.id == id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @ClientCallable
    private void sendEvents(int id) {
        if(id == 0) {
            id = Integer.MIN_VALUE;
        }
        eventHandles.send(id);
    }

    private class EventHandles extends ConcurrentHashMap<Integer, EventHandler> {

        public void removeHandler(EventHandler eventHandler) {
            if(remove(eventHandler.id) != null) {
                if(!neverUpdated) {
                    executeJS("undefineSOEvent", eventHandler.id);
                }
            }
        }

        void send(int id) {
            int minId = eventHandles.keySet().stream().mapToInt(k -> k).filter(k -> k > id).min()
                    .orElse(Integer.MAX_VALUE);
            if(minId == Integer.MAX_VALUE) {
                chartUpdated();
                return;
            }
            EventHandler eh = eventHandles.get(minId);
            String parameters = eh.componentPart == null ? "" : eh.componentPart.getEventParameters(eh.type);
            executeJS("defineSOEvent", eh.id, eh.type.getName(), eh.type.getCategory(), parameters);
        }
    }
}