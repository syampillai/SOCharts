package com.storedobject.chart;

/**
 * Represents a data structure for storing and managing boxplot data.
 * This class extends {@code AbstractData} to handle a list of {@code BoxplotData.Boxplot} objects,
 * which represent individual boxplot data points.
 *
 * @author Syam
 */
public class BoxplotData extends AbstractData<BoxplotData.Boxplot> {

    /**
     * Constructs a {@code BoxplotData} object with the specified boxplot data points.
     *
     * @param data An array of {@code Boxplot} objects that represent the data points
     *             for the boxplots to be managed by this instance. Each {@code Boxplot}
     *             consists of values for lower, Q1, median, Q3, and upper.
     */
    public BoxplotData(Boxplot... data) {
        super(DataType.OBJECT, data);
    }

    /**
     * Represents a boxplot data record with the key statistical parts of a boxplot.
     * The components include lower bound, first quartile (Q1), median, third quartile (Q3),
     * and upper bound.
     *
     * This record provides an immutable representation of these values.
     *
     * @param lower  The lower bound of the data.
     * @param q1     The first quartile (Q1), representing the 25th percentile of the data.
     * @param median The median, representing the 50th percentile of the data.
     * @param q3     The third quartile (Q3), representing the 75th percentile of the data.
     * @param upper  The upper bound of the data.
     *
     * @author Syam
     */
    public record Boxplot(Number lower, Number q1, Number median, Number q3, Number upper) {

        @SuppressWarnings("NullableProblems")
        @Override
        public String toString() {
            return "[" + lower + "," + q1 + "," + median + "," + q3 + "," + upper + "]";
        }
    }
}
