/*
 *  Copyright 2019-2021 Syam Pillai
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Class to represent a list of activities (instances of {@link Activity}).
 * <p>
 * {@link ActivityChart} uses data from this class.
 * </p>
 * <p>
 * An {@link ActivityList} consists of instances of {@link Activity} and each {@link Activity} belongs to an
 * {@link ActivityGroup}. Instances of {@link Activity} can be created via
 * {@link #createActivity(ActivityGroup, String, LocalDateTime, int)} or
 * {@link #createActivity(ActivityGroup, String, LocalDateTime, LocalDateTime)}. Alternatively, a {@link Activity} can
 * be created under an {@link ActivityGroup} via {@link ActivityGroup#createActivity(String, LocalDateTime, int)} or
 * {@link ActivityGroup#createActivity(ActivityGroup, String, LocalDateTime, LocalDateTime)}.
 * </p>
 *
 * @author Syam
 */
public class ActivityList extends AbstractProject {

	private final List<ActivityGroup> activityGroups = new ArrayList<>();
	private boolean checked = false;

	/**
	 * Constructor for a date-based event list.
	 */
	public ActivityList() {
		this(null);
	}

	/**
	 * Constructor.
	 *
	 * @param durationType
	 *            Type of duration to be used for this {@link ActivityList}. (Note: {@link ChronoUnit#ERAS} and
	 *            {@link ChronoUnit#FOREVER} are not supported and if used, will be considered as
	 *            {@link ChronoUnit#MILLIS}).
	 */
	public ActivityList(final ChronoUnit durationType) {
		super(durationType);
	}

	/**
	 * Validate dependencies. A {@link ChartException} is thrown if any overlapping activity is found with in the same
	 * group.
	 * <p>
	 * Important: This method removes any "activity group" from this list that does not have any "activity".
	 * </p>
	 */
	@Override
	void validateConstraints() throws ChartException {
		if (checked) {
			return;
		}
		super.validateConstraints();
		arrange();
		for (final ActivityGroup activityGroup : activityGroups) {
			activityGroup.check();
		}
		checked = true;
	}

	private void arrange() {
		activityGroups.removeIf(tg -> tg.activities.isEmpty());
		activityGroups.forEach(ActivityGroup::sort);
	}

	@Override
	public boolean isEmpty() {
		return activityGroups.isEmpty();
	}

	private boolean contains(ProjectActivity instance) {
		if (instance instanceof Activity) {
			final Activity activity = (Activity) instance;
			if (activity.group == null) {
				return false;
			}
			instance = activity.group;
		}
		return activityGroups.contains(instance);
	}

	/**
	 * Create and add a {@link ActivityGroup} to the {@link ActivityList}.
	 *
	 * @param name
	 *            Name of the activity group.
	 * @return Activity group created.
	 */
	public ActivityGroup createActivityGroup(final String name) {
		final ActivityGroup activityGroup = new ActivityGroup(name);
		activityGroups.add(0, activityGroup);
		return activityGroup;
	}

	/**
	 * Create and add a {@link Activity} to the {@link ActivityList}.
	 *
	 * @param activityGroup
	 *            Group to which the {@link Activity} to be added.
	 * @param activityName
	 *            Name of the {@link Activity}.
	 * @param start
	 *            Start.
	 * @param duration
	 *            Duration of the {@link Activity}. (Should be grater than 0).
	 * @return Activity created. (Returns <code>null</code> if the {@link ActivityGroup} passed doesn't belong to this).
	 */
	public Activity createActivity(final ActivityGroup activityGroup, final String activityName,
			final LocalDateTime start, final int duration) {
		if (activityGroups.contains(activityGroup)) {
			return new Activity(activityGroup, activityName, start, duration);
		}
		return null;
	}

