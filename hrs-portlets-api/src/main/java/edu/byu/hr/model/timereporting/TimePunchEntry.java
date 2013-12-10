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

import java.io.Serializable;

/**
 * Description
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class TimePunchEntry implements Serializable {
    JobDescription job;
    String weekTimeWorked;
    String payPeriodTimeWorked;
    boolean punchedIn;

    public TimePunchEntry() {
    }

    public TimePunchEntry(JobDescription job, String weekTimeWorked, String payPeriodTimeWorked) {
        this(job, weekTimeWorked, payPeriodTimeWorked, false);
    }

    public TimePunchEntry(JobDescription job, String weekTimeWorked, String payPeriodTimeWorked, boolean punchedIn) {
        this.job = job;
        this.weekTimeWorked = weekTimeWorked;
        this.payPeriodTimeWorked = payPeriodTimeWorked;
        this.punchedIn = punchedIn;
    }

    public JobDescription getJob() {
        return job;
    }

    public void setJob(JobDescription job) {
        this.job = job;
    }

    public String getWeekTimeWorked() {
        return weekTimeWorked;
    }

    public void setWeekTimeWorked(String weekTimeWorked) {
        this.weekTimeWorked = weekTimeWorked;
    }

    public String getPayPeriodTimeWorked() {
        return payPeriodTimeWorked;
    }

    public void setPayPeriodTimeWorked(String payPeriodTimeWorked) {
        this.payPeriodTimeWorked = payPeriodTimeWorked;
    }

    public boolean isPunchedIn() {
        return punchedIn;
    }

    public void setPunchedIn(boolean punchedIn) {
        this.punchedIn = punchedIn;
    }
}
