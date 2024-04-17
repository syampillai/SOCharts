package com.storedobject.chart;

/**
 * Represents a chart event.
 *
 * @author Asnel Christian (Modified by Syam)
 */
public class SOEvent {

	private final String name;
	private final String target;

	/**
	 * Constructor.
	 *
	 * @param name
	 *            Name.
	 * @param target
	 *            Target.
	 */
	public SOEvent(final String name, final String target) {
		this.name = name;
		this.target = target;
	}

	/**
	 * Get the event name.
	 *
	 * @return Event name.
	 */
	public String getEvent() {
		return name;
	}

	/**
	 * Get the target.
	 *
	 * @return Target.
	 */
	public String getTarget() {
		return target;
	}

	private String key() {
		return String.format("%s%s", name, target);
	}

	@Override
	public int hashCode() {
		return this.key().hashCode();
	}

	@Override
	public boolean equals(final Object that) {
		return that instanceof SOEvent && this.key().equals(((SOEvent) that).key());
	}
}
