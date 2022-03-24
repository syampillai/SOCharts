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

import com.storedobject.helper.ID;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Class to represent a project.
 * <p>{@link GanttChart} uses data from this class.</p>
 * <p>A project consists of instances of {@link Task}s (can be created via {@link #createTask(String, int)}).
 * You can also have {@link TaskGroup}s and specify that a {@link Task} instance belongs to a specific {@link TaskGroup}
 * instance while creating it via {@link #createTask(TaskGroup, String, int)} and {@link TaskGroup}s can be created
 * via {@link #createTaskGroup(String)}. Alternatively, a {@link Task} can be created under a {@link TaskGroup} via
 * {@link TaskGroup#createTask(String, int)}.</p>
 * <p>Dependencies between {@link Task}s and/or {@link TaskGroup}s can be specified via
 * {@link #dependsOn(AbstractTask, AbstractTask)}.</p>
 * <p>Note: Even though this class is designed to provide data for the {@link GanttChart}, it can be used
 * independently for scheduling tasks / task groups involved in a typical project. Once you defined the project by
 * adding all the task groups, tasks and dependencies, you can just use methods like {@link #streamGroups()},
 * {@link #streamTasks(TaskGroup)} and {@link #streamDependencies(AbstractTask)} to retrieve the data related to
 * the project. All those methods return data after auto-scheduling the tasks in the project.</p>
 *
 * @author Syam
 */
public class Project {

    private final long id = ID.newID();
    private final List<TaskGroup> taskGroups = new ArrayList<>();
    private String name;
    private final ChronoUnit durationType;
    private LocalDateTime start = null;
    private boolean checked = false;
    private Function<LocalDateTime, String> todayFormat, tooltipTimeFormat;
    private LocalDateTime today;
    private Color todayColor;
    private String bandColorEven, bandColorOdd;

    /**
     * Constructor for a date-based project.
     */
    public Project() {
        this(null);
    }

    /**
     * Constructor.
     *
     * @param durationType Type of duration to be used for this project. (Note: {@link ChronoUnit#ERAS} and
     *                     {@link ChronoUnit#FOREVER} are not supported and if used, will be considered as
     *                     {@link ChronoUnit#MILLIS}).
     */
    public Project(ChronoUnit durationType) {
        if(durationType == null) {
            durationType = ChronoUnit.DAYS;
        } else {
            durationType = switch(durationType) {
                case DAYS, HOURS, MINUTES, SECONDS -> durationType;
                default -> ChronoUnit.MILLIS;
            };
        }
        this.durationType = durationType;
        if(durationType.isDateBased()) {
            today = LocalDate.now().atStartOfDay();
        } else {
            today = LocalDateTime.now();
        }
    }

    /**
     * Get the duration type of this project.
     *
     * @return Duration type.
     */
    public final ChronoUnit getDurationType() {
        return durationType;
    }

    /**
     * Set the name of the project.
     *
     * @param name Name of the project.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the name of the project.
     *
     * @return Project name.
     */
    public final String getName() {
        return name == null || name.isEmpty() ? ("Project " + id) : name;
    }

    /**
     * Validate dependencies. A {@link ChartException} is thrown if any circular dependency is found.
     * <p>Important: This method removes any "task group" from this project that does not have any "task".</p>
     */
    public void validateConstraints() throws ChartException {
        if(checked) {
            return;
        }
        if(start == null) {
            throw new ChartException("Project start not specified");
        }
        taskGroups.removeIf(tg -> tg.tasks.isEmpty());
        for(TaskGroup taskGroup: taskGroups) {
            validateDependency(taskGroup);
            for(Task task: taskGroup.tasks) {
                validateDependency(task);
            }
        }
        schedule();
        taskGroups.sort(Project::compare);
        taskGroups.forEach(TaskGroup::sort);
        checked = true;
    }

    /**
     * Is this project empty?
     *
     * @return True if no tasks exist.
     */
    public boolean isEmpty() {
        return taskGroups.isEmpty();
    }

    private void schedule() {
        for(TaskGroup taskGroup: taskGroups) {
            taskGroup.start = start;
            taskGroup.tasks.forEach(t -> t.start = start);
        }
        adjustTaskStart();
        adjustGroupStart();
        adjustStart();
        while(adjustTaskStart()) {
            if(!adjustGroupStart() && !adjustStart()) {
                break;
            }
        }
    }

    private boolean adjustTaskStart() {
        boolean adjusted = false;
        for(TaskGroup taskGroup: taskGroups) {
            for(Task task: taskGroup.tasks) {
                if(task.applyTaskStartDependency()) {
                    adjusted = true;
                }
            }
        }
        return adjusted;
    }

    private boolean adjustGroupStart() {
        boolean adjusted = false;
        for(TaskGroup taskGroup: taskGroups) {
            if(taskGroup.applyGroupStartDependency()) {
                adjusted = true;
            }
        }
        return adjusted;
    }

    private boolean adjustStart() {
        boolean adjusted = false;
        for(TaskGroup taskGroup: taskGroups) {
            if(taskGroup.applyStartDependency()) {
                adjusted = true;
            }
            for(Task task: taskGroup.tasks) {
                if(task.applyStartDependency()) {
                    adjusted = true;
                }
            }
        }
        return adjusted;
    }

    private static void validateDependency(AbstractTask instance) throws ChartException {
        if(validateDependency(instance, instance.predecessors)) {
            throw new ChartException("Circular dependency: Task " + (instance instanceof  TaskGroup ? "Group " : "")
                    + '\'' + instance.getName() + '\'');
        }
    }

    private static boolean validateDependency(AbstractTask instance, List<AbstractTask> predecessors) {
        if(predecessors.contains(instance)) {
            return true;
        }
        for(AbstractTask a: predecessors) {
            if(a instanceof TaskGroup tg && instance instanceof Task t && tg.tasks.contains(t)) {
                return true;
            }
            if(a instanceof Task t && instance instanceof TaskGroup tg && tg.equals(t.group)) {
                return true;
            }
            if(validateDependency(instance, a.predecessors)) {
                return true;
            }
        }
        return false;
    }

    private boolean contains(AbstractTask instance) {
        if(instance instanceof Task task) {
            if(task.group == null) {
                return false;
            }
            instance = task.group;
        }
        return taskGroups.contains((TaskGroup) instance);
    }

    /**
     * Create and add a {@link TaskGroup} to the project.
     *
     * @param name Name of the task group.
     * @return Task group created.
     */
    public TaskGroup createTaskGroup(String name) {
        TaskGroup taskGroup = new TaskGroup(name);
        taskGroups.add(0, taskGroup);
        return taskGroup;
    }

    /**
     * Create and add a {@link Task} to the project. (This method should be used only if there is no need of
     * "grouping of tasks".)
     *
     * @param name Name of the task.
     * @param duration Duration of the task. (A duration of zero denotes a project milestone).
     * @return Task created.
     */
    public Task createTask(String name, int duration) {
        return createTask(createTaskGroup("DEFAULT"), name, duration);
    }

    /**
     * Create and add a {@link Task} to the project.
     *
     * @param taskGroup Group to which the task to be added.
     * @param taskName Name of the task.
     * @param duration Duration of the task. (A duration of zero denotes a project milestone).
     * @return Task created. (Returns <code>null</code> if the {@link TaskGroup} passed doesn't belong to this project).
     */
    public Task createTask(TaskGroup taskGroup, String taskName, int duration) {
        if(taskGroups.contains(taskGroup)) {
            return new Task(taskGroup, taskName, duration);
        }
        return null;
    }

    private void deleteTask(Task task) {
        if(task == null) {
            return;
        }
        checked = false;
        task.group.tasks.remove(task);
        taskGroups.forEach(taskGroup -> taskGroup.tasks.forEach(t -> t.predecessors.remove(task)));
    }

    private void deleteGroup(TaskGroup taskGroup) {
        if(taskGroup == null) {
            return;
        }
        checked = false;
        taskGroups.remove(taskGroup);
        while(!taskGroup.tasks.isEmpty()) {
            deleteTask(taskGroup.tasks.get(0));
        }
    }

    /**
     * Delete {@link TaskGroup}s from the project. (All tasks and dependencies related to these groups will be
     * dropped).
     *
     * @param taskGroups {@link TaskGroup}s to delete.
     */
    public void delete(TaskGroup... taskGroups) {
        if(taskGroups != null) {
            for(TaskGroup taskGroup: taskGroups) {
                deleteGroup(taskGroup);
            }
        }
    }

    /**
     * Delete {@link Task}s from the project. (All dependencies related to these tasks will be dropped).
     *
     * @param tasks {@link Task}s to delete.
     */
    public void delete(Task... tasks) {
        if(tasks != null) {
            for(Task task: tasks) {
                deleteTask(task);
            }
        }
    }

    /**
     * Specify that a task/group depends on another task/group.
     *
     * @param dependent Dependent.
     * @param predecessor Predecessor.
     */
    public void dependsOn(AbstractTask dependent, AbstractTask predecessor) {
        if(dependent == null || predecessor == null) {
            return;
        }
        List<AbstractTask> predecessors = dependent.predecessors;
        if(!predecessors.contains(predecessor)) {
            predecessors.add(predecessor);
        }
    }

    private LocalDateTime trim(LocalDateTime dateTime) {
        return dateTime.truncatedTo(durationType);
    }

    /**
     * Set the start of the project. Depending on the duration type, one of the "setStart" methods can be used.
     * However, the start value will be appropriately trimmed if you try to set a higher resolution value.
     *
     * @param start Start of the project.
     */
    public void setStart(LocalDateTime start) {
        checked = false;
        this.start = trim(start);
    }

    /**
     * Set the start of the project. Depending on the duration type, one of the "setStart" methods can be used.
     * However, the start value will be appropriately trimmed if you try to set a higher resolution value.
     *
     * @param start Start of the project.
     */
    public void setStart(LocalDate start) {
        setStart(start.atStartOfDay());
    }

    /**
     * Set the start of the project. Depending on the duration type, one of the "setStart" methods can be used.
     * However, the start value will be appropriately trimmed if you try to set a higher resolution value.
     *
     * @param start Start of the project.
     */
    public void setStart(Instant start) {
        setStart(LocalDateTime.from(start));
    }

    /**
     * Get the project start.
     *
     * @return Start date/time.
     */
    public final LocalDateTime getStart() {
        return start;
    }

    /**
     * Get the project end.
     *
     * @return End date/time.
     */
    public final LocalDateTime getEnd() {
        LocalDateTime start = Project.this.start, end = start, e;
        for(TaskGroup taskGroup: taskGroups) {
            for(Task task: taskGroup.tasks) {
                e = task.getEnd();
                if(e.isAfter(end)) {
                    end = e;
                }
            }
        }
        return end;
    }

    /**
     * Reset the earliest start set via one of the "setEarliestStart" methods so that the constraint is no more
     * applicable.
     *
     * @param task Task for which "earliest start" needs to be reset.
     */
    public void resetEarliestStart(AbstractTask task) {
        checked = false;
        task.earliestStart = null;
    }

    /**
     * Set the "earliest start" possible for a task/group. Depending on the duration type, one of the "setEarliestStart"
     * methods can be used. However, the value will be appropriately trimmed if you try to set a higher
     * resolution value. This is applied as a constraint when tasks are scheduled.
     * <p>Note: You can use {@link #resetEarliestStart(AbstractTask)} to undo the effect of this constraint.</p>
     *
     * @param task Task/group for which the "earliest start" should be set.
     * @param start Earliest start possible for the task/group.
     */
    public void setEarliestStart(AbstractTask task, LocalDateTime start) {
        checked = false;
        task.earliestStart = trim(start);
    }

    /**
     * Set the "earliest start" possible for a task/group. Depending on the duration type, one of the "setEarliestStart"
     * methods can be used. However, the value will be appropriately trimmed if you try to set a higher
     * resolution value. This is applied as a constraint when tasks are scheduled.
     * <p>Note: You can use {@link #resetEarliestStart(AbstractTask)} to undo the effect of this constraint.</p>
     *
     * @param task Task/group for which the "earliest start" should be set.
     * @param start Earliest start possible for the task/group.
     */
    public void setEarliestStart(AbstractTask task, LocalDate start) {
        setEarliestStart(task, start.atStartOfDay());
    }

    /**
     * Set the "earliest start" possible for a task/group. Depending on the duration type, one of the "setEarliestStart"
     * methods can be used. However, the value will be appropriately trimmed if you try to set a higher
     * resolution value. This is applied as a constraint when tasks are scheduled.
     * <p>Note: You can use {@link #resetEarliestStart(AbstractTask)} to undo the effect of this constraint.</p>
     *
     * @param task Task/group for which the "earliest start" should be set.
     * @param start Earliest start possible for the task/group.
     */
    public void setEarliestStart(AbstractTask task, Instant start) {
        setEarliestStart(task, LocalDateTime.from(start));
    }

    private static int compare(AbstractTask a1, AbstractTask a2) {
        int c = a2.getStart().compareTo(a1.getStart());
        return c == 0 ? Integer.compare(a1.order, a2.order) : c;
    }

    /**
     * Get the task count.
     *
     * @return Task count.
     */
    public final int getTaskCount() {
        return taskGroups.stream().mapToInt(tg -> tg.tasks.size()).sum();
    }

    /**
     * Get the {@link TaskGroup} of this project.
     *
     * @param groupIndex Index of the task group to be retrieved.
     * @return {@link TaskGroup}.
     */
    public final TaskGroup getTaskGroup(int groupIndex) {
        return taskGroups.get(groupIndex);
    }

    /**
     * Get the number of {@link Task} belonging to this group.
     *
     * @return Task count.
     */
    public final int getGroupCount() {
        return taskGroups.size();
    }

    private static LocalDateTime max(LocalDateTime one, LocalDateTime two) {
        if(one == null && two == null) {
            return null;
        }
        if(one == null) {
            return two;
        }
        if(two == null) {
            return one;
        }
        return one.isBefore(two) ? two : one;
    }

    /**
     * An abstract base class for the representation of a Task or Task Group.
     *
     * @author Syam
     */
    abstract class AbstractTask {

        private final long id = ID.newID();
        private String name, extraInfo;
        private Color color;
        private int order = -1;
        /**
         * Possible earliest start of the task/group.
         */
        LocalDateTime earliestStart = null;
        /**
         * Start of the task/group.
         */
        LocalDateTime start = null;
        /**
         * List of predecessors.
         */
        final List<AbstractTask> predecessors = new ArrayList<>();

        /**
         * Constructor.
         *
         * @param name Name of the task/group.
         */
        protected AbstractTask(String name) {
            setName(name);
        }

        public final long getId() {
            return id;
        }

        /**
         * Set the name.
         *
         * @param name The name to set.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Get the name.
         *
         * @return Current name.
         */
        public final String getName() {
            if(name != null && !name.isEmpty()) {
                return "DEFAULT".equals(name) ? "" : name;
            }
            return (this instanceof Task ? "Task" : "Group") + ": " + id;
        }

        @Override
        public final boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(!(o instanceof AbstractTask that)) {
                return false;
            }
            return id == that.id;
        }

        @Override
        public final int hashCode() {
            return Objects.hash(id);
        }

        /**
         * Set the order. Start time and dependencies decide the order of a task/group. This parameter is used to
         * decide on the ordering only when there is a tie. Mostly, it is not required to set this value.
         *
         * @param order The order to set.
         */
        public void setOrder(int order) {
            this.order = order;
        }

        /**
         * Get the order.
         *
         * @return Current order.
         */
        public int getOrder() {
            return order;
        }

        /**
         * Set extra information.
         *
         * @param extraInfo Extra information.
         */
        public void setExtraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
        }

        /**
         * Get the extra information associated with this.
         *
         * @return Extra information associated with this.
         */
        public String getExtraInfo() {
            return extraInfo;
        }

        /**
         * Get the duration.
         *
         * @return Duration (in {@link #getDurationType()}).
         */
        public abstract int getDuration();

        /**
         * Get the start.
         *
         * @return Start.
         */
        public LocalDateTime getStart() {
            start = max(earliestStart, start);
            return start;
        }

        /**
         * Get the end.
         *
         * @return End.
         */
        public final LocalDateTime getEnd() {
            return getStart().plus(getDuration(), durationType);
        }

        /**
         * Adjust start based on the dependencies. (For internal use only)
         *
         * @return True if any adjustment done.
         */
        boolean applyTaskStartDependency() {
            return applyStartDependency(a -> a instanceof TaskGroup);
        }

        /**
         * Adjust start based on the dependencies. (For internal use only)
         *
         * @return True if any adjustment done.
         */
        boolean applyGroupStartDependency() {
            return applyStartDependency(a -> a instanceof Task);
        }

        /**
         * Adjust start based on the dependencies. (For internal use only)
         *
         * @return True if any adjustment done.
         */
        boolean applyStartDependency() {
            return applyStartDependency(a -> false);
        }

        private boolean applyStartDependency(Predicate<AbstractTask> skip) {
            if(predecessors.isEmpty()) {
                return false;
            }
            for(AbstractTask p: predecessors) {
                if(skip.test(p)) {
                    continue;
                }
                p.applyStartDependency(skip);
            }
            boolean adjusted = false;
            LocalDateTime end;
            for(AbstractTask p: predecessors) {
                if(skip.test(p)) {
                    continue;
                }
                end = p.getEnd();
                if(!(p instanceof Task task) || !task.isMilestone()) {
                    end = end.plus(1, durationType);
                }
                if(start.isBefore(end)) {
                    adjusted = true;
                    start = end;
                }
            }
            return adjusted;
        }

        /**
         * Check if this task / task group is completed or not.
         *
         * @return True/false.
         */
        public abstract boolean isCompleted();

        /**
         * Start used by renderers may be different from the normal start. (For internal use only).
         *
         * @return Start for the renderers.
         */
        LocalDateTime renderStart() {
            if(getDuration() == 0) {
                return start.minus(1, durationType);
            }
            return start;
        }


        /**
         * Set the color to be used when rendering tasks under this task/group.
         *
         * @param color Color.
         */
        public void setColor(Color color) {
            this.color = color;
        }

        /**
         * Get the color to be used when rendering tasks under this task/group.
         *
         * @return Color.
         */
        public Color getColor() {
            return color;
        }
    }

    /**
     * Represents a group of {@link Task}s.
     *
     * @author Syam
     */
    public class TaskGroup extends AbstractTask {

        private final List<Task> tasks = new ArrayList<>();

        /**
         * Constructor.
         *
         * @param name Name.
         */
        TaskGroup(String name) {
            super(name);
        }

        /**
         * Create and add a {@link Task} to this task group.
         *
         * @param name Name of the task.
         * @param duration Duration of the task. (A duration of zero denotes a project milestone).
         * @return Task created.
         */
        public Task createTask(String name, int duration) {
            return Project.this.createTask(this, name, duration);
        }

        @Override
        public Color getColor() {
            Color color = super.getColor();
            if(color == null) {
                setColor(color = SOChart.getDefaultColor(taskGroups.indexOf(this)));
            }
            return color;
        }

        private void sort() {
            tasks.sort(Project::compare);
        }

        @Override
        public final int getDuration() {
            LocalDateTime start = null, end = null;
            LocalDateTime taskStart, taskEnd;
            for(Task task: tasks) {
                taskStart = task.getStart();
                if(start == null || taskStart.isBefore(start)) {
                    start = taskStart;
                }
                taskEnd = task.getEnd();
                if(end == null || taskEnd.isAfter(end)) {
                    end = taskEnd;
                }
            }
            if(start == null || end == null) {
                return 0;
            }
            return (int)durationType.between(start, end);
        }

        @Override
        public final LocalDateTime getStart() {
            LocalDateTime taskStart = tasks.get(0).getStart();
            if(taskStart.isBefore(start)) {
                start = taskStart;
            }
            for(Task task: tasks) {
                taskStart = task.getStart();
                if(taskStart.isBefore(start)) {
                    start = taskStart;
                }
            }
            return super.getStart();
        }

        /**
         * Get a {@link Task} from this group.
         *
         * @param taskIndex Index of the task to be retrieved.
         * @return {@link Task}.
         */
        public final Task getTask(int taskIndex) {
            return tasks.get(taskIndex);
        }

        /**
         * Get the number of {@link Task} belonging to this group.
         *
         * @return Task count.
         */
        public final int getTaskCount() {
            return tasks.size();
        }

        @Override
        public boolean isCompleted() {
            return tasks.stream().allMatch(AbstractTask::isCompleted);
        }
    }

    /**
     * Represents a task in a project.
     *
     * @author Syam
     */
    public class Task extends AbstractTask {

        private final int duration;
        private final TaskGroup group;
        private double completed = 0;

        /**
         * Constructor.
         *
         * @param taskGroup Task group to which this task belongs to.
         * @param name Name.
         * @param duration Duration of the task. (A duration of zero denotes a project milestone).
         */
        Task(TaskGroup taskGroup, String name, int duration) {
            super(name);
            this.group = taskGroup;
            this.group.tasks.add(this);
            this.duration = Math.max(duration, 0);
        }

        /**
         * Set completion percentage.
         *
         * @param completed Percentage completed.
         */
        public void setCompleted(double completed) {
            this.completed = Math.min(Math.max(0, completed), 100);
        }

        /**
         * Get percentage completed.
         *
         * @return Percentage completed.
         */
        public final double getCompleted() {
            return completed;
        }

        @Override
        public final int getDuration() {
            return duration;
        }

        /**
         * Is this a milestone task?
         *
         * @return True if duration is zero.
         */
        public final boolean isMilestone() {
            return duration == 0;
        }

        @Override
        public LocalDateTime getStart() {
            start = max(start, group.earliestStart);
            return super.getStart();
        }

        /**
         * Get the {@link TaskGroup} this task belongs to.
         *
         * @return {@link TaskGroup}.
         */
        public final TaskGroup getGroup() {
            return group;
        }

        @Override
        public boolean isCompleted() {
            if(duration > 0) {
                return completed >= 100;
            }
            return predecessors.stream().allMatch(AbstractTask::isCompleted);
        }

        @Override
        public Color getColor() {
            Color color = super.getColor();
            return color == null ? group.getColor() : color;
        }
    }

    /**
     * Get all task groups of this project.
     * <p>Note: Null will be returned if the project contains inconsistencies.</p>
     *
     * @return Stream of task groups.
     */
    public Stream<TaskGroup> streamGroups() {
        try {
            validateConstraints();
        } catch(ChartException e) {
            return null;
        }
        return taskGroups.stream();
    }

    /**
     * Get all tasks in the given task group.
     * <p>Note: Null will be returned if the project contains inconsistencies.</p>
     *
     * @param taskGroup Task group.
     * @return Stream of tasks.
     */
    public Stream<Task> streamTasks(TaskGroup taskGroup) {
        try {
            validateConstraints();
        } catch(ChartException e) {
            return null;
        }
        return taskGroup.tasks.stream();
    }

    /**
     * Get all dependencies of the given task or group.
     * <p>Note: Null will be returned if the project contains inconsistencies.</p>
     *
     * @param task Task.
     * @return Stream of tasks/groups.
     */
    public Stream<AbstractTask> streamDependencies(AbstractTask task) {
        try {
            validateConstraints();
        } catch(ChartException e) {
            return null;
        }
        return task.predecessors.stream();
    }

    private class TaskIterable<T> implements Iterable<T> {

        private final BiFunction<Task, Integer, T> encoder;
        private final Predicate<Task> taskFilter;

        private TaskIterable(BiFunction<Task, Integer, T> encoder, Predicate<Task> taskFilter) {
            this.encoder = encoder;
            this.taskFilter = taskFilter;
        }

        @Override
        public Iterator<T> iterator() {
            return new TaskIterator<>(encoder, taskFilter);
        }
    }

    private class TaskIterator<T> implements Iterator<T> {

        private int index = -1;
        private int groupIndex = -1;
        private int taskIndex = -1;
        private Task next = null;
        private final BiFunction<Task, Integer, T> encoder;
        private final Predicate<Task> taskFilter;

        private TaskIterator(BiFunction<Task, Integer, T> encoder, Predicate<Task> taskFilter) {
            this.encoder = encoder;
            this.taskFilter = taskFilter;
        }

        @Override
        public boolean hasNext() {
            if(next != null) {
                return true;
            }
            if(groupIndex == Integer.MIN_VALUE) {
                return false;
            }
            if(groupIndex == -1) {
                if(taskGroups.isEmpty()) {
                    groupIndex = Integer.MIN_VALUE;
                    return false;
                }
                groupIndex = 0;
                taskIndex = 0;
            } else {
                ++taskIndex;
            }
            TaskGroup taskGroup = taskGroups.get(groupIndex);
            while(taskIndex >= taskGroup.tasks.size()) {
                if(++groupIndex >= taskGroups.size()) {
                    groupIndex = Integer.MIN_VALUE;
                    return false;
                }
                taskGroup = taskGroups.get(groupIndex);
                if(taskGroup.tasks.isEmpty()) {
                    continue;
                }
                taskIndex = 0;
                break;
            }
            ++index;
            next = taskGroup.getTask(taskIndex);
            if(taskFilter != null && !taskFilter.test(next)) {
                next = null;
                return hasNext();
            }
            return true;
        }

        @Override
        public T next() {
            if(next == null) {
                throw new NoSuchElementException();
            }
            Task task = next;
            next = null;
            return encoder.apply(task, index);
        }
    }

    private String encode(LocalDateTime time) {
        return "\"" + (durationType.isDateBased() ? time.toLocalDate() : time) + "\"";
    }

    private <T> Stream<T> taskData(BiFunction<Task, Integer, T> encoder, Predicate<Task> taskFilter) {
        return StreamSupport.stream(new TaskIterable<>(encoder, taskFilter).spliterator(), false);
    }

    /**
     * Data for rendering the bands. (For internal use only).
     *
     * @return Band data.
     */
    AbstractDataProvider<String> taskBands() {
        if(bandColorOdd == null) {
            bandColorOdd = "#D9E1F2";
        }
        if(bandColorEven == null) {
            bandColorEven = "#EEF0F3";
        }
        if(!bandColorOdd.startsWith("\"")) {
            bandColorOdd = "\"" + bandColorOdd + "\"";
        }
        if(!bandColorEven.startsWith("\"")) {
            bandColorEven = "\"" + bandColorEven + "\"";
        }
        LocalDateTime end = getEnd();
        return dataProvider(DataType.OBJECT, (t, i) -> "[" + i + "," + Project.this.encode(start) + ","
                + Project.this.encode(end) + ","
                + (i % 2 == 0 ? bandColorEven : bandColorOdd) + "]");
    }

    private static String trim(double v) {
        String t = "" + v;
        if(t.endsWith(".0")) {
            t = t.substring(0, t.indexOf('.'));
        }
        return t;
    }

    /**
     * Get the label of the given task (Will be used for displaying the label on the task bar).
     * The default implementation returns {@link Task#getName()} + " (" + {@link Task#getCompleted()} + "%)".
     * <p>In the case of a milestone ({@link Task#isMilestone()}), it returns just the milestone name.</p>
     *
     * @param task Task.
     * @return Label.
     */
    protected String getTaskLabel(Task task) {
        if(task.isMilestone()) {
            return task.getName();
        }
        return task.getName() + " ("+ trim(task.getCompleted()) + "%)";
    }

    /**
     * Get data for rendering tasks. (For internal use only).
     *
     * @return Task data.
     */
    AbstractDataProvider<String> taskData() {
        BiFunction<Task, Integer, String> func = (t, i) -> "[" + i + ",\"" + getTaskLabel(t) + "\","
                + encode(t.renderStart()) + "," + encode(t.getEnd()) + ","
                + (t.isMilestone() ? 100 : t.getCompleted()) + ","
                + t.getColor() + "]";
        return dataProvider(DataType.OBJECT, func);
    }

    /**
     * Get data for rendering task dependencies. (For internal use only).
     *
     * @return Task dependencies.
     */
    AbstractDataProvider<String> taskDependencies() {
        BiFunction<Task, Integer, String> func = (t, i) -> "[" + i + "," + encode(t.renderStart())
                + "," + encode(t.getEnd()) + "," + dependents(t) + "]";
        return dataProvider(DataType.OBJECT, func, t -> t.predecessors.size() > 0);
    }

    private String dependents(Task task) {
        StringBuilder sb = new StringBuilder("{\"d\":[");
        boolean first = true;
        for(AbstractTask at: task.predecessors) {
            if(at instanceof TaskGroup tg) {
                at = tg.tasks.get(tg.tasks.size() - 1);
            }
            if(first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append("[").append(indexOf((Task)at)).append(",").append(encode(at.renderStart())).append(",")
                    .append(encode(at.getEnd())).append("]");
        }
        sb.append("]}");
        return "\"" + sb.toString().replace("\"", "^") + "\"";
    }

    private int indexOf(Task t) {
        int n = 0;
        for(TaskGroup tg: taskGroups) {
            if(tg == t.group) {
                break;
            }
            n += tg.tasks.size();
        }
        return n + t.group.tasks.indexOf(t);
    }

    /**
     * Get the axis label for the task. (Will be used for displaying the axis label).
     * <p>The default implementation returns the name of the task.</p>
     *
     * @param task Task.
     * @return Label.
     */
    protected String getTaskAxisLabel(Task task) {
        return task.getName();
    }

    /**
     * Get today/now.
     *
     * @return Today/now.
     */
    protected LocalDateTime getToday() {
        return today;
    }

    /**
     * Get the extra axis label for the task. (Will be used for displaying the 2nd line of the axis label).
     * <p>The default implementation returns the description of the time-left to work on the task.</p>
     *
     * @param task Task.
     * @return Label.
     */
    protected String getExtraTaskAxisLabel(Task task) {
        if(task.isCompleted()) {
            return task.isMilestone() ? "Achieved" : "Completed";
        }
        Duration duration = Duration.between(task.getEnd(), today);
        String timeName;
        long left;
        if(durationType.isDateBased()) {
            left = duration.toDays();
            timeName = "day";
        } else {
            switch(durationType) {
                case SECONDS -> {
                    left = duration.toSeconds();
                    timeName = "second";
                }
                case MINUTES -> {
                    left = duration.toMinutes();
                    timeName = "minute";
                }
                case HOURS -> {
                    left = duration.toHours();
                    timeName = "hour";
                }
                default -> {
                    left = duration.toMillis();
                    timeName = "millisecond";
                }
            }
        }
        if(Math.abs(left) != 1) {
            timeName += "s";
        }
        if(left > 0) {
            return "Late by " + left + " " + timeName;
        }
        return (-left) + " " + timeName + " remaining";
    }

    private String getTaskAxisLabel(Task task, Integer index) {
        return "[" + index + ",\"" + task.group.getName() + "\","
                + (task == task.group.tasks.get(task.group.tasks.size() - 1) ? 0 : 1) + ",\""
                + getTaskAxisLabel(task) + "\",\"" + getExtraTaskAxisLabel(task) + "\"," + task.getColor() + "]";
    }

    /**
     * Get labels for tooltip for the task axis. (For internal use only).
     *
     * @return Labels for task axis.
     */
    AbstractDataProvider<String> taskAxisLabels() {
        return dataProvider(DataType.OBJECT, this::getTaskAxisLabel);
    }

    /**
     * Get the tooltip label of the given task (Will be used for displaying the tooltip label).
     *
     * @param task Task.
     * @return Label.
     */
    protected String getTooltipLabel(Task task) {
        String extra = task.getExtraInfo();
        if(extra == null || extra.isEmpty()) {
            extra = "";
        } else {
            extra = "<br>" + extra;
        }
        Function<LocalDateTime, String> timeConverter = getTooltipTimeFormat();
        String s = getTaskLabel(task) + "<br>" + timeConverter.apply(task.start);
        if(task.isMilestone()) {
            return s + extra;
        }
        return s + " - " + timeConverter.apply(task.getEnd()) + " (" + task.getDuration() + ")" + extra;
    }

    /**
     * Get labels for tooltip for the tasks. (For internal use only).
     *
     * @return Tooltip labels for tasks.
     */
    AbstractDataProvider<String> taskTooltipLabels() {
        return dataProvider(DataType.CATEGORY, (t, i) -> "\"" + getTooltipLabel(t) + "\"");
    }

    private <T> AbstractDataProvider<T> dataProvider(DataType dataType, BiFunction<Task, Integer, T> encoder) {
        return dataProvider(dataType, encoder, null);
    }

    private <T> AbstractDataProvider<T> dataProvider(DataType dataType, BiFunction<Task, Integer, T> encoder,
                                                     Predicate<Task> taskFilter) {
        return new BasicDataProvider<>() {

            @Override
            public Stream<T> stream() {
                return taskData(encoder, taskFilter);
            }

            @Override
            public DataType getDataType() {
                return dataType;
            }

            @Override
            public void encode(StringBuilder sb, T value) {
                sb.append(value);
            }
        };
    }

    /**
     * Data required for rendering today/time marker. (For internal use only).
     *
     * @return Data for today/time marker.
     */
    AbstractDataProvider<?> dataForToday() {
        return new BasicDataProvider<>() {

            @Override
            public Stream<Object> stream() {
                return Stream.of("");
            }

            @Override
            public DataType getDataType() {
                return DataType.OBJECT;
            }

            @Override
            public void encode(StringBuilder sb, Object value) {
                if(todayColor == null) {
                    todayColor = new Color("#FF000");
                }
                sb.append("[").append(Project.this.encode(today)).append(",\"").append(getTodayFormat()
                        .apply(today)).append("\",").append(todayColor).append(",100]");
            }
        };
    }

    /**
     * Set today. By default, today = {@link LocalDate#now()}. However, it is possible to set another date.
     * (Used in date-based projects).
     *
     * @param today Value of today.
     */
    public void setToday(LocalDate today) {
        this.today = today.atStartOfDay();
    }

    /**
     * Set the color for today-marker on the Gantt chart.
     * (Used in date-based projects).
     *
     * @param color Color.
     */
    public void setTodayColor(Color color) {
        todayColor = color;
    }

    /**
     * Set time-now. By default, now = {@link LocalDateTime#now()}. However, it is possible to set another date or time.
     * (Used in time-based projects).
     *
     * @param now Value of time-now.
     */
    public void setTimeNow(LocalDateTime now) {
        this.today = now;
    }

    /**
     * Set the color for time-now-marker on the Gantt chart.
     * (Used in time-based projects).
     *
     * @param color Color.
     */
    public void setTimeNowColor(Color color) {
        todayColor = color;
    }

    /**
     * Set a formatter function to format the today-marker. By default, today is formatted as follows:
     * <p>Today: Jan 23, 1998</p>
     *
     * @param todayFormat Formatter function.
     */
    public void setTodayFormat(Function<LocalDateTime, String> todayFormat) {
        this.todayFormat = todayFormat;
    }

    /**
     * Set a formatter function to format the tooltip to task. By default, it is formatted as follows:
     * <p>Aug 07, 2002</p>
     *
     * @param tooltipTimeFormat Formatter function.
     */
    public void setTooltipTimeFormat(Function<LocalDateTime, String> tooltipTimeFormat) {
        this.tooltipTimeFormat = tooltipTimeFormat;
    }

    private String timePattern() {
        StringBuilder s = new StringBuilder("MMM dd, yyyy");
        if(!durationType.isDateBased()) {
            s.append(' ');
            switch(durationType) {
                case SECONDS -> s.append("HH:mm:ss");
                case MINUTES -> s.append("HH:mm");
                case HOURS -> s.append("HH");
                default -> s.append("HH:mm:ss.S");
            }
        }
        return s.toString();
    }

    private Function<LocalDateTime, String> getTodayFormat() {
        if(todayFormat == null) {
            String s = timePattern();
            todayFormat = d -> "Today: " + DateTimeFormatter.ofPattern(s).format(d);
        }
        return todayFormat;
    }

    private Function<LocalDateTime, String> getTooltipTimeFormat() {
        if(tooltipTimeFormat == null) {
            String s = timePattern();
            tooltipTimeFormat = d -> DateTimeFormatter.ofPattern(s).format(d);
        }
        return tooltipTimeFormat;
    }

    /**
     * Set the colors of the task bands.
     *
     * @param bandColorOdd Color for odd rows.
     * @param bandColorEven Color for even rows.
     */
    public void setTaskBandColors(Color bandColorOdd, Color bandColorEven) {
        this.bandColorOdd = bandColorOdd.toString();
        this.bandColorEven = bandColorEven.toString();
    }
}
