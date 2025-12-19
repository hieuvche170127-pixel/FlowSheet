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
    .kv {
        border: 1px solid #eee;
        border-radius: 10px;
        padding: 14px;
        background: #fafafa;
    }
    .row {
        display:flex;
        gap:14px;
        flex-wrap:wrap;
    }
    .cell {
        flex: 1 1 260px;
    }
    .k {
        font-size: 12px;
        color:#666;
        margin-bottom:4px;
        font-weight:700;
        text-transform:uppercase;
    }
    .v {
        font-size: 14px;
        color:#222;
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
</style>

<div class="page-wrapper">

    <% if (!isSupervisor) { %>
    <div style="padding:10px 12px;border:1px solid #ffb3b9;background:#fff5f6;border-radius:8px;color:#b4232a;">
        Access denied. Only Supervisor can view Task Reviews.
    </div>
    <% } else { %>

    <h2 class="title">Task Review Detail</h2>

    <div class="kv">
        <div class="row">
            <div class="cell">
                <div class="k">Review ID</div>
                <div class="v">${review.reviewId}</div>
            </div>
            <div class="cell">
                <div class="k">Task</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${not empty task}">
                            ${task.taskName} <span style="color:#888;">(#${review.taskId})</span>
                        </c:when>
                        <c:otherwise>
                            Task #${review.taskId}
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="row" style="margin-top:12px;">
            <div class="cell">
                <div class="k">Progress (%)</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${review.estimateWorkPercentDone != null}">
                            ${review.estimateWorkPercentDone}
                        </c:when>
                        <c:otherwise>0</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <div class="cell">
                <div class="k">Created At</div>
                <div class="v">
                    <c:choose>
                        <c:when test="${not empty review.dateCreated}">
                            <fmt:formatDate value="${review.dateCreated}" pattern="yyyy-MM-dd HH:mm"/>
                        </c:when>
                        <c:otherwise>N/A</c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div style="margin-top:12px;">
            <div class="k">Comment</div>
            <div class="v">
                <c:choose>
                    <c:when test="${not empty review.reviewComment}">
                        <c:out value="${review.reviewComment}"/>
                    </c:when>
                    <c:otherwise><span style="color:#888;">N/A</span></c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div class="actions">
        <a class="btn"
           href="${pageContext.request.contextPath}/task-review?action=list&taskId=${taskId}">
            Back to list
        </a>
        <a class="btn" href="${pageContext.request.contextPath}/task-review/update?reviewId=${review.reviewId}">Edit</a>
    </div>

    <% }%>

</div>

<jsp:include page="/nghiapages/layout_footer.jsp" />
