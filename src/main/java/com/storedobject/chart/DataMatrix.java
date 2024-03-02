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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Represents a matrix of data. {@link Data} can be added as rows to the matrix. This class provides methods to
 * access rows and columns of the matrix as {@link DataProvider}s.
 * <pre>
 *     -- Fruit Production in Million Tons --
 *     Fruits     | Apple | Orange | Grapes |
 *     Years
 *     2011       | 23.4  | 34.5   | 56.7   |
 *     2012       | 25.2  | 36.2   | 60.1   |
 *     2013       | 20.9  | 39.5   | 59.7   |
 *     2014       | 23.7  | 40.1   | 66.7   |
 *
 *     setName("Fruit Production in Million Tons");
 *     setColumnName("Fruits");
 *     setColumnNames("Apple", "Orange", "Grapes");
 *     setRowName("Years");
 *     setRowNames("2011", "2012", "2013", "2014");
 *     addRow(23.4, 34.5, 56.7);
 *     addRow(25.2, 36.2, 60.1);
 *     addRow(20.9, 39.5, 59.7);
 *     addRow(23.7, 40.1, 66.7);
 * </pre>
 *
 * @author Syam
 */
public class DataMatrix {

    private final List<Data> dataList = new ArrayList<>();
    private final List<DataProvider> rowData = new ArrayList<>();
    private final List<DataProvider> columnData = new ArrayList<>();
    private String name, columnName, rowName;
    private CategoryData columnNames, rowNames;
    private CategoryDataProvider columnNameGenerator, rowNameGenerator;

    /**
     * Constructor.
     *
     * @param data Data rows to add.
     */
    public DataMatrix(Data... data) {
        this(null, data);
    }

    /**
     * Constructor.
     *
     * @param name Name of the data set.
     * @param data Data rows to add.
     */
    public DataMatrix(String name, Data... data) {
        setName(name);
        addRow(data);
    }

    /**
     * Add rows of data.
     *
     * @param data Data to add.
     */
    public void addRow(Data... data) {
        if(data != null) {
            for(Data d: data) {
                if(d != null) {
                    dataList.add(d);
                }
            }
        }
    }

    /**
     * Add a row of data.
     *
     * @param data Data to add.
     */
    public void addRow(Number... data) {
        if(data != null && data.length > 0) {
            dataList.add(new Data(data));
        }
    }

    /**
     * Add a row of data.
     *
     * @param data Data to add.
     */
    public void addRow(double... data) {
        if(data != null && data.length > 0) {
            dataList.add(new Data(data));
        }
    }

    /**
     * Remove data rows.
     *
     * @param data Data to remove.
     */
    public void removeRow(Data... data) {
        if(data != null) {
            for(Data d: data) {
                if(d != null) {
                    dataList.remove(d);
                }
            }
        }
    }

    /**
     * Remove data at the given index.
     *
     * @param rowIndex Row index.
     */
    public void removeRow(int rowIndex) {
        if(rowIndex >= 0 && rowIndex <= dataList.size()) {
            dataList.remove(rowIndex);
        }
    }

    /**
     * Get the row count.
     *
     * @return Row count.
     */
    public int getRowCount() {
        return dataList.size();
    }

    /**
     * Get the column count.
     *
     * @return Column count.
     */
    public int getColumnCount() {
        if(dataList.isEmpty()) {
            return 0;
        }
        int c = Integer.MAX_VALUE;
        for(Data d: dataList) {
            c = Math.min(d.size(), c);
        }
        return c;
    }

    /**
     * Set column names.
     * @param columnNames Column names.
     */
    public void setColumnNames(String... columnNames) {
        this.columnNames = new CategoryData(columnNames);
    }

    /**
     * Set column names.
     * @param columnNames Column names.
     */
    public void setColumnNames(CategoryData columnNames) {
        this.columnNames = columnNames;
    }

