/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package edu.byu.hr.model.timereporting;

import org.joda.time.LocalDate;

/**
 * Description
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class TimePeriodEntry {
    /**
     * Date the time applies to.
     */
    LocalDate date;

    /**
     * Job code to apply the time against.
     */
    int jobCode;

    /**
     * Time to apply as represented in hh:mm.
     */
    Duration timeEntered;

    public TimePeriodEntry() {
    }

    public TimePeriodEntry(LocalDate date, int jobCode, Duration timeEntered) {
        this.date = date;
        this.jobCode = jobCode;
        this.timeEntered = timeEntered;
    }

    public TimePeriodEntry(LocalDate date, int jobCode, int minutes) {
        this.date = date;
        this.jobCode = jobCode;
        this.timeEntered = new Duration(minutes);
    }

    public TimePeriodEntry(LocalDate date, int jobCode, String hhmm) {
        this.date = date;
        this.jobCode = jobCode;
        this.timeEntered = new Duration(hhmm);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getJobCode() {
        return jobCode;
    }

    public void setJobCode(int jobCode) {
        this.jobCode = jobCode;
    }

    public Duration getTimeEntered() {
        return timeEntered;
    }

    public void setTimeEntered(Duration timeEntered) {
        this.timeEntered = timeEntered;
    }
}
