package edu.byu.portlet.hrs.web.timereporting.staffleave;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import edu.byu.hr.model.timereporting.LeaveTimeBalance;
import edu.byu.hr.model.timereporting.PayPeriodDailyLeaveTimeSummary;
import edu.byu.hr.model.timereporting.TimePeriodEntry;
import edu.byu.hr.timereporting.service.StaffTimeReportingService;
import edu.byu.hr.timereporting.util.TimeCalculations;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.omg.CORBA.portable.OutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

@Controller
@RequestMapping("VIEW")
public class StaffLeaveReportingController {

    private static final String FIELD_PREFIX = "leaveItem";
    private static final String SEPARATOR = ".";
    private static final int DAYS_PER_ENTRY_TABLE = 7; // Number of days to display in a table

    @Autowired
    StaffTimeReportingService service;
    
    @RequestMapping
    public String viewStaffLeaveReportingInfo(ModelMap model, PortletRequest request,
                                              @RequestParam(value = "payDate", required = false) String payDateStart) {
//        final String emplId = PrimaryAttributeUtils.getPrimaryId();
        final String emplId = request.getRemoteUser();

        LocalDate date = StringUtils.isNotBlank(payDateStart) ? LocalDate.parse(payDateStart) : new LocalDate();

        List<LeaveTimeBalance> leaveBalance = service.getLeaveBalance(request, emplId);
        PayPeriodDailyLeaveTimeSummary summary =  service.getLeaveHoursReported(request, emplId, date);

        model.addAttribute("prefix", FIELD_PREFIX);
        model.addAttribute("leaveBalance", leaveBalance);
        model.addAttribute("entriesMap", createMapOfJobCodeDateEntries(summary));
        model.addAttribute("listOfTableDates", createListOfTableDates(summary));
        model.addAttribute("summary", summary);

        return "staffLeaveReporting";
    }

    /**
     * Create a sparsely-populated map of jobCode.date, timeEntered.
     * @param summary
     * @return
     */
    private Map<String, String> createMapOfJobCodeDateEntries(PayPeriodDailyLeaveTimeSummary summary) {
        List<TimePeriodEntry> timePeriodEntries = summary.getTimePeriodEntries();
        Map<String, String> entries = new HashMap<String, String>();
        for (TimePeriodEntry timePeriodEntry : timePeriodEntries) {
            entries.put(timePeriodEntry.getJobCode() + SEPARATOR + timePeriodEntry.getDate(), timePeriodEntry.getTimeEntered());
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

    private void addDayTotalsToModel(ModelMap model, PayPeriodDailyLeaveTimeSummary summary) {
//        int daysInPayPeriod = Period.fieldDifference(summary.getPayPeriodStart(), summary.getPayPeriodEnd()).getDays();
//        Map<LocalDate, Integer> dayTotals = new HashMap<LocalDate, Integer>();
//        for (TimePeriodEntry timeEntry : summary.getTimePeriodEntries()) {
//            int minutes = dayTotals.get(timeEntry.getDate()) != null ? dayTotals.get(timeEntry.getDate()) : 0;
//            dayTotals.put(timeEntry.getDate(), minutes + TimeCalculations.computeMinutes(timeEntry.get))
//        }
    }

    @ActionMapping(params = "action=updateLeave")
    public void updateLeave(ActionRequest request, ActionResponse response) {
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if (parameterName.startsWith(FIELD_PREFIX)) {
                String value = request.getParameter(parameterName);

                // Field names have format:  PREFIX.jobCode.YYYY-mm-dd

            }

        }
    }

    @ResourceMapping(value = "staffLeaveRequest")
    public void test(ResourceRequest request, ResourceResponse response) throws IOException {
        System.out.println("In Staff Controller");
        OutputStream outStream = (OutputStream) response.getPortletOutputStream();
        outStream.write("{staffLeaveRequest succeeded}".getBytes());
    }
}
