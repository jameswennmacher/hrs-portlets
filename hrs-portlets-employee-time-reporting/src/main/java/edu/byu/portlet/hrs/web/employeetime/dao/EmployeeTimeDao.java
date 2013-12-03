package edu.byu.portlet.hrs.web.employeetime.dao;

import edu.byu.portlet.hrs.web.employeetime.model.JobClockingType;
import edu.byu.portlet.hrs.web.employeetime.model.PersonInformation;
import edu.byu.portlet.hrs.web.employeetime.model.TimeSheetInformation;

/**
 * @author sconnolly
 *
 */
public interface EmployeeTimeDao {
    /**
     * Retrieve generic employee information from the employee's Id
     * @param employeeId
     * @return Employee's Information
     */
    PersonInformation getPersonInformation(String employeeId);
    /**
     * Retrieve employee's timesheet information from the employee's Id 
     * @param employeeId
     * @return Employee's Timesheet Information 
     */
    TimeSheetInformation getTimeSheetInformation(String employeeId);
    /**
     * Update employee's job by changing clocking
     * @param employeeId
     * @param jobId
     * @param jobClockingType
     */
    void clockEmployeeInOutOfJob(String employeeId, String jobId, JobClockingType jobClockingType);
}
