package edu.byu.portlet.hrs.web.timereporting.timepunch;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;

import edu.byu.hr.model.timereporting.TimePunchEntry;
import edu.byu.hr.timereporting.service.StaffTimePunchService;
import edu.byu.hr.timereporting.util.TimeCalculations;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

@Controller
@RequestMapping("VIEW")
public class StaffTimePunchController {
    public static final String ACAHOURS_DEEP_LINK = "hoursDeepLink2";
    public static final String TIMESHEET_DEEP_LINK = "timesheetDeepLink";

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StaffTimePunchService service;

    @RequestMapping(params="action=refresh")
    public void refresh(ActionRequest request, ActionResponse response) {
        log.debug("Refreshing data");
        response.setRenderParameter("refresh", "true");
    }

    // Render phase
    @RequestMapping
    public String viewEmployeeTimeReportingInfo(ModelMap model, PortletRequest request,
                                                @RequestParam(value = "refresh", required = false) boolean refresh) {
//        final String emplId = PrimaryAttributeUtils.getPrimaryId();
          final String emplId = request.getRemoteUser();
        log.debug("Rendering time punch for employee ID {}, refresh={}", emplId, refresh);
        List<TimePunchEntry> jobEntries = service.getTimePunchEntries(request, emplId, refresh);
        model.addAttribute("jobEntries", jobEntries);
        addSummaryTimeEntries(model, jobEntries, emplId);

        PortletPreferences prefs = request.getPreferences();
        model.addAttribute("timesheetLink", prefs.getValue(TIMESHEET_DEEP_LINK, "http://localhost:8080/specify-timesheetDeepLink-preference"));
        model.addAttribute("timesheetLink2", prefs.getValue(ACAHOURS_DEEP_LINK, "http://localhost:8080/specify-hoursDeepLink2-preference"));

        return "employeeTimeReporting";
    }

    private void addSummaryTimeEntries(ModelMap model, List<TimePunchEntry> jobEntries, String emplId) {
        Period weekTotal = new Period();
        Period payPeriodTotal = new Period();

        for (TimePunchEntry jobEntry : jobEntries) {
            weekTotal = TimeCalculations.addTime(weekTotal, jobEntry.getWeekTimeWorked());
            weekTotal = TimeCalculations.addTime(payPeriodTotal, jobEntry.getPayPeriodTimeWorked());
        }
        model.addAttribute("weekTotal", weekTotal.getHours() + ":" + weekTotal.getMinutes());
        model.addAttribute("payPeriodTotal", payPeriodTotal.getHours() + ":" + payPeriodTotal.getMinutes());
    }

    @ActionMapping(params = "action=punchOut")
    public void punchOut(ActionRequest request, ActionResponse response, @RequestParam("jobCode") final int jobCode) {
//        final String emplId = PrimaryAttributeUtils.getPrimaryId();
        final String emplId = request.getRemoteUser();
        log.debug("Punching out employee ID {} for job code {}", emplId, jobCode);

        service.punchOutTimeClock(request, emplId, jobCode, "127.0.0.1");
        response.setRenderParameter("message", "You have been punched out");
    }

    @ActionMapping(params = "action=punchIn")
    public void punchIn(ActionRequest request, ActionResponse response, @RequestParam("jobCode") final int jobCode) {
//        final String emplId = PrimaryAttributeUtils.getPrimaryId();
        final String emplId = request.getRemoteUser();
        log.debug("Punching in employee ID {} for job code {}", emplId, jobCode);

        service.punchInTimeClock(request, emplId, jobCode, "127.0.0.1");
        response.setRenderParameter("message", "You have been punched in");
    }
}
