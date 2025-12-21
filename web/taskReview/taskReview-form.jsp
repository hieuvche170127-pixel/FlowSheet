<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.UserAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%
    UserAccount current = (UserAccount) session.getAttribute("user");
    if (current == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    boolean isSupervisor = (current.getRoleID() == 2);
%>

<jsp:include page="/nghiapages/layout_header.jsp" />

<style>
    .page-wrapper {
        max-width: 1020px;
        margin: 30px auto;
        background: #fff;
        border: 1px solid #ddd;
        border-radius: 10px;
        padding: 24px 28px 32px;
        box-sizing: border-box;
    }
    .title {
        font-size: 26px;
        font-weight: 800;
        margin: 0 0 14px;
        color: #222;
    }
    .msg {
        padding: 10px 12px;
        border-radius: 8px;
        margin-bottom: 14px;
        border: 1px solid #eee;
        background: #fafafa;
    }
    .msg.err { border-color: #ffb3b9; background: #fff5f6; color: #b4232a; }

    .row { display:flex; gap:12px; flex-wrap:wrap; }
    .field { flex: 1 1 320px; }
    label { display:block; font-size: 13px; color:#444; margin-bottom:6px; font-weight:600; }

    select, input, textarea {
        width: 100%;
        padding: 10px 10px;
        border-radius: 10px;
        border: 1px solid #ccd2e0;
        font-size: 13px;
        box-sizing: border-box;
    }
    textarea { min-height: 140px; resize: vertical; }

    .actions {
        margin-top: 14px;
        display:flex;
        gap:10px;
        flex-wrap:wrap;
        align-items:center;
    }
    .btn {
        padding: 9px 16px;
        border-radius: 16px;
        border: 1px solid #333;
        background: #fff;
        cursor: pointer;
        font-size: 13px;
        text-decoration: none;
        display:inline-block;
    }
    .btn.primary { background: #00bfa5; border-color:#00bfa5; color:#fff; }
    .btn.primary:hover { opacity: .92; }

    /* Report picker table */
    .section-title {
        margin-top: 18px;
        font-size: 15px;
        font-weight: 800;
        color: #222;
    }
    .hint { font-size: 12px; color:#6b7280; margin-top: 4px; }

    .table-wrap {
        margin-top: 10px;
        border: 1px solid #e5e7eb;
        border-radius: 10px;
        overflow: hidden;
    }
    table {
        width: 100%;
        border-collapse: collapse;
        font-size: 13px;
    }
    thead th {
        text-align: left;
        background: #fafafa;
        border-bottom: 1px solid #e5e7eb;
        padding: 10px 12px;
        font-weight: 800;
        color: #111827;
    }
    tbody td {
        padding: 10px 12px;
        border-bottom: 1px solid #f1f5f9;
        vertical-align: top;
    }
    tbody tr:hover { background: #fcfcfc; }

    .mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; }
    .small { font-size: 12px; color:#6b7280; }
    .desc {
        max-width: 420px;
        white-space: normal;
        overflow-wrap: anywhere;
        line-height: 1.35;
    }

    .pill {
        display:inline-block;
        padding: 2px 8px;
        border: 1px solid #d1d5db;
        border-radius: 999px;
        font-size: 12px;
        background: #fff;
        color:#111827;
    }

    .summary {
        margin-top: 10px;
        padding: 10px 12px;
        border: 1px dashed #d1d5db;
        border-radius: 10px;
        background: #fafafa;
        font-size: 13px;
        color:#111827;
    }
</style>

<div class="page-wrapper">

    <% if (!isSupervisor) { %>
        <div class="msg err">Access denied. Only Supervisor can create/update Task Reviews.</div>
    <% } else { %>

    <h2 class="title">
        <c:choose>
            <c:when test="${mode == 'update'}">Update Task Review</c:when>
            <c:otherwise>Create Task Review</c:otherwise>
        </c:choose>
    </h2>

    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>

    <form method="post" action="${formAction}">
        <c:if test="${mode == 'update'}">
            <input type="hidden" name="reviewId" value="${review.reviewId}" />
        </c:if>

        <div class="row">
            <div class="field">
                <label>Task *</label>

                <c:choose>
                    <c:when test="${mode == 'update'}">
                        <select name="taskId">
                            <option value="0">-- Select task --</option>
                            <c:forEach var="t" items="${tasks}">
                                <option value="${t.taskId}" <c:if test="${t.taskId == taskId}">selected</c:if>>
                                    ${t.taskName} (#${t.taskId})
                                </option>
                            </c:forEach>
                        </select>
                        <div class="hint">Update mode: task selection does not reload reports.</div>
                    </c:when>

                    <c:otherwise>
                        <select name="taskId"
                                onchange="location.href='${pageContext.request.contextPath}/task-review?action=create&taskId=' + this.value;">
                            <option value="0">-- Select task --</option>
                            <c:forEach var="t" items="${tasks}">
                                <option value="${t.taskId}" <c:if test="${t.taskId == taskId}">selected</c:if>>
                                    ${t.taskName} (#${t.taskId})
                                </option>
                            </c:forEach>
                        </select>
                        <div class="hint">Select a task to load all student reports for that task.</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="field">
                <label>Estimated Work Percent Done (0 - 100)</label>
                <input id="percentInput" type="number"
                       name="estimateWorkPercentDone"
                       step="0.1" min="0" max="100"
                       value="<c:out value='${mode == "update" ? review.estimateWorkPercentDone : ""}'/>"
                       placeholder="Auto-filled from selected report (editable)" />
                <div class="hint">Tip: select a report to auto-fill percent.</div>
            </div>
        </div>

        <!-- Only show report selector in CREATE mode -->
        <c:if test="${mode != 'update'}">
            <c:choose>
                <c:when test="${taskId > 0}">
                    <div class="section-title">Student reports for selected task</div>
                    <div class="hint">Choose one report to review (includes progress, hours, and report time).</div>

                    <c:if test="${empty reports}">
                        <div class="summary">No reports found for this task yet.</div>
                    </c:if>

                    <c:if test="${not empty reports}">
                        <div class="table-wrap">
                            <table>
                                <thead>
                                    <tr>
                                        <th style="width:44px;"></th>
                                        <th style="width:220px;">Student</th>
                                        <th style="width:110px;">Progress</th>
                                        <th style="width:110px;">Hours</th>
                                        <th style="width:160px;">Created at</th>
                                        <th>Description</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="rp" items="${reports}">
                                        <tr>
                                            <td>
                                                <input type="radio" name="reportId" value="${rp.reportId}"
                                                       data-percent="${rp.estimateWorkPercentDone}"
                                                       onclick="fillFromReport(this)"
                                                       required />
                                            </td>
                                            <td>
                                                <div><b><c:out value="${reportUserNameMap[rp.userId]}"/></b></div>
                                                <div class="small mono">UserID: ${rp.userId} • ReportID: ${rp.reportId}</div>
                                            </td>
                                            <td>
                                                <span class="pill">${rp.estimateWorkPercentDone}%</span>
                                            </td>
                                            <td>
                                                <span class="pill">${rp.totalHourUsed}</span>
                                            </td>
                                            <td class="mono">
                                                <c:out value="${rp.createdAt}"/>
                                            </td>
                                            <td class="desc">
                                                <c:out value="${rp.reportDescription}"/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <div class="summary" id="pickedSummary">
                            Select a report above to auto-fill progress.
                        </div>
                    </c:if>
                </c:when>

                <c:otherwise>
                    <div class="summary">Select a task first to load student reports.</div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <div style="margin-top:12px;">
            <label>Review Comment</label>
            <textarea name="reviewComment" placeholder="Write supervisor feedback..."><c:out value="${mode == 'update' ? review.reviewComment : ''}"/></textarea>
        </div>

        <div class="actions">
            <button class="btn primary" type="submit">
                <c:choose>
                    <c:when test="${mode == 'update'}">Save Changes</c:when>
                    <c:otherwise>Create Review</c:otherwise>
                </c:choose>
            </button>

            <a class="btn" href="${pageContext.request.contextPath}/task-review?action=list">Back to list</a>
        </div>
    </form>

    <% } %>

</div>

<script>
    function fillFromReport(radio){
        const percent = radio.getAttribute("data-percent");
        if (percent !== null && percent !== "") {
            document.getElementById("percentInput").value = percent;
        }
        const summary = document.getElementById("pickedSummary");
        if(summary){
            summary.textContent = "Selected reportId = " + radio.value + ". Percent auto-filled (you can edit it).";
        }
    }
</script>

<jsp:include page="/nghiapages/layout_footer.jsp" />