    /**
     * Set row names.
     * @param rowNames Row names.
     */
    public void setRowNames(String... rowNames) {
        this.rowNames = new CategoryData(rowNames);
    }

    /**
     * Set row names.
     * @param rowNames Row names.
     */
    public void setRowNames(CategoryData rowNames) {
        this.rowNames = rowNames;
    }

    /**
     * Get column names as a category data provider.
     *
     * @return Column names as category data provider.
     */
    public CategoryDataProvider getColumnNames() {
        if(columnNameGenerator == null) {
            columnNameGenerator = new ColumnNames();
        }
        return columnNameGenerator;
    }

    /**
     * Get row names as a category data provider.
     *
     * @return Row names as category data provider.
     */
    public CategoryDataProvider getRowNames() {
        return getRowNames(-1);
    }

    /**
     * Get row names as a category data provider.
     *
     * @param limit Limit the number of entries returned.
     * @return Row names as category data provider.
     */
    public CategoryDataProvider getRowNames(int limit) {
        if(rowNameGenerator == null) {
            rowNameGenerator = new RowNames(limit);
        }
        return rowNameGenerator;
    }

    /**
     * Get a specific row as data provider.
     *
     * @param row Row index.
     * @return Row as data provider.
     */
    public DataProvider getRow(int row) {
        return getRow(row, -1);
    }

    /**
     * Get a specific row as data provider.
     *
     * @param row Row index.
     * @param limit Limit the number of entries returned.
     * @return Row as data provider.
     */
    public DataProvider getRow(int row, int limit) {
        if(row >= 0 && row <= dataList.size()) {
            if(row < rowData.size()) {
                return rowData.get(row);
            }
            if(row >= dataList.size()) {
                return null;
            }
            while(rowData.size() <= row) {
                rowData.add(new RowData(rowData.size(), limit));
            }
            return rowData.get(rowData.size() - 1);
        }
        return null;
    }

    /**
     * Get a specific column as data provider.
     *
     * @param column Column index.
     * @return Column as data provider.
     */
    public DataProvider getColumn(int column) {
        return getColumn(column, -1);
    }

    /**
     * Get a specific column as data provider.
     *
     * @param column Column index.
     * @param limit Limit the number of entries returned.
     * @return Column as data provider.
     */
    public DataProvider getColumn(int column, int limit) {
        if(column < 0) {
            return null;
        }
        if(column < columnData.size()) {
            return columnData.get(column);
        }
        if(column >= getColumnCount()) {
            return null;
        }
        while(columnData.size() <= column) {
            columnData.add(new ColumnData(columnData.size(), limit));
        }
        return columnData.get(columnData.size() - 1);
    }

    /**
     * Get the name of a particular row.
     *
     * @param rowIndex Row index.
     * @return Name.
     */
    public String getRowName(int rowIndex) {
        String name = null;
        if(rowIndex >= 0) {
            if(rowNames != null && rowNames.size() > rowIndex) {
                name = rowNames.get(rowIndex);
            } else if(dataList.size() > rowIndex) {
                name = dataList.get(rowIndex).getName();
            }
        }
        return name == null ? ("Row " + (rowIndex + 1)) : name;
    }

    /**
     * Get the name of a particular column.
     *
     * @param columnIndex Column index.
     * @return Name.
     */
    public String getColumnName(int columnIndex) {
        String name = null;
        if(columnIndex >= 0) {
            if (columnNames != null && columnNames.size() > columnIndex) {
                name = columnNames.get(columnIndex);
            }
        }
        return name == null ? ("Column " + (columnIndex + 1)) : name;
    }

    /**
     * Get the name of the column data.
     *
     * @return Name of the column data.
     */
    public String getColumnDataName() {
        return columnName;
    }

    /**
     * Set the name of the column data.
     *
     * @param columnName Name of the column data.
     */
    public void setColumnDataName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * Get the name of the row data.
     *
     * @return Name of the row data.
     */
    public String getRowDataName() {
        return rowName;
    }