	/**
	 * Create and add a {@link Activity} to the {@link ActivityList}.
	 *
	 * @param activityGroup
	 *            Group to which the {@link Activity} to be added.
	 * @param activityName
	 *            Name of the {@link Activity}.
	 * @param start
	 *            Start.
	 * @param end
	 *            End.
	 * @return Activity created. (Returns <code>null</code> if the {@link ActivityGroup} passed doesn't belong to this).
	 */
	public Activity createActivity(final ActivityGroup activityGroup, final String activityName,
			final LocalDateTime start, final LocalDateTime end) {
		if (activityGroups.contains(activityGroup)) {
			return new Activity(activityGroup, activityName, start, end);
		}
		return null;
	}

	private void deleteActivity(final Activity activity) {
		if (activity == null) {
			return;
		}
		checked = false;
		activity.group.activities.remove(activity);
	}

	private void deleteGroup(final ActivityGroup activityGroup) {
		if (activityGroup == null) {
			return;
		}
		checked = false;
		activityGroups.remove(activityGroup);
		while (!activityGroup.activities.isEmpty()) {
			deleteActivity(activityGroup.activities.get(0));
		}
	}

	/**
	 * Delete {@link ActivityGroup}s from the {@link ActivityList}. (All {@link Activity}s and dependencies related to
	 * these groups will be dropped).
	 *
	 * @param activityGroups
	 *            {@link ActivityGroup}s to delete.
	 */
	public void delete(final ActivityGroup... activityGroups) {
		if (activityGroups != null) {
			for (final ActivityGroup activityGroup : activityGroups) {
				deleteGroup(activityGroup);
			}
		}
	}

	/**
	 * Delete {@link Activity}s from the {@link ActivityList}. (All dependencies related to these activities will be
	 * dropped).
	 *
	 * @param activities
	 *            {@link Activity}s to delete.
	 */
	public void delete(final Activity... activities) {
		if (activities != null) {
			for (final Activity activity : activities) {
				deleteActivity(activity);
			}
		}
	}

	@Override
	public final void setStart(final LocalDateTime start) {
		checked = false;
		super.setStart(start);
	}

	@Override
	public final LocalDateTime getStart() {
		LocalDateTime start = super.getStart();
		if (start == null) {
			arrange();
			if (!isEmpty()) {
				start = activityGroups.get(0).getStart();
			}
		}
		return start;
	}

	@Override
	public final LocalDateTime getEnd() {
		final LocalDateTime start = getStart();
		LocalDateTime end = start, e;
		for (final ActivityGroup activityGroup : activityGroups) {
			for (final Activity activity : activityGroup.activities) {
				e = activity.getEnd();
				if (e.isAfter(end)) {
					end = e;
				}
			}
		}
		return end;
	}

	private static int compare(final ProjectActivity a1, final ProjectActivity a2) {
		return a2.getStart().compareTo(a1.getStart());
	}

	@Override
	public final int getRowCount() {
		return activityGroups.size();
	}

	/**
	 * Get the {@link ActivityGroup} of this {@link ActivityList}.
	 *
	 * @param groupIndex
	 *            Index of the activity group to be retrieved.
	 * @return {@link ActivityGroup}.
	 */
	public final ActivityGroup getEventGroup(final int groupIndex) {
		return activityGroups.get(groupIndex);
	}

	/**
	 * Get the number of {@link Activity} belonging to this group.
	 *
	 * @return Activity count.
	 */
	public final int getGroupCount() {
		return activityGroups.size();
	}

	/**
	 * An abstract base class for the representation of an Activity or Activity Group.
	 *
	 * @author Syam
	 */
	abstract class ProjectActivity extends AbstractTask {

		private final int duration;

		/**
		 * Constructor.
		 *
		 * @param name
		 *            Name of the activity/group.
		 */
		protected ProjectActivity(final String name, final LocalDateTime start, final int duration) {
			setName(name);
			this.start = start;
			this.duration = Math.max(duration, 0);
		}

		/**
		 * Constructor.
		 *
		 * @param name
		 *            Name of the activity/group.
		 */
		protected ProjectActivity(final String name, final LocalDateTime start, final LocalDateTime end) {
			setName(name);
			this.start = start;
			duration = (int) getDurationType().between(start, end);
		}

