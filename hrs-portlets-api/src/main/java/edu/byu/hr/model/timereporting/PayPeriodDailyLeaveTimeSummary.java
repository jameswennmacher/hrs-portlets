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

import org.joda.time.LocalDate;

/**
 * Statement of Leave Reported for each day in a pay period
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public class PayPeriodDailyLeaveTimeSummary {

    /**
     * Start of pay period.  End of pay period can be determined based on number of entries in DailyLeaveSummary.
     */
    LocalDate payPeriodStart;

    /**
     * List of leave information for each day in the pay period.  This list is fully populated for all days in
     * the pay period.  Days with no time reported will have an entry with all 0s.
     */
    List<DailyLeaveSummary> dailyHoursSummary;
}
