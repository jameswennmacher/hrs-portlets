<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<c:set var="n"><portlet:namespace/></c:set>

Staff Leave Reporting!!

<div>
    <a href="${timesheetLink}">Timesheet</a>
    <a href="${leaveHistoryLink}">Leave History</a>
</div>

<form action='<portlet:resourceURL id="updateLeave"/>' method="POST">
    <c:forEach var="tableDates" items="${listOfTableDates}">
        <table>
            <tr>
                <td><spring:message code="leave.reporting.dow.type"/></td>
                <c:forEach var="date" items="${tableDates}">
                    <td><spring:message code="leave.reporting.dow.${date.dayOfWeek}"/>&nbsp${date.dayOfMonth}</td>
                </c:forEach>
                <td>Total</td>
            </tr>
            <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                <tr>
                    <td>${jobDescription.jobTitle}</td>
                    <c:forEach var="date" items="${tableDates}">
                        <c:set var="jobCodeAndDate">${jobDescription.jobCode}${sep}${date}</c:set>
                        <c:set var="inputName">${prefix}${sep}${jobDescription.jobCode}${sep}${date}${sep}${n}</c:set>
                        <td><input type="text" id="${inputName}" name="${inputName}" value="${entriesMap[jobCodeAndDate].hoursAndMinutes}"/></td>
                    </c:forEach>
                    <td>${jobTotals[jobDescription.jobCode]}</td>
                </tr>
            </c:forEach>
            <tr>
                <td>Total</td>
                <c:forEach var="date" items="${tableDates}">
                    <td>${dayTotals[date]}</td>
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
                <td>${leaveStartBalances[jobDescription.jobCode].hoursAndMinutes}</td>
            </c:forEach>
        </tr>
        <tr>
            <td>Reported this period</td>

            <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                <td>${jobTotals[jobDescription.jobCode]}</td>
            </c:forEach>
        </tr>
        <tr>
            <td>End balance</td>
            <c:forEach var="jobDescription" items="${summary.jobDescriptions}">
                <td>${leaveEndBalances[jobDescription.jobCode].hoursAndMinutes}</td>
            </c:forEach>
        </tr>
    </table>
    <input type="submit" value="submit"/>
</form>