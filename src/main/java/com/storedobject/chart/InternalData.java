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

import java.util.stream.Stream;

/**
 * A marker interface to combine the interfaces {@link AbstractDataProvider} and {@link InternalDataProvider}.
 * (End-developers should not use this anywhere in their code).
 *
 * @author Syam
 */
public interface InternalData<T> extends AbstractDataProvider<T>, InternalDataProvider {

	/**
	 * Wrap an {@link AbstractDataProvider} to create an instance of an {@link InternalData}.
	 *
	 * @param dataProvider
	 *            Data provider to wrap.
	 * @param <T>
	 *            Type of the data provider.
	 * @return Wrapped data provider that is "internal".
	 */
	static <T> InternalData<T> wrap(final AbstractDataProvider<T> dataProvider) {
		return new WrappedDataProvider<>(dataProvider);
	}

	/**
	 * A wrapper class to convert any instance of {@link AbstractDataProvider} to an instance of
	 * {@link InternalDataProvider}.
	 *
	 * @param <T>
	 *            Type of the data provider.
	 * @author Syam
	 */
	public static class WrappedDataProvider<T> implements InternalData<T> {

		private AbstractDataProvider<T> dataProvider;

		public AbstractDataProvider<T> getDataProvider() {
			return dataProvider;
		}

		public void setDataProvider(final AbstractDataProvider<T> dataProvider) {
			this.dataProvider = dataProvider;
		}

		public WrappedDataProvider(final AbstractDataProvider<T> dataProvider) {
			this.dataProvider = dataProvider;
		}

		@Override
		public Stream<T> stream() {
			return dataProvider.stream();
		}

		@Override
		public DataType getDataType() {
			return dataProvider.getDataType();
		}

		@Override
		public void setSerial(final int serial) {
			dataProvider.setSerial(serial);
		}

		@Override
		public int getSerial() {
			return dataProvider.getSerial();
		}
	}
}
