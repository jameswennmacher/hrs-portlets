package edu.byu.portlet.hrs.web.timereporting.staffleave;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import edu.byu.hr.HrPortletRuntimeException;
import edu.byu.hr.model.timereporting.Duration;
import edu.byu.hr.model.timereporting.JobDescription;
import edu.byu.hr.model.timereporting.LeaveTimeBalance;
import edu.byu.hr.model.timereporting.PayPeriodDailyLeaveTimeSummary;
import edu.byu.hr.model.timereporting.TimePeriodEntry;
import edu.byu.hr.timereporting.service.StaffTimeReportingService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

@Controller
@RequestMapping("VIEW")
public class StaffLeaveReportingController {

    private static final String FIELD_PREFIX = "leaveItem";
    private static final String SEPARATOR = "_";
    private static final int DAYS_PER_ENTRY_TABLE = 7; // Number of days to display in a table
    private static final String DEFAULT_INVALID_FIELD_ERROR_MESSAGE = "Invalid time value, enter as hh:mm";
    public static final String INVALID_FIELD_ERROR_MESSAGE="leave.reporting.invalid.field";

    public static final String LEAVE_HISTORY_DEEP_LINK = "leaveHistory";
    public static final String TIMESHEET_DEEP_LINK = "timesheetDeepLink";

    @Autowired
    StaffTimeReportingService service;

    @Autowired
    private MessageSource messageSource;

    @RequestMapping
    public String viewStaffLeaveReportingInfo(ModelMap model, PortletRequest request,
                                              @RequestParam(value = "payDate", required = false) String payDateStart) {
//        final String emplId = PrimaryAttributeUtils.getPrimaryId();
        final String emplId = request.getRemoteUser();

        LocalDate date = StringUtils.isNotBlank(payDateStart) ? LocalDate.parse(payDateStart) : new LocalDate();

        List<LeaveTimeBalance> leaveBalances = service.getLeaveBalance(request, emplId);
        PayPeriodDailyLeaveTimeSummary summary =  service.getLeaveHoursReported(request, emplId, date);

        PortletPreferences prefs = request.getPreferences();
        model.addAttribute("timesheetLink", prefs.getValue(TIMESHEET_DEEP_LINK, "http://localhost:8080/specify-timesheetDeepLink-preference"));
        model.addAttribute("leaveHistoryLink", prefs.getValue(LEAVE_HISTORY_DEEP_LINK, "http://localhost:8080/enterLeaveHistoryUrlHere"));

        model.addAttribute("prefix", FIELD_PREFIX);
        model.addAttribute("sep", SEPARATOR);

        boolean blankEmptyEntries = Boolean.parseBoolean(prefs.getValue("blankZeroTimeValues", "true"));
        model.addAttribute("entriesMap", createMapOfJobCodeDateEntries(summary, blankEmptyEntries));
        model.addAttribute("listOfTableDates", createListOfTableDates(summary));
        model.addAttribute("dayTotals", calculateDayTotals(summary));
        addJobAndLeaveTotals(summary, leaveBalances, model);
        model.addAttribute("summary", summary);

        return "staffLeaveReporting";
    }

    /**
     * Create a sparsely-populated map of jobCode.date, timeEntered.
     * @param summary
     * @return
     */
    private Map<String,Duration> createMapOfJobCodeDateEntries(PayPeriodDailyLeaveTimeSummary summary,
                                                               boolean blankEmptyEntries) {
        List<TimePeriodEntry> timePeriodEntries = summary.getTimePeriodEntries();
        Map<String, Duration> entries = new HashMap<String, Duration>();
        for (TimePeriodEntry timePeriodEntry : timePeriodEntries) {
            if (!blankEmptyEntries || timePeriodEntry.getTimeEntered().getMinutes() > 0) {
                entries.put(timePeriodEntry.getJobCode() + SEPARATOR + timePeriodEntry.getDate(), timePeriodEntry.getTimeEntered());
            }
        }
        return entries;
    }

    private List<List<LocalDate>> createListOfTableDates(PayPeriodDailyLeaveTimeSummary summary) {
        int daysInPayPeriod = Period.fieldDifference(summary.getPayPeriodStart(), summary.getPayPeriodEnd()).getDays();
        List<List<LocalDate>> tables = new ArrayList<List<LocalDate>>();
        List<LocalDate> tableDates = new ArrayList<LocalDate>();
        for (int i = 0; i < daysInPayPeriod; i++) {
            if (i > 0 && i % DAYS_PER_ENTRY_TABLE == 0) {
                tables.add(tableDates);
                tableDates = new ArrayList<LocalDate>();
            }
            tableDates.add(summary.getPayPeriodStart().plusDays(i));
        }
        tables.add(tableDates);
        return tables;
    }

