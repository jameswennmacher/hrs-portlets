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

package edu.byu.hr.timereporting.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.byu.hr.dao.timereporting.StaffTimeReportingDao;
import edu.byu.hr.model.timereporting.JobDescription;
import edu.byu.hr.model.timereporting.LeaveTimeBalance;
import edu.byu.hr.model.timereporting.PayPeriodDailyLeaveTimeSummary;
import edu.byu.hr.model.timereporting.TimePeriodEntry;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Description
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */
@Repository
public class DemoStaffTimeReportingImpl implements StaffTimeReportingDao {
    private static final int SATURDAY = 6;  // Day of week for Saturday, the start of the week for this
    private static final int DAYS_IN_PAY_PERIOD = 14;
    private static final int WORKED = 0;
    private static final int SICK = 201;
    private static final int VACATION = 202;

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private String jsonDataDir = "/data/demo/timereporting/";
    private String jsonDefaultInputFile = "default.json";
    private List<JobDescription> jobDescriptions;
    private Set<Integer> uneditableJobs;
    private Set<Integer> allUneditableJobs;

    // Map<emplId, List<LeaveTimeBalance>
    private Map<String, List<LeaveTimeBalance>> emplLeaveBalances = Collections.synchronizedMap(
            new HashMap<String, List<LeaveTimeBalance>>());

    // Map<emplId, List<TimePeriodEntry>
    private Map<String, List<TimePeriodEntry>> emplLeaveEntries = Collections.synchronizedMap(
            new HashMap<String, List<TimePeriodEntry>>());

    DemoStaffTimeReportingImpl() {
        initJobDescriptions();
        initUneditableJobs();
    }

    private void initJobDescriptions() {
        List<JobDescription> jobDesc = new ArrayList<JobDescription>();
        jobDesc.add(new JobDescription(WORKED, "Hours", "Hours Worked"));
        jobDesc.add(new JobDescription(SICK, "Sick", "Sick Time"));
        jobDesc.add(new JobDescription(VACATION, "Vacation", "Vacation Time"));
        jobDescriptions = Collections.unmodifiableList(jobDesc);
    }

    private void initUneditableJobs() {
        Set<Integer> uneditable = new HashSet<Integer>();
        uneditable.add(WORKED);
        uneditableJobs = Collections.unmodifiableSet(uneditable);

        Set<Integer> allUneditable = new HashSet<Integer>();
        allUneditable.addAll(Arrays.asList(new Integer[] {WORKED, SICK, VACATION}));
        allUneditableJobs = Collections.unmodifiableSet(allUneditable);
    }

    private void initializeForEmployeeIfNeeded (String emplId) {
        if (emplLeaveBalances.get(emplId) == null) {
            List<LeaveTimeBalance> leaveBalances = new ArrayList<LeaveTimeBalance>();
            leaveBalances.add(new LeaveTimeBalance(SICK, 215*60+4)); // 215 hours, 4 min
            leaveBalances.add(new LeaveTimeBalance(VACATION, 64*60+10)); //64:10
            emplLeaveBalances.put(emplId, leaveBalances);
        }
        if (emplLeaveEntries.get(emplId) == null) {
            LocalDate startDate = calculatePayperiodStartDate(new LocalDate());
            List<TimePeriodEntry> items = new ArrayList<TimePeriodEntry>();
            for (int i = 2; i < DAYS_IN_PAY_PERIOD; i++) {
                // Skip Sat and Sun since they by default don't display
                LocalDate date = startDate.plusDays(i);
                if (date.getDayOfWeek() < 6) {
                    items.add(new TimePeriodEntry(date, WORKED, date.getDayOfWeek()*60+15));
                }
            }
            emplLeaveEntries.put(emplId, items);
        }
    }

    private LocalDate calculatePayperiodStartDate(LocalDate date) {
        return date.minusDays(5).withDayOfWeek(SATURDAY);
    }

    @Override
    public PayPeriodDailyLeaveTimeSummary getLeaveHoursReported(String emplId, LocalDate dateInPayPeriod) {
        initializeForEmployeeIfNeeded(emplId);

        LocalDate startDate = calculatePayperiodStartDate(dateInPayPeriod);

        PayPeriodDailyLeaveTimeSummary summary = new PayPeriodDailyLeaveTimeSummary();
        summary.setPayPeriodStart(startDate);
        summary.setPayPeriodEnd(startDate.plusDays(DAYS_IN_PAY_PERIOD));
        summary.setJobDescriptions(jobDescriptions);
        summary.setDisplayOnlyJobCodes(uneditableJobs);
        summary.setTimePeriodEntries(getTimeEntries(emplId, startDate));

        return summary;
    }

    private List<TimePeriodEntry> getTimeEntries (String emplId, LocalDate startDate) {
        // todo get entries for a particular date range
        List<TimePeriodEntry> entries = emplLeaveEntries.get(emplId);
        return entries;
    }

    @Override
    public void updateLeaveTimeReported(String emplId, List<TimePeriodEntry> updatedTimesheet) {
        initializeForEmployeeIfNeeded(emplId);
        log.debug("Updating leave entries for employee ID {}", emplId);
        // todo Currently just replaces all current entries.
        emplLeaveEntries.put(emplId, updatedTimesheet);
    }

    @Override
    public List<LeaveTimeBalance> getLeaveBalance(String emplId) {
        initializeForEmployeeIfNeeded(emplId);
        return emplLeaveBalances.get(emplId);
    }
}
