package edu.byu.portlet.hrs.web.employeetime;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.omg.CORBA.portable.OutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import edu.wisc.hr.dm.bnsumm.BenefitSummary;
import edu.wisc.web.security.portlet.primaryattr.PrimaryAttributeUtils;

@Controller
@RequestMapping("VIEW")
public class EmployeeTimeReportingController {

    @RequestMapping
    public String viewEmployeeTimeReportingInfo(ModelMap model, PortletRequest request) {
        //final String emplId = PrimaryAttributeUtils.getPrimaryId();
        
        return "employeeTimeReporting";
    }
    
    @ResourceMapping(value = "employeeTimeRequest")
    public String test(ModelMap modelMap, @RequestParam("test") final int test) throws IOException {
        System.out.println("In Employee Controller");
        System.out.println("test:" + test);
        modelMap.addAttribute("test", new TestResponseModel());
        
        return "jsonView";
    }
}