    private Map<LocalDate, String> calculateDayTotals(PayPeriodDailyLeaveTimeSummary summary) {
        Map<LocalDate, Integer> dayTotals = new HashMap<LocalDate, Integer>();
        Map<LocalDate, String> dayTotalsAsTime = new HashMap<LocalDate, String>();

        for (TimePeriodEntry timeEntry : summary.getTimePeriodEntries()) {
            Integer currentDayTotal = dayTotals.get(timeEntry.getDate());
            int minutes = currentDayTotal != null ? currentDayTotal : 0;
            minutes += timeEntry.getTimeEntered().getMinutes();
            dayTotals.put(timeEntry.getDate(), minutes);
            dayTotalsAsTime.put(timeEntry.getDate(), Duration.asHoursAndMinutes(minutes));
        }
        return dayTotalsAsTime;
    }

    private void addJobAndLeaveTotals(PayPeriodDailyLeaveTimeSummary summary,
                                                      List<LeaveTimeBalance> leaveBalancesList, ModelMap model) {
        Map<Integer, Integer> jobTotals = new HashMap<Integer, Integer>();
        Map<Integer, String> jobTotalsAsTime = new HashMap<Integer, String>();

        // Pre-initialize all job totals in case we don't have data for some
        for (JobDescription jobDescription : summary.getJobDescriptions()) {
            jobTotals.put(jobDescription.getJobCode(), 0);
        }

        for (TimePeriodEntry timeEntry : summary.getTimePeriodEntries()) {
            Integer currentJobCodeTotal = jobTotals.get(timeEntry.getJobCode());
            int minutes = currentJobCodeTotal != null ? currentJobCodeTotal : 0;
            minutes += timeEntry.getTimeEntered().getMinutes();
            jobTotals.put(timeEntry.getJobCode(), minutes);
            jobTotalsAsTime.put(timeEntry.getJobCode(), Duration.asHoursAndMinutes(minutes));
        }
        model.addAttribute("jobTotals", jobTotalsAsTime);

        // Calculate the leave starting balances by taking away the job totals and store both as a map<jobCode, Duration
        Map<Integer, Duration> leaveStartBalances = new HashMap<Integer, Duration>();
        Map<Integer, Duration> leaveEndBalances = new HashMap<Integer, Duration>();
        for (LeaveTimeBalance endBalance : leaveBalancesList) {
            int startBalance = endBalance.getTimeAvailable().getMinutes() - jobTotals.get(endBalance.getJobCode());
            leaveStartBalances.put(endBalance.getJobCode(), new Duration(startBalance));

            leaveEndBalances.put(endBalance.getJobCode(), endBalance.getTimeAvailable());
        }
        model.addAttribute("leaveStartBalances", leaveStartBalances);
        model.addAttribute("leaveEndBalances", leaveEndBalances);
    }

    @ResourceMapping(value="updateLeave")
    public String updateLeave(ResourceRequest request, ResourceResponse response, ModelMap map) {
//        final String emplId = PrimaryAttributeUtils.getPrimaryId();
        final String emplId = request.getRemoteUser();

        String errorMessage = null;
        List<String> invalidFields = new ArrayList<String>();
        List<TimePeriodEntry> userEntries = new ArrayList<TimePeriodEntry>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            // Field names have format:  PREFIX.jobCode.YYYY-mm-dd.crazyPortletIdValue
            if (parameterName.startsWith(FIELD_PREFIX)) {
                String timeValue = request.getParameter(parameterName);
                try {
                    String[] fields = parameterName.split(SEPARATOR);
                    String jobCode = fields[1];
                    String dateString = fields[2];
                    Duration duration = new Duration(timeValue);
                    userEntries.add(new TimePeriodEntry(LocalDate.parse(dateString), Integer.parseInt(jobCode), duration));
                } catch (HrPortletRuntimeException e) {
                    invalidFields.add(parameterName);
                    errorMessage = messageSource.getMessage(INVALID_FIELD_ERROR_MESSAGE, null,
                            DEFAULT_INVALID_FIELD_ERROR_MESSAGE, request.getLocale());
                }

            }
        }
        if (errorMessage == null && invalidFields.size() == 0) {
            try {
                service.updateLeaveTimeReported(request, emplId, userEntries);
            } catch (HrPortletRuntimeException e) {
                errorMessage = e.getMessage();
            }
        }
        if (errorMessage == null && invalidFields.size() == 0) {
            map.addAttribute("success", true);
        } else {
            map.addAttribute("error_message", errorMessage);
            map.addAttribute("fields", invalidFields);
        }
        return "jsonView";
    }

}
