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

import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

/**
 * Statement of Leave Reported for each day in a pay period
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class PayPeriodDailyLeaveTimeSummary {

    /**
     * Start date of the pay period.
     */
    LocalDate payPeriodStart;

    /**
     * End date of the pay period.
     */
    LocalDate payPeriodEnd;

    /**
     * List of job descriptions available to the employee.
     */
    List<JobDescription> jobDescriptions;

    /**
     * Set of the job codes that are display-only, for example holidays, summaries of hours worked, or periods
     * of time that the user is not allowed to enter or update data for.
     */
    Set<Integer> displayOnlyJobCodes;

    /**
     * List of leave information for each day in the pay period.  This list may not be fully populated for all days in
     * the pay period.
     */
    List<TimePeriodEntry> timePeriodEntries;
}
