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

    // Supervisor = roleID 2 (as you used)
    boolean isSupervisor = (current.getRoleID() == 2);

    // Controller SHOULD set canManage for fine-grained permission.
    // If controller doesn't set it yet, fallback: supervisor can manage, others view-only.
    Object canManageObj = request.getAttribute("canManage");
    boolean canManage = (canManageObj instanceof Boolean) ? (Boolean) canManageObj : isSupervisor;
%>

<jsp:include page="/nghiapages/layout_header.jsp" />

<style>
    .page-wrapper {
        max-width: 1100px;
        margin: 30px auto;
        background: #fff;
        border: 1px solid #ddd;
        border-radius: 8px;
        padding: 24px 28px 40px;
        box-sizing: border-box;
    }
    .page-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 18px;
    }
    .page-header h1 {
        margin: 0;
        font-size: 24px;
        font-weight: 700;
        color: #222;
    }
    .btn-primary, .btn-outline {
        padding: 8px 14px;
        border-radius: 16px;
        border: 1px solid #333;
        background: #fff;
        cursor: pointer;
        font-size: 13px;
        text-decoration: none;
        display: inline-block;
    }
    .btn-primary:hover, .btn-outline:hover {
        background: #f3f3f3;
    }
    .btn-danger {
        border: 1px solid #ff5a6b;
        color: #ff5a6b;
        background: #fff;
    }
    .btn-danger:hover {
        background: #fff5f6;
    }
    .filter-row {
        display: flex;
        gap: 10px;
        align-items: center;
        margin-bottom: 16px;
        flex-wrap: wrap;
    }
    .filter-row select, .filter-row input {
        padding: 8px 10px;
        border-radius: 10px;
        border: 1px solid #ccd2e0;
        font-size: 13px;
    }
    .table-wrapper {
        border: 1px solid #ccc;
        border-radius: 6px;
        padding: 12px;
        background: #fff;
    }
    table {
        width: 100%;
        border-collapse: collapse;
        font-size: 14px;
    }
    th, td {
        padding: 10px 8px;
        text-align: left;
        border-bottom: 1px solid #eee;
        vertical-align: middle;
    }
    th {
        font-weight: 700;
        text-transform: lowercase;
    }
    .muted {
        color: #888;
        font-size: 13px;
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
    .msg.ok  {
        border-color: #b7ebc6;
        background: #f3fff7;
        color: #0f6b2f;
    }
    .msg.info {
        border-color: #cfe3ff;
        background: #f3f8ff;
        color: #1a4b9a;
    }
</style>

<div class="page-wrapper">

    <div class="page-header">
        <h1>Task Reviews</h1>

        <%-- Only show Create button if user can manage --%>
        <% if (canManage) { %>
            <a class="btn-primary"
               href="${pageContext.request.contextPath}/task-review?action=create&taskId=${taskId}">
                Create Review
            </a>
        <% } %>
    </div>

    <%-- Inform students / view-only users --%>
    <% if (!canManage) { %>
        <div class="msg info">You have <b>view-only</b> access to Task Reviews.</div>
    <% } %>

    <c:if test="${not empty error}">
        <div class="msg err">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="msg ok">${success}</div>
    </c:if>

    <form class="filter-row" method="get" action="${pageContext.request.contextPath}/task-review">
        <input type="hidden" name="action" value="list"/>

        <label class="muted">Filter by task:</label>
        <select name="taskId">
            <option value="">-- All tasks --</option>
            <c:forEach var="t" items="${tasks}">
                <option value="${t.taskId}"
                        <c:if test="${param.taskId != null && param.taskId == '' + t.taskId}">selected</c:if>>
                    #${t.taskId} - ${t.taskName}
                </option>
            </c:forEach>
        </select>

        <label class="muted">Search comment:</label>
        <input type="text" name="q" value="${param.q}" placeholder="keyword..." />

        <button class="btn-outline" type="submit">Search</button>
        <a class="btn-outline"
           href="${pageContext.request.contextPath}/task-review?action=list">
            Clear
        </a>
    </form>

    <div class="table-wrapper">
        <table>
            <thead>
                <tr>
                    <th>review id</th>
                    <th>task</th>
                    <th>progress (%)</th>
                    <th>comment</th>
                    <th>created at</th>
                    <th>action</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty reviews}">
                        <c:forEach var="r" items="${reviews}">
                            <tr>
                                <td>${r.reviewId}</td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty taskMap && taskMap[r.taskId] != null}">
                                            ${taskMap[r.taskId].taskName}
                                            <span class="muted">(#${r.taskId})</span>
                                        </c:when>
                                        <c:otherwise>
                                            Task #${r.taskId}
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${r.estimateWorkPercentDone != null}">
                                            ${r.estimateWorkPercentDone}
                                        </c:when>
                                        <c:otherwise>0</c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty r.reviewComment}">
                                            ${r.reviewComment}
                                        </c:when>
                                        <c:otherwise><span class="muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${not empty r.dateCreated}">
                                            <fmt:formatDate value="${r.dateCreated}" pattern="yyyy-MM-dd HH:mm"/>
                                        </c:when>
                                        <c:otherwise><span class="muted">N/A</span></c:otherwise>
                                    </c:choose>
                                </td>

                                <td style="display:flex; gap:8px; flex-wrap:wrap;">
                                    <%-- View-only users see no edit/delete --%>
                                    <% if (canManage) { %>
                                        <a class="btn-outline"
                                           href="${pageContext.request.contextPath}/task-review?action=edit&reviewId=${r.reviewId}&taskId=${taskId}">
                                            Edit
                                        </a>

                                        <form method="post"
                                              action="${pageContext.request.contextPath}/task-review"
                                              style="margin:0;"
                                              onsubmit="return confirm('Delete this review?');">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="taskId" value="${taskId}"/>
                                            <input type="hidden" name="reviewId" value="${r.reviewId}"/>
                                            <button class="btn-outline btn-danger" type="submit">Delete</button>
                                        </form>
                                    <% } else { %>
                                        <span class="muted">—</span>
                                    <% } %>
                                </td>

                            </tr>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <tr>
                            <td colspan="6" style="text-align:center; padding:18px;">
                                <span class="muted">No task reviews yet.</span>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

</div>

<jsp:include page="/nghiapages/layout_footer.jsp" />