		/**
		 * Get the name.
		 *
		 * @return Current name.
		 */
		@Override
		public final String getName() {
			final String name = super.getName();
			if (name != null && !name.isEmpty()) {
				return "DEFAULT".equals(name) ? "" : name;
			}
			return (this instanceof Activity ? "Activity" : "Group") + ": " + getId();
		}

		/**
		 * Get the duration.
		 *
		 * @return Duration (in {@link #getDurationType()}).
		 */
		public int getDuration() {
			return duration;
		}

		/**
		 * Get the end.
		 *
		 * @return End.
		 */
		@Override
		public final LocalDateTime getEnd() {
			return getStart().plus(getDuration(), getDurationType());
		}

		/**
		 * Start used by renderers may be different from the normal start. (For internal use only).
		 *
		 * @return Start for the renderers.
		 */
		@Override
		public LocalDateTime renderStart() {
			if (getDuration() == 0) {
				return start.minus(1, getDurationType());
			}
			return start;
		}

		abstract void check() throws ChartException;
	}

	/**
	 * Represents a group of {@link Activity}s.
	 *
	 * @author Syam
	 */
	public class ActivityGroup extends ProjectActivity {

		private final List<Activity> activities = new ArrayList<>();

		/**
		 * Constructor.
		 *
		 * @param name
		 *            Name.
		 */
		ActivityGroup(final String name) {
			super(name, LocalDateTime.now(), 0);
		}

		/**
		 * Create and add a {@link Activity} to this group.
		 *
		 * @param name
		 *            Name of the activity.
		 * @param start
		 *            Start.
		 * @param duration
		 *            Duration of the {@link Activity}. (Should be grater than 0).
		 * @return Activity created.
		 */
		public Activity createActivity(final String name, final LocalDateTime start, final int duration) {
			return ActivityList.this.createActivity(this, name, start, duration);
		}

		/**
		 * Create and add a {@link Activity} to this group.
		 *
		 * @param name
		 *            Name of the activity.
		 * @param start
		 *            Start.
		 * @param end
		 *            End.
		 * @return Activity created.
		 */
		public Activity createActivity(final String name, final LocalDateTime start, final LocalDateTime end) {
			return ActivityList.this.createActivity(this, name, start, end);
		}

		@Override
		public Color getColor() {
			Color color = super.getColor();
			if (color == null) {
				setColor(color = SOChart.getDefaultColor(activityGroups.indexOf(this)));
			}
			return color;
		}

		private void sort() {
			activities.sort(ActivityList::compare);
		}

		@Override
		public final int getDuration() {
			final LocalDateTime start = activities.get(0).getStart(),
					end = activities.get(activities.size() - 1).getEnd();
			return (int) getDurationType().between(start, end);
		}

		@Override
		public final LocalDateTime getStart() {
			return activities.get(0).getStart();
		}

		/**
		 * Get an {@link Activity} from this group.
		 *
		 * @param activityIndex
		 *            Index of the activity to be retrieved.
		 * @return {@link Activity}.
		 */
		public final Activity getActivity(final int activityIndex) {
			return activities.get(activityIndex);
		}

		/**
		 * Get the {@link Activity} count belonging to this group.
		 *
		 * @return Activity count.
		 */
		public final int getActivityCount() {
			return activities.size();
		}

		@Override
		public boolean isCompleted() {
			return activities.stream().allMatch(ProjectActivity::isCompleted);
		}

		@Override
		public double getCompleted() {
			return 0;
		}

		@Override
		void check() throws ChartException {
			final Activity p = activities.get(0);
			Activity c;
			p.check();
			for (int i = 1; i < activities.size(); i++) {
				c = activities.get(i);
				c.check();
				if (p.getStart().isBefore(c.getEnd())) {
					throw new ChartException(p + " overlaps with " + c);
				}
			}
		}
	}

	/**
	 * Represents an activity in a {@link ActivityList}.
	 *
	 * @author Syam
	 */
	public class Activity extends ProjectActivity {

