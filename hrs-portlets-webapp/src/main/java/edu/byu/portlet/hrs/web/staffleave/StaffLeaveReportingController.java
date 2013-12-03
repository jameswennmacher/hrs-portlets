package edu.byu.portlet.hrs.web.staffleave;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.omg.CORBA.portable.OutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import edu.wisc.web.security.portlet.primaryattr.PrimaryAttributeUtils;

@Controller
@RequestMapping("VIEW")
public class StaffLeaveReportingController {
    
    @RequestMapping
    public String viewStaffLeaveReportingInfo(ModelMap model, PortletRequest request) {
        //final String emplId = PrimaryAttributeUtils.getPrimaryId();
        
        return "staffLeaveReporting";
    }
    
    @ResourceMapping(value = "staffLeaveRequest")
    public void test(ResourceRequest request, ResourceResponse response) throws IOException {
        System.out.println("In Staff Controller");
        OutputStream outStream = (OutputStream) response.getPortletOutputStream();
        outStream.write("{staffLeaveRequest succeeded}".getBytes());
    }
}
