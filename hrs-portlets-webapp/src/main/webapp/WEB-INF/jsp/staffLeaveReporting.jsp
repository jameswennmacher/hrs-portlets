<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="ut" uri="http://my.wisc.edu/HRSPortlets/ut"%>
<%@ taglib prefix="time" uri="http://edu.byu.portlet.hrs/HRSPortlets/time"%>
<c:set var="n"><portlet:namespace/></c:set>
<portlet:renderURL var="previousPayPeriod"><portlet:param name="payDate" value="${previousPayDate}"/></portlet:renderURL>
<portlet:renderURL var="nextPayPeriod"><portlet:param name="payDate" value="${nextPayDate}"/></portlet:renderURL>

Staff Leave Reporting!!

<div>
    <a href="${timesheetLink}">Timesheet</a>
    <a href="${leaveHistoryLink}">Leave History</a>
    <a href="${previousPayPeriod}">Previous</a>
    <a href="${nextPayPeriod}">Next</a>
</div>

<form action='<portlet:resourceURL id="updateLeave"/>' method="POST">
    <c:forEach var="tableDates" items="${listOfTableDates}" varStatus="tableNumber">
        <table>
            <tr>
                <td><spring:message code="leave.reporting.type"/></td>
                <c:forEach var="date" items="${tableDates}">
                    <td><spring:message code="leave.reporting.dow.${date.dayOfWeek}"/>&nbsp${date.dayOfMonth}</td>
                </c:forEach>
                <td><spring:message code="leave.reporting.total"/></td>
            </tr>
            <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                <tr>
                    <td><c:out value="${jobDescription.jobTitle}"/></td>
                    <c:forEach var="date" items="${tableDates}">
                        <c:set var="jobCodeAndDate">${jobDescription.jobCode}${sep}${date}</c:set>
                        <c:set var="inputName">${prefix}${sep}${jobDescription.jobCode}${sep}${date}${sep}${n}</c:set>
                        <td>
                            <c:choose>
                                <c:when test="${ut:contains(summary.displayOnlyJobCodes,jobDescription.jobCode)}">
                                    <c:out value="${time:toHhMm(entriesMap[jobCodeAndDate])}"/>
                                </c:when>
                                <c:otherwise>
                                    <input type="text" id="${inputName}" name="${inputName}" value="${time:toHhMm(entriesMap[jobCodeAndDate])}"/>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:forEach>
                    <td>${time:toHhMm(perTableJobCodeTotals[tableNumber.index][jobDescription.jobCode])}</td>
                </tr>
            </c:forEach>
            <tr>
                <td>Total</td>
                <c:forEach var="date" items="${tableDates}">
                    <td>${time:toHhMm(dayTotals[date])}</td>
                </c:forEach>
            </tr>
        </table>
    </c:forEach>

    <table>
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
    <input type="submit" value="submit"/>
</form>