		private final ActivityGroup group;
		private double completed = 0;

		/**
		 * Constructor.
		 *
		 * @param activityGroup
		 *            Activity group to which this {@link Activity} belongs to.
		 * @param name
		 *            Name.
		 * @param start
		 *            Start.
		 * @param duration
		 *            Duration of the {@link Activity}. (A duration of zero denotes a {@link ActivityList} milestone).
		 */
		Activity(final ActivityGroup activityGroup, final String name, final LocalDateTime start, final int duration) {
			super(name, trim(start), duration);
			group = activityGroup;
			group.activities.add(this);
		}

		/**
		 * Constructor.
		 *
		 * @param activityGroup
		 *            Activity group to which this {@link Activity} belongs to.
		 * @param name
		 *            Name.
		 * @param start
		 *            Start.
		 * @param end
		 *            End.
		 */
		Activity(final ActivityGroup activityGroup, final String name, final LocalDateTime start,
				final LocalDateTime end) {
			super(name, trim(start), end);
			group = activityGroup;
			group.activities.add(this);
		}

		/**
		 * Create a new activity following this activity.
		 *
		 * @param name
		 *            Name of the new activity.
		 * @param gap
		 *            Gap between this activity and the new activity. (Must be greater than 0).
		 * @param duration
		 *            Duration of the new activity.
		 * @return New activity.
		 */
		public Activity createNext(final String name, final int gap, final int duration) {
			return group.createActivity(name, getEnd().plus(gap, getDurationType()), duration);
		}

		/**
		 * Create a new activity immediately following this activity.
		 *
		 * @param name
		 *            Name of the new activity.
		 * @param duration
		 *            Duration of the new activity.
		 * @return New activity.
		 */
		public Activity createNext(final String name, final int duration) {
			return createNext(name, 1, duration);
		}

		/**
		 * Set completion percentage.
		 *
		 * @param completed
		 *            Percentage completed.
		 */
		public void setCompleted(final double completed) {
			this.completed = Math.min(Math.max(0, completed), 100);
		}

		@Override
		public final double getCompleted() {
			return completed;
		}

		@Override
		public final boolean isMilestone() {
			return getDuration() == 0;
		}

		/**
		 * Get the {@link ActivityGroup} this activity belongs to.
		 *
		 * @return {@link ActivityGroup}.
		 */
		public final ActivityGroup getGroup() {
			return group;
		}

		@Override
		public boolean isCompleted() {
			return completed >= 100 || getDuration() == 0;
		}

		@Override
		public Color getColor() {
			final Color color = super.getColor();
			return color == null ? group.getColor() : color;
		}

		@Override
		void check() throws ChartException {
			if (getDuration() <= 0) {
				throw new ChartException("Invalid duration in " + this);
			}
		}

		@Override
		public String toString() {
			final Function<LocalDateTime, String> timeConverter = getTooltipTimeFormat();
			return "[" + getName() + " (" + timeConverter.apply(getStart()) + " - " + timeConverter.apply(getEnd())
					+ ")]";
		}
	}

	/**
	 * Get all activity groups of this {@link ActivityList}.
	 * <p>
	 * Note: Null will be returned if the {@link ActivityList} contains inconsistencies.
	 * </p>
	 *
	 * @return Stream of activity groups.
	 */
	public Stream<ActivityGroup> streamGroups() {
		try {
			validateConstraints();
		} catch (final ChartException e) {
			return null;
		}
		return activityGroups.stream();
	}

	/**
	 * Get all activities in the given activity group.
	 * <p>
	 * Note: Null will be returned if the {@link ActivityList} contains inconsistencies.
	 * </p>
	 *
	 * @param activityGroup
	 *            Activity group.
	 * @return Stream of {@link Activity}s.
	 */
	public Stream<Activity> streamActivities(final ActivityGroup activityGroup) {
		try {
			validateConstraints();
		} catch (final ChartException e) {
			return null;
		}
		return activityGroup.activities.stream();
	}

