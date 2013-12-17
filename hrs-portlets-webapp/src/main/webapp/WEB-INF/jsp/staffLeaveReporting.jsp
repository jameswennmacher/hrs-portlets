<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ include file="/WEB-INF/jsp/header.jsp"%>

<%@ taglib prefix="ut" uri="http://my.wisc.edu/HRSPortlets/ut"%>
<%@ taglib prefix="time" uri="http://edu.byu.portlet.hrs/HRSPortlets/time"%>
<c:set var="n"><portlet:namespace/></c:set>

<%-- values are in minutes.  day = 24x60, week = 40x60 --%>
<c:set var="weekMaxTime">2400</c:set>
<c:set var="dayMaxTime">1440</c:set>

<portlet:renderURL var="previousPayPeriod"><portlet:param name="payDate" value="${previousPayDate}"/></portlet:renderURL>
<portlet:renderURL var="nextPayPeriod"><portlet:param name="payDate" value="${nextPayDate}"/></portlet:renderURL>

<style type="text/css">
        .leave-entry-alerts {
                display: none;
        }

/*        .leave-entry .leave-entry-input tbody tr td.leave-entry-total-error {
                color: #900;
        }*/
</style>

<link href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css" rel="stylesheet">

<form class="leave-entry-form" name="leave-entry-form" action='<portlet:resourceURL id="updateLeave"/>' method="POST">
        <div class="bootstrap-styles leave-entry">
                <div class="leave-entry-alerts alert"></div>
                
                <div class="leave-entry-nav">
                        <a href="${previousPayPeriod}" class="pull-left"> <i class="fa fa-arrow-circle-o-left"></i>
                                <span class="sr-only">Previous</span>
                        </a>
                        <span class="leave-entry-period">For the Pay Period of: Sept 28 - Oct 11, 2013</span>
                        <a href="${nextPayPeriod}" class="pull-right"> <i class="fa fa-arrow-circle-o-right"></i>
                                <span class="sr-only">Next</span>
                        </a>
                </div>

                <div class="leave-entry-input">
                        <div class="leave-entry-weekend-toggle">
                                <label>
                                <input type="checkbox" class="toggleWeekends" name="${n}toggleWeekends" id="${n}toggleWeekends" />
                                Show Sat/Sun</label>
                        </div>

                        <c:forEach var="tableDates" items="${listOfTableDates}" varStatus="tableNumber">
                                <table class="table table-bordered table-striped leave-entry-table">
                                        <thead>
                                                <tr>
                                                        <th>
                                                                <spring:message code="leave.reporting.type"/>        
                                                        </th>
                                                        <c:forEach var="date" items="${tableDates}">
                                                                <th>
                                                                        <spring:message code="leave.reporting.dow.${date.dayOfWeek}"/>        
                                                                        <br />        
                                                                        ${date.dayOfMonth}
                                                                </th>
                                                        </c:forEach>
                                                        <th>
                                                                <spring:message code="leave.reporting.total"/>        
                                                        </th>
                                                </tr>
                                        </thead>
                                        <tbody>
                                                <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                                                        <tr>
                                                                <td title="${jobDescription.jobTitle}">
                                                                        <c:out value="${jobDescription.jobTitle}"/>        
                                                                </td>
                                                                <c:forEach var="date" items="${tableDates}">
                                                                        <c:set var="jobCodeAndDate">${jobDescription.jobCode}${sep}${date}</c:set>
                                                                        <c:set var="inputName">
                                                                                ${prefix}${sep}${jobDescription.jobCode}${sep}${date}${sep}${n}
                                                                        </c:set>
                                                                        <c:choose>
                                                                                <c:when test="${entriesMap[jobCodeAndDate] > dayMaxTime}">
                                                                                        <c:set var="cellErrorClass">danger</c:set>
                                                                                </c:when>
                                                                                <c:otherwise>
                                                                                        <c:set var="cellErrorClass"></c:set>
                                                                                </c:otherwise>
                                                                        </c:choose>
                                                                        <td class="${cellErrorClass}">
                                                                                <c:choose>
                                                                                        <c:when test="${ut:contains(summary.displayOnlyJobCodes,jobDescription.jobCode)}">
                                                                                                <c:out value="${time:toHhMm(entriesMap[jobCodeAndDate])}"/>        
                                                                                        </c:when>
                                                                                        <c:otherwise>
                                                                                                <label>
                                                                                                        <span class="sr-only">Time Entry for ${date}</span>
