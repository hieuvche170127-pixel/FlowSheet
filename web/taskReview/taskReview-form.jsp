<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="entity.UserAccount"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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
        max-width: 900px;
        margin: 30px auto;
        background: #fff;
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 24px 28px 32px;
        box-sizing: border-box;
    }
    .title {
        font-size: 22px;
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
    .msg.err {
        border-color: #ffb3b9;
        background: #fff5f6;
        color: #b4232a;
    }
    .row {
        display:flex;
        gap:12px;
        flex-wrap:wrap;
    }
    .field {
        flex: 1 1 260px;
    }
    label {
        display:block;
        font-size: 13px;
        color:#444;
        margin-bottom:6px;
        font-weight:600;
    }
    select, input, textarea {
        width: 100%;
        padding: 10px 10px;
        border-radius: 10px;
        border: 1px solid #ccd2e0;
        font-size: 13px;
        box-sizing: border-box;
    }
    textarea {
        min-height: 140px;
        resize: vertical;
    }
    .actions {
        margin-top: 14px;
        display:flex;
        gap:10px;
        flex-wrap:wrap;
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
    .btn.primary {
        background: #00bfa5;
        border-color:#00bfa5;
        color:#fff;
    }
    .btn.primary:hover {
        opacity: .92;
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
                <select name="taskId" required>
                    <option value="">-- Select task --</option>
                    <c:forEach var="t" items="${tasks}">
                        <option value="${t.taskId}"
                                <c:if test="${mode == 'update' && review.taskId == t.taskId}">selected</c:if>>
                            #${t.taskId} - ${t.taskName}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="field">
                <label>Estimated Work Percent Done (0 - 100)</label>
                <input type="number"
                       name="estimateWorkPercentDone"
                       step="0.1" min="0" max="100"
                       value="<c:out value='${mode == "update" ? review.estimateWorkPercentDone : ""}'/>"
                       placeholder="e.g. 45.5" />
            </div>
        </div>

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

            <a class="btn"
               href="${pageContext.request.contextPath}/task-review?action=list">
                Back to list
            </a>
    </form>

    <% }%>

</div>

<jsp:include page="/nghiapages/layout_footer.jsp" />
