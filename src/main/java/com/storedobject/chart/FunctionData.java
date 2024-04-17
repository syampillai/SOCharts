package com.storedobject.chart;

import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.IntToDoubleFunction;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Representation of data as a mathematical function of another data or stream of values. The resulting data is always
 * of {@link Double} type.
 *
 * @author Syam
 */
public class FunctionData implements AbstractDataProvider<Double> {

	private int serial = -1;
	private String name;
	private final Supplier<Stream<?>> streamSupplier;
	private final ToDoubleFunction<?> converter;

	private <T> FunctionData(final Supplier<Stream<T>> streamSupplier, final ToDoubleFunction<T> converter) {
		this.streamSupplier = streamSupplier::get;
		this.converter = converter;
	}

	/**
	 * Create a {@link FunctionData} that generates values by converting a stream of another set of values.
	 *
	 * @param xValues
	 *            Data to convert to double values.
	 * @param converter
	 *            Converter function.
	 */
	public static <T> FunctionData create(final AbstractDataProvider<T> xValues, final ToDoubleFunction<T> converter) {
		return new FunctionData(xValues::stream, converter);
	}

	/**
	 * Create a {@link FunctionData} that generates values by converting a stream of another set of values.
	 *
	 * @param streamSupplier
	 *            Stream supplier that supplies the stream of data to convert to double values.
	 * @param converter
	 *            Converter function.
	 */
	public static <T> FunctionData create(final Supplier<Stream<T>> streamSupplier,
			final ToDoubleFunction<T> converter) {
		return new FunctionData(streamSupplier, converter);
	}

	/**
	 * Create a {@link FunctionData} that generates a sequence of integer equivalents as its output.
	 *
	 * @param startingValue
	 *            Starting value of the integer.
	 * @param endingValue
	 *            Ending value of the integer.
	 * @return Function data created.
	 */
	public static FunctionData create(final int startingValue, final int endingValue) {
		return create(startingValue, endingValue, startingValue < endingValue ? 1 : -1);
	}

	/**
	 * Create a {@link FunctionData} that generates a sequence of integer equivalents as its output.
	 *
	 * @param startingValue
	 *            Starting value of the integer.
	 * @param endingValue
	 *            Ending value of the integer.
	 * @param step
	 *            Incrementing step (Could be negative and in that case, startingValue > endingValue).
	 * @return Function data created.
	 */
	public static FunctionData create(final int startingValue, final int endingValue, final int step) {
		return create((IntToDoubleFunction) i -> i, startingValue, endingValue, step);
	}

	/**
	 * Create a mathematical function that generates {@link FunctionData} for a given range of integer equivalents.
	 *
	 * @param converter
	 *            Converter function.
	 * @param startingValue
	 *            Starting value of the integer.
	 * @param endingValue
	 *            Ending value of the integer.
	 * @return Function data created.
	 */
	public static FunctionData create(final IntToDoubleFunction converter, final int startingValue,
			final int endingValue) {
		return create(converter, startingValue, endingValue, startingValue < endingValue ? 1 : -1);
	}

	/**
	 * Create a mathematical function that generates {@link FunctionData} for a given range of integers.
	 *
	 * @param converter
	 *            Converter function.
	 * @param startingValue
	 *            Starting value of the integer.
	 * @param endingValue
	 *            Ending value of the integer.
	 * @param step
	 *            Incrementing step (Could be negative and in that case, startingValue > endingValue).
	 * @return Function data created.
	 */
	public static FunctionData create(final IntToDoubleFunction converter, final int startingValue,
			final int endingValue, final int step) {
		IntPredicate hasNext;
		if (step < 0) {
			hasNext = i -> i > endingValue;
		} else {
			hasNext = i -> i < endingValue;
		}
		return new FunctionData(() -> IntStream.iterate(startingValue, hasNext, i -> i + step).boxed(),
				converter::applyAsDouble);
	}

	/**
	 * Create a {@link FunctionData} that generates a sequence of double values as its output.
	 *
	 * @param startingValue
	 *            Starting value.
	 * @param endingValue
	 *            Ending value.
	 * @param step
	 *            Incrementing step (Could be negative and in that case, startingValue > endingValue).
	 * @return Function data created.
	 */
	public static FunctionData create(final double startingValue, final double endingValue, final double step) {
		return create(d -> d, startingValue, endingValue, step);
	}

	/**
	 * Create a mathematical function that generates {@link FunctionData} for a given range of double values.
	 *
	 * @param startingValue
	 *            Starting value.
	 * @param endingValue
	 *            Ending value.
	 * @return Function data created.
	 */
	public static FunctionData create(final DoubleFunction<Double> converter, final double startingValue,
			final double endingValue) {
		return create(converter, startingValue, endingValue, startingValue < endingValue ? 1 : -1);
	}

	/**
	 * Create a mathematical function that generates {@link FunctionData} for a given range of double values.
	 *
	 * @param converter
	 *            Converter function.
	 * @param startingValue
	 *            Starting value.
	 * @param endingValue
	 *            Ending value.
	 * @param step
	 *            Incrementing step (Could be negative and in that case, startingValue > endingValue).
	 * @return Function data created.
	 */
	public static FunctionData create(final DoubleFunction<Double> converter, final double startingValue,
			final double endingValue, final double step) {
		DoublePredicate hasNext;
		if (step < 0) {
			hasNext = d -> d > endingValue;
		} else {
			hasNext = d -> d < endingValue;
		}
		return new FunctionData(() -> DoubleStream.iterate(startingValue, hasNext, d -> d + step).boxed(),
				converter::apply);
	}

	@Override
	public final Stream<Double> stream() {
		// noinspection unchecked,rawtypes
		return new Streamer(streamSupplier.get(), converter).streamIt();
	}

	private class Streamer<T> {

		private Stream<T> stream;
		private ToDoubleFunction<T> converter;

		public Streamer(final Stream<T> stream, final ToDoubleFunction<T> converter) {
			this.stream = stream;
			this.converter = converter;
		}

		public Stream<T> getStream() {
			return stream;
		}

		public void setStream(final Stream<T> stream) {
			this.stream = stream;
		}

		public ToDoubleFunction<T> getConverter() {
			return converter;
		}

		public void setConverter(final ToDoubleFunction<T> converter) {
			this.converter = converter;
		}

		private Stream<Double> streamIt() {
			return stream.map(converter::applyAsDouble);
		}
	}

	/**
	 * Get the data type.
	 *
	 * @return Data type.
	 */
	@Override
	public final DataType getDataType() {
		return DataType.NUMBER;
	}

	@Override
	public final int getSerial() {
		return serial;
	}

	@Override
	public final void setSerial(final int serial) {
		this.serial = serial;
	}

	/**
	 * Get the name of this data set.
	 *
	 * @return Name.
	 */
	@Override
	public final String getName() {
		return name;
	}

	/**
	 * Set a name for this data set.
	 *
	 * @param name
	 *            Name to set.
	 */
	@Override
	public void setName(final String name) {
		this.name = name;
	}
}