	@Override
	<T> Iterator<T> iterator(final BiFunction<AbstractTask, Integer, T> encoder,
			final Predicate<AbstractTask> activityFilter) {
		return new TaskIterator<>(encoder, activityFilter);
	}

	private class TaskIterator<T> implements Iterator<T> {

		private int index = -1;
		private int groupIndex = -1;
		private int activityIndex = -1;
		private Activity next = null;
		private final BiFunction<AbstractTask, Integer, T> encoder;
		private final Predicate<AbstractTask> activityFilter;

		private TaskIterator(final BiFunction<AbstractTask, Integer, T> encoder,
				final Predicate<AbstractTask> activityFilter) {
			this.encoder = encoder;
			this.activityFilter = activityFilter;
		}

		@Override
		public boolean hasNext() {
			if (next != null) {
				return true;
			}
			if (groupIndex == Integer.MIN_VALUE) {
				return false;
			}
			if (groupIndex == -1) {
				if (activityGroups.isEmpty()) {
					groupIndex = Integer.MIN_VALUE;
					return false;
				}
				groupIndex = 0;
				activityIndex = 0;
			} else {
				++activityIndex;
			}
			ActivityGroup activityGroup = activityGroups.get(groupIndex);
			while (activityIndex >= activityGroup.activities.size()) {
				if (++groupIndex >= activityGroups.size()) {
					groupIndex = Integer.MIN_VALUE;
					return false;
				}
				activityGroup = activityGroups.get(groupIndex);
				if (activityGroup.activities.isEmpty()) {
					continue;
				}
				activityIndex = 0;
				break;
			}
			++index;
			next = activityGroup.getActivity(activityIndex);
			if (activityFilter != null && !activityFilter.test(next)) {
				next = null;
				return hasNext();
			}
			return true;
		}

		@Override
		public T next() {
			if (next == null) {
				throw new NoSuchElementException();
			}
			final Activity activity = next;
			next = null;
			return encoder.apply(activity, index);
		}
	}

	@Override
	final int renderingPosition(final AbstractTask activity, final int index) {
		return activityGroups.indexOf(((Activity) activity).group);
	}

	@Override
	protected final String getExtraAxisLabel(final AbstractTask activity) {
		return activity instanceof ActivityGroup ? getExtraAxisLabel(((ActivityGroup) activity)) : "";
	}

	protected String getExtraAxisLabel(@SuppressWarnings("unused") final ActivityGroup activityGroup) {
		return activityGroup.getExtraInfo();
	}

	@Override
	final String getAxisLabel(final AbstractTask abstractActivity, final int index) {
		final ActivityGroup group = ((Activity) abstractActivity).group;
		return "[" + activityGroups.indexOf(group) + ",\"\",0,\"" + nullAsEmpty(group.getName()) + "\",\""
				+ nullAsEmpty(getExtraAxisLabel(group)) + "\"," + group.getColor() + "," + groupFontSize(group) + ","
				+ extraFontSize(group) + "]";
	}

	private String nullAsEmpty(final String s) {
		return s == null ? "" : s;
	}

	@Override
	AbstractDataProvider<String> axisLabels() {
		return dataProvider(DataType.OBJECT, this::getAxisLabel, t -> ((Activity) t).group.activities.get(0) == t);
	}

	@Override
	protected String getTooltipLabel(final AbstractTask abstractActivity) {
		final Activity activity = (Activity) abstractActivity;
		String extra = nullAsEmpty(activity.getExtraInfo());
		if (!extra.isEmpty()) {
			extra = "<br>" + extra;
		}
		final Function<LocalDateTime, String> timeConverter = getTooltipTimeFormat();
		final String s = getLabel(activity) + "<br>" + timeConverter.apply(activity.start);
		if (activity.isMilestone()) {
			return s + extra;
		}
		return s + " - " + timeConverter.apply(activity.getEnd()) + " (" + activity.getDuration() + ")" + extra;
	}
}
