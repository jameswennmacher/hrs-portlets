package edu.byu.portlet.hrs.web.timereporting.staffleave;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import edu.byu.hr.HrPortletRuntimeException;
import edu.byu.hr.model.timereporting.JobDescription;
import edu.byu.hr.model.timereporting.LeaveTimeBalance;
import edu.byu.hr.model.timereporting.PayPeriodDailyLeaveTimeSummary;
import edu.byu.hr.model.timereporting.TimePeriodEntry;
import edu.byu.hr.timereporting.service.StaffTimeReportingService;
import edu.byu.portlet.hrs.web.timereporting.util.HhMmTimeUtility;
import edu.byu.portlet.hrs.web.timereporting.util.TimeParser;
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

    private static final String LEAVE_HISTORY_DEEP_LINK = "leaveHistory";
    private static final String TIMESHEET_DEEP_LINK = "timesheetDeepLink";

    private static final String INVALID_FIELD_ERROR_MESSAGE="leave.reporting.invalid.field";
    private static final String DEFAULT_INVALID_FIELD_ERROR_MESSAGE = "Invalid time value, enter as hh:mm";

    private static final String FIELD_PREFIX = "leaveItem";
    private static final String SEPARATOR = "_";
    private static final int DAYS_PER_ENTRY_TABLE = 7; // Number of days to display in a table

    private TimeParser timeParser = new HhMmTimeUtility();

    @Autowired
    StaffTimeReportingService service;

    @Autowired
    private MessageSource messageSource;

    public void setTimeParser(TimeParser timeParser) {
        this.timeParser = timeParser;
    }

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

        int daysInPayPeriod = Period.fieldDifference(summary.getPayPeriodStart(), summary.getPayPeriodEnd()).getDays();
        model.addAttribute("previousPayDate", summary.getPayPeriodStart().minusDays(daysInPayPeriod));
        model.addAttribute("nextPayDate", summary.getPayPeriodEnd().plusDays(1));

        model.addAttribute("entriesMap", createMapOfJobCodeDateEntries(summary, blankEmptyEntries));
        List<List<LocalDate>> tableDates = createListOfTableDates(summary);
        model.addAttribute("listOfTableDates", tableDates);
        model.addAttribute("dayTotals", calculateDayTotals(summary));
        Map<Integer, Integer> jobCodeTotals = calculateJobCodeTotals(summary);
        model.addAttribute("jobTotals", jobCodeTotals);
        model.addAttribute("perTableJobCodeTotals", calculatePerTableJobCodeTotals(summary, tableDates));
        addLeaveTotals(jobCodeTotals, leaveBalances, model);
        model.addAttribute("summary", summary);

        return "staffLeaveReporting";
    }

    /**
     * Create a sparsely-populated map of jobCode_date, timeEntered in minutes.
     * @param summary
     * @return
     */
    private Map<String,Integer> createMapOfJobCodeDateEntries(PayPeriodDailyLeaveTimeSummary summary,
                                                               boolean blankEmptyEntries) {
        List<TimePeriodEntry> timePeriodEntries = summary.getTimePeriodEntries();
        Map<String, Integer> entries = new HashMap<String, Integer>();
        for (TimePeriodEntry timePeriodEntry : timePeriodEntries) {
            if (!blankEmptyEntries || timePeriodEntry.getTimeEntered() > 0) {
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

    private Map<LocalDate, Integer> calculateDayTotals(PayPeriodDailyLeaveTimeSummary summary) {
        Map<LocalDate, Integer> dayTotals = new HashMap<LocalDate, Integer>();

        for (TimePeriodEntry timeEntry : summary.getTimePeriodEntries()) {
            Integer currentDayTotal = dayTotals.get(timeEntry.getDate());
            int minutes = currentDayTotal != null ? currentDayTotal : 0;
            minutes += timeEntry.getTimeEntered();
            dayTotals.put(timeEntry.getDate(), minutes);
        }
        return dayTotals;
    }

    // Create map<table#, Map<jobCode, total>>
    private Map<Integer, Map<Integer,Integer>> calculatePerTableJobCodeTotals(
                PayPeriodDailyLeaveTimeSummary summary, List<List<LocalDate>> tableDates) {

        Map<LocalDate, Integer> dateToTableNumberMap = mapDatesToTables(tableDates);

        // Create map<table#, Map<jobCode, total>>
        Map<Integer, Map<Integer,Integer>> tableJobCodeTotals = new HashMap<Integer, Map<Integer,Integer>>();
        for (int i = 0; i < tableDates.size(); i++) {
            Map<Integer,Integer> jobCodeTotals = new HashMap<Integer,Integer>();
            for (JobDescription jobDescription : summary.getJobDescriptions()) {
                jobCodeTotals.put(jobDescription.getJobCode(), 0);
            }
            tableJobCodeTotals.put(i, jobCodeTotals);
        }

        for (TimePeriodEntry timeEntry : summary.getTimePeriodEntries()) {
            // If bad date not in pay date range is in timeEntry, allow NPE since ignoring it may lead to inconsistent data
            int tableNumber = dateToTableNumberMap.get(timeEntry.getDate());
            int minutes = tableJobCodeTotals.get(tableNumber).get(timeEntry.getJobCode());
            minutes += timeEntry.getTimeEntered();
            tableJobCodeTotals.get(tableNumber).put(timeEntry.getJobCode(), minutes);
        }
        return tableJobCodeTotals;
    }

    private Map<LocalDate, Integer> mapDatesToTables(List<List<LocalDate>> tableDates) {
        Map<LocalDate, Integer> tableDateMap = new HashMap<LocalDate, Integer>();
        for (int i = 0; i < tableDates.size(); i++) {
            for (LocalDate date : tableDates.get(i)) {
                tableDateMap.put(date, i);
            }
        }
        return tableDateMap;
    }

    private Map<Integer, Integer> calculateJobCodeTotals(PayPeriodDailyLeaveTimeSummary summary) {
        Map<Integer, Integer> jobTotals = new HashMap<Integer, Integer>();

        // Pre-initialize all job totals in case we don't have data for some
        for (JobDescription jobDescription : summary.getJobDescriptions()) {
            jobTotals.put(jobDescription.getJobCode(), 0);
        }

        for (TimePeriodEntry timeEntry : summary.getTimePeriodEntries()) {
            // In case of bad jobCode data in timeEntry, allow NPE since not sure what to do with it.
            int minutes = jobTotals.get(timeEntry.getJobCode());
            minutes += timeEntry.getTimeEntered();
            jobTotals.put(timeEntry.getJobCode(), minutes);
        }
        return jobTotals;
    }

    private void addLeaveTotals(Map<Integer, Integer> jobCodeTotals, List<LeaveTimeBalance> leaveBalancesList,
                                ModelMap model) {
        // Calculate the leave starting balances by taking away the job totals and store both as a map<jobCode, minutes>
        Map<Integer, Integer> leaveStartBalances = new HashMap<Integer, Integer>();
        Map<Integer, Integer> leaveEndBalances = new HashMap<Integer, Integer>();
        for (LeaveTimeBalance endBalance : leaveBalancesList) {
            int startBalance = endBalance.getTimeAvailable() - jobCodeTotals.get(endBalance.getJobCode());
            leaveStartBalances.put(endBalance.getJobCode(), startBalance);

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
                try {
                    String[] fields = parameterName.split(SEPARATOR);
                    String jobCode = fields[1];
                    String dateString = fields[2];
                    int timeValue = timeParser.computeMinutes(request.getParameter(parameterName));
                    userEntries.add(new TimePeriodEntry(LocalDate.parse(dateString), Integer.parseInt(jobCode), timeValue));
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
