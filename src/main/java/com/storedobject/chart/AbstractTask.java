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

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for the representation of tasks/activities used within {@link Project} instances and
 * {@link ActivityList} instances.
 *
 * @author Syam
 */
public abstract class AbstractTask {

    private final long id = ID.newID();
    private String name, extraInfo;
    private Color color;
    private int fontSize = 0, extraFontSize = 0;
    /**
     * Start of the task/group.
     */
    LocalDateTime start = null;

    /**
     * Is this a milestone task? (Not applicable to activities).
     *
     * @return True if the duration is zero.
     */
    public boolean isMilestone() {
        return false;
    }

    /**
     * Unique ID for this task/activity.
     *
     * @return A unique number that is automatically generated.
     */
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
    public String getName() {
        return name;
    }

    /**
     * Get the start.
     *
     * @return Start.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Duration. The unit is the same as that of the "duration type" of the project or activity list.
     * @return Duration of this task/activity.
     */
    public abstract int getDuration();

    /**
     * Get the end.
     *
     * @return End.
     */
    public abstract LocalDateTime getEnd();

    /**
     * Get percentage completed.
     *
     * @return Percentage completed.
     */
    public abstract double getCompleted();

    /**
     * Start used by renderers (Maybe different from the normal start).
     *
     * @return Start for the renderers.
     */
    public LocalDateTime renderStart() {
        return getStart();
    }

    /**
     * Get the color used when rendering.
     *
     * @return Color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color to be used when rendering.
     *
     * @param color Color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get the font-size used to render the details
     *
     * @return Font-size. Default is 12.
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * Set the font size used to render the details.
     *
     * @param fontSize Font-size to set (in pixels). Default is 12.
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Get the font-size used to render the extra information of a group (Applicable only if this is a group).
     *
     * @return Font-size. Default is 9.
     */
    public int getExtraFontSize() {
        return extraFontSize;
    }

    /**
     * Set the font size used to render the extra information of a group (Applicable only if this is a group).
     * <p>Note: You can also set different values for individual groups. See {@link AbstractTask#setFontSize(int)}
     * </p>
     *
     * @param extraFontSize Font-size to set (in pixels). Default is 9.
     */
    public void setExtraFontSize(int extraFontSize) {
        this.extraFontSize = extraFontSize;
    }

    /**
     * Check if this task/activity is completed or not.
     *
     * @return True/false.
     */
    public boolean isCompleted() {
        return getCompleted() >= 100;
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
}
