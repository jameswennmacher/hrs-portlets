<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/WEB-INF/jsp/include.jsp"%>

<portlet:actionURL var="refreshUrl">
    <portlet:param name="action" value="refresh"/>
    <portlet:param name="refresh" value="true"/>
</portlet:actionURL>

Employee Time Reporting!!

<div>
    <a href="${timesheetLink}">Timesheet</a>
    <a href="${timesheetLink2}">ACA Hours</a>
    <a href="${refreshUrl}">Refresh</a>
    Week total: ${weekTotal}
    Pay Period total: ${payPeriodTotal}
    Entries:
    <table>
        <thead>
        <td>jobcode</td>
        <td>Title</td>
        <td>Description</td>
        <td>week</td>
        <td>pay period</td>
        <td>punched in</td>
        <td>In</td>
        <td>Out</td>
        </thead>
        <tbody>
        <c:forEach var="timePunchEntry" items="${jobEntries}">
            <tr>
                <td>${timePunchEntry.job.jobCode}</td>
                <td>${timePunchEntry.job.jobTitle}</td>
                <td>${timePunchEntry.job.jobDescription}</td>
                <td>${timePunchEntry.weekTimeWorked}</td>
                <td>${timePunchEntry.payPeriodTimeWorked}</td>
                <td>${timePunchEntry.punchedIn}</td>
                <portlet:resourceURL var="punchInUrl" id="punchIn">
                    <portlet:param name="jobCode" value="${timePunchEntry.job.jobCode}"/>
                </portlet:resourceURL>
                <td><a href="${punchInUrl}">In</a></td>
                <portlet:actionURL var="punchOutUrl">
                    <portlet:param name="action" value="punchOut"/>
                    <portlet:param name="jobCode" value="${timePunchEntry.job.jobCode}"/>
                </portlet:actionURL>
                <td><a href="${punchOutUrl}">Out</a></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