    /**
     * Set the name of the row data.
     *
     * @param rowName Name of the row data.
     */
    public void setRowDataName(String rowName) {
        this.rowName = rowName;
    }

    /**
     * Get name of the data set.
     *
     * @return Name.
     */
    public String getName() {
        return name == null ? "Data" : name;
    }

    /**
     * Set name of the data set.
     *
     * @param name Name.
     */
    public void setName(String name) {
        this.name = name;
    }

    private abstract static class BaseData {

        private int serial = -1;

        public long getId() {
            return -1L;
        }

        public final int getSerial() {
            return serial;
        }

        public final void setSerial(int serial) {
            this.serial = serial;
        }
    }

    private class ColumnNames extends BaseData implements CategoryDataProvider {

        private ColumnNames() {
        }

        @Override
        public Stream<String> stream() {
            return Stream.generate(new ColumnNameGenerator()).limit(getColumnCount());
        }

        @Override
        public String getName() {
            String name = columnName;
            if(name == null) {
                if(columnNames != null) {
                    name = columnNames.getName();
                }
            }
            if(name == null) {
                name = "Column Data";
            }
            return name;
        }

        @Override
        public void validate() {
        }

        private class ColumnNameGenerator implements Supplier<String> {

            private int index = 0;

            @Override
            public String get() {
                return getColumnName(index++);
            }
        }
    }

    private class RowNames extends BaseData implements CategoryDataProvider {

        private final int limit;

        private RowNames(int limit) {
            this.limit = limit;
        }

        @Override
        public Stream<String> stream() {
            int l = limit;
            if(l <= 0) {
                l = getRowCount();
            } else {
                l = Math.min(l, getRowCount());
            }
            return Stream.generate(new RowNameGenerator()).limit(l);
        }

        @Override
        public String getName() {
            String name = rowName;
            if(name == null) {
                if(rowNames != null) {
                    name = rowNames.getName();
                }
            }
            if(name == null) {
                name = "Row Data";
            }
            return name;
        }

        @Override
        public void validate() {
        }

        private class RowNameGenerator implements Supplier<String> {

            private int row = 0;

            @Override
            public String get() {
                return getRowName(row++);
            }
        }
    }

    private class ColumnData extends BaseData implements DataProvider {

        private final int index;
        private final int limit;

        private ColumnData(int index, int limit) {
            this.index = index;
            this.limit = limit;
        }

        @Override
        public String getName() {
            return getColumnName(index);
        }

        @Override
        public Stream<Number> stream() {
            int l = limit;
            if(l <= 0) {
                l = dataList.size();
            } else {
                l = Math.min(l, dataList.size());
            }
            return Stream.generate(new Generator()).limit(l);
        }

        @Override
        public void validate() throws ChartException {
            if (index < 0) {
                throw new ChartException("Index can't be negative");
            }
            for (Data data : dataList) {
                if (index >= data.size()) {
                    String m = data.getName();
                    if (m == null) {
                        m = "Data[" + dataList.indexOf(data) + "]";
                    }
                    throw new ChartException("No data at index " + index + " in " + m);
                }
            }
        }

        private class Generator implements Supplier<Number> {

            private int row = 0;

            @Override
            public Number get() {
                if(row >= dataList.size()) {
                    return null;
                }
                return dataList.get(row++).get(index);
            }
        }
    }

    private class RowData extends BaseData implements DataProvider {

        private final int row;
        private final int limit;

        private RowData(int row, int limit) {
            this.row = row;
            this.limit = limit;
        }

        @Override
        public Stream<Number> stream() {
            Stream<Number> s = dataList.get(row).stream();
            return limit <= 0 ? s : s.limit(limit);
        }

        @Override
        public String getName() {
            return getRowName(row);
        }

        @Override
        public void validate() {
        }
    }
}
