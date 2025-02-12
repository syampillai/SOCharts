/*
 *  Copyright yam Pillai
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

/**
 * A basic data provider that can be extended to create customized data providers.
 *
 * @param <T> Data type.
 *
 * @author Syam
 */
public abstract class BasicDataProvider<T> implements AbstractDataProvider<T> {

    private int serial = -1;

    @Override
    public final void setSerial(int serial) {
        this.serial = serial;
    }

    @Override
    public final int getSerial() {
        return serial;
    }

    @Override
    public DataType getDataType() {
        return DataType.OBJECT;
    }
}