<%-- The logic commented out here doesn't seem to work.
                                                                                                        <c:choose>
                                                                                                                <c:when test="${empty entriesMap[JobCodeAndDate]}">
                                                                                                                        <c:set var="fieldValue"></c:set>
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
--%>
                                                                                                                        <c:set var="fieldValue">${time:toHhMm(entriesMap[jobCodeAndDate])}</c:set>
<%--
                                                                                                                </c:otherwise>
                                                                                                        </c:choose>
--%>
                                                                                                        <c:choose>
                                                                                                                <c:when test="${entriesMap[jobCodeAndDate] > dayMaxTime}">
                                                                                                                        <c:set var="fieldErrorClass">leave-entry-error</c:set>
                                                                                                                </c:when>
                                                                                                                <c:otherwise>
                                                                                                                        <c:set var="fieldErrorClass"></c:set>
                                                                                                                </c:otherwise>
                                                                                                        </c:choose>
                                                                                                        <input type="text" class="form-control input-sm ${fieldErrorClass}" value="${fieldValue}" name="${inputName}" id="${inputName}" />        
                                                                                                </label>
                                                                                        </c:otherwise>
                                                                                </c:choose>
                                                                        </td>
                                                                </c:forEach>
                                                                <c:choose>
                                                                        <c:when test="${perTableJobCodeTotals[tableNumber.index][jobDescription.jobCode] > weekMaxTime}">
                                                                                <c:set var="rowTotalClass">leave-entry-total-error</c:set>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                                <c:set var="rowTotalClass"></c:set>
                                                                        </c:otherwise>
                                                                </c:choose>
                                                                <td class="${rowTotalClass}">
                                                                        ${time:toHhMm(perTableJobCodeTotals[tableNumber.index][jobDescription.jobCode])}
                                                                </td>
                                                        </tr>
                                                </c:forEach>
                                                <tr>
                                                        <td>Total</td>
                                                        <c:forEach var="date" items="${tableDates}">
                                                                <c:choose>
                                                                        <c:when test="${dayTotals[date] > dayMaxTime}">
                                                                                <c:set var="columnTotalClass">leave-entry-total-error</c:set>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                                <c:set var="columnTotalClass"></c:set>
                                                                        </c:otherwise>
                                                                </c:choose>
                                                                <td class="${columnTotalClass}">${time:toHhMm(dayTotals[date])}</td>
                                                        </c:forEach>
                                                        <td></td>
                                                </tr>
                                        </tbody>
                                </table>
                        </c:forEach>
                </div>

                <div class="leave-entry-balances">
                        <table class="table">
                                <tr>
                                        <td></td>
                                        <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                                                <td>${jobDescription.jobTitle}</td>
                                        </c:forEach>
                                </tr>
                                <tr>
                                        <td>Start balance</td>
                                        <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                                                <!-- Display values only for leave job codes and not worked time -->        
                                                <c:choose>
                                                        <c:when test="${not empty leaveStartBalances[jobDescription.jobCode]}">
                                                                <td>${time:toHhMm(leaveStartBalances[jobDescription.jobCode])}</td>
                                                        </c:when>
                                                        <c:otherwise>
                                                                <td></td>
                                                        </c:otherwise>
                                                </c:choose>
                                        </c:forEach>
                                </tr>
                                <tr>
                                        <td>Reported this period</td>
                                        <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                                                <td>${time:toHhMm(jobTotals[jobDescription.jobCode])}</td>
                                        </c:forEach>
                                </tr>
                                <tr>
                                        <td>End balance</td>
                                        <!-- Display values only for leave job codes and not worked time -->        
                                        <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                                                <c:choose>
                                                        <c:when test="${not empty leaveEndBalances[jobDescription.jobCode]}">
                                                                <td>${time:toHhMm(leaveEndBalances[jobDescription.jobCode])}</td>
                                                        </c:when>
                                                        <c:otherwise>
                                                                <td></td>
                                                        </c:otherwise>
                                                </c:choose>
                                        </c:forEach>
                                </tr>
                        </table>
                        <div class="leave-entry-buttons">
                                <a href="${leaveHistoryLink}" class="btn btn-default btn-sm" title="My Timesheet" role="button">My Leave History</a>
                                <a href="${timesheetLink}" class="btn btn-default btn-sm" title="My ACA Hours" role="button">My Timesheet</a>
                                <a href="" class="btn btn-primary btn-sm pull-right leave-entry-submit disabled" title="Submit" role="button">Submit</a>
                        </div>
                </div>
        </div>
</form>

<script src="${pageContext.request.contextPath}/js/leaveEntry.js" type="text/javascript" ></script>
<script type="text/javascript">
        $( document ).ready(function() {
                $('form.leave-entry-form').leaveEntryTimeTotalCalculations();
        });
</script>