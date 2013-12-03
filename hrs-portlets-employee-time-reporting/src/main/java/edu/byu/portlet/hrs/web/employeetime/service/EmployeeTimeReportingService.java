package edu.byu.portlet.hrs.web.employeetime.service;

import org.springframework.beans.factory.annotation.Autowired;

import edu.byu.portlet.hrs.web.employeetime.dao.EmployeeTimeDao;
import edu.byu.portlet.hrs.web.employeetime.model.JobClockingType;
import edu.byu.portlet.hrs.web.employeetime.model.PersonInformation;
import edu.byu.portlet.hrs.web.employeetime.model.TimeSheetInformation;

public class EmployeeTimeReportingService {

    @Autowired
    EmployeeTimeDao employeeTimeDao;
    
    PersonInformation getPersonInformation(String employeeId) {
        return employeeTimeDao.getPersonInformation(employeeId);
    }

    TimeSheetInformation getTimeSheetInformation(String employeeId) {
        return employeeTimeDao.getTimeSheetInformation(employeeId);
    }

    void clockEmployeeInOutOfJob(String employeeId, String jobId, JobClockingType jobClockingType) {
        employeeTimeDao.clockEmployeeInOutOfJob(employeeId, jobId, jobClockingType);
    }
}
