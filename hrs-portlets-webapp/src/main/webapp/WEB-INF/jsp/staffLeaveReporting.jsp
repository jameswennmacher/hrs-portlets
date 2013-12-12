<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/include.jsp"%>

Staff Leave Reporting!!

<div>
    <a href="${timesheetLink}">Timesheet</a>
    <a href="${leaveHistoryLink}">Leave History</a>
</div>

<form action='<portlet:actionURL><portlet:param name="action" value="submit"/></portlet:actionURL>' method="POST">
    <c:forEach var="tableDates" items="${listOfTableDates}">
        <table>
            <tr>
                <td>Type</td>
                <c:forEach var="date" items="${tableDates}">
                    <td>DOW-${date.dayOfWeek}&nbsp${date.dayOfMonth}</td>
                </c:forEach>
                <td>Total</td>
            </tr>
            <c:forEach var="jobDescriptions" items="${summary.jobDescriptions}">
                <tr>
                    <td>${jobDescriptions.jobTitle}</td>
                    <c:forEach var="date" items="${tableDates}">
                        <c:set var="jobCodeAndDate">${jobDescriptions.jobCode}.${date}</c:set>
                        <c:set var="inputName">${prefix}.${jobDescriptions.jobCode}.${date}</c:set>
                        <td><input type="text" name="${inputName}" value="${entriesMap[jobCodeAndDate]}"/></td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
    </c:forEach>
    <input type="submit" value="submit"/>
</form>