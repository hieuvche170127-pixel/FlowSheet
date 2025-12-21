<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- 
    LƯU Ý: Nếu layout_header.jsp đã có thẻ <html>, <head>, <body> mở đầu
    thì XÓA đoạn html/head/body ở file này đi để tránh trùng lặp.
    Ở đây mình giả định layout_header chỉ chứa Nav/CSS, chưa đóng body.
-->

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>My Timesheet</title>
        <!-- Ưu tiên dùng Bootstrap từ layout, dòng này chỉ để fallback nếu layout thiếu -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> 
    </head>


    <%@ include file="/nghiapages/layout_header.jsp" %>

    <div class="container mt-4">
        <c:if test="${not empty sessionScope.errorList}">
            <div class="alert alert-danger">
                <ul>
                    <%-- 2. Duyệt qua từng lỗi và in ra --%>
                    <c:forEach var="error" items="${sessionScope.errorList}">
                        <li>${error}</li>
                        </c:forEach>
                </ul>
            </div>

            <%-- 3. Hủy session sau khi đã hiển thị xong --%>
            <c:remove var="errorList" scope="session" />
        </c:if>

        <c:if test="${not empty sessionScope.sessionMessage}">
            <div class="alert alert-${sessionScope.messageType} alert-dismissible fade show" role="alert">
                ${sessionScope.sessionMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <%-- Hiển thị xong thì xóa ngay cho sạch session --%>
            <c:remove var="sessionMessage" scope="session" />
            <c:remove var="messageType" scope="session" />
        </c:if>

        <%-- Hiển thị thông báo LỖI (sessionError) --%>
        <c:if test="${not empty sessionScope.sessionError}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill me-2"></i> <strong>Lỗi:</strong> ${sessionScope.sessionError}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <%-- Hiển thị xong thì xóa ngay để tránh lặp lại khi F5 --%>
            <c:remove var="sessionError" scope="session" />
        </c:if>


        <h1>Timesheet của tôi: </h1>

        <button type="button" class="btn btn-primary mb-3" data-bs-toggle="modal" data-bs-target="#timesheetModal">
            Add Timesheet
        </button>

        <h2 class="mb-4">My Timesheets List</h2>

        <table class="table table-bordered table-hover">
            <thead class="table-dark">
                <tr>
                    <th>Start Date (Mon)</th>
                    <th>End Date (Sun)</th>
                    <th>Status</th>
                    <th>Last Updated</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${not empty timesheetList}">
                        <c:forEach items="${timesheetList}" var="ts">
                            <tr>
                                <td><fmt:formatDate value="${ts.dayStart}" pattern="dd/MM/yyyy"/></td>
                                <td><fmt:formatDate value="${ts.dayEnd}" pattern="dd/MM/yyyy"/></td>
                                <td>
                                    <span>
                                        ${ts.status}
                                    </span>
                                </td>
                                <td><fmt:formatDate value="${ts.lastUpdatedAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/ViewDetailTimesheet?timesheetId=${ts.timesheetId}" class="btn btn-sm btn-info text-white">View Detail</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="text-center text-muted">You don't have any timesheets yet.</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <!-- MODAL -->
    <div class="modal fade" id="timesheetModal" tabindex="-1" aria-labelledby="timesheetModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="timesheetModalLabel">Add New Timesheet</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form action="${pageContext.request.contextPath}/AddTimesheet" method="Get">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="startDate" class="form-label">Start Date (Monday)</label>
                            <!-- FIX: type="date" input -->
                            <input type="date" class="form-control" id="startDate" name="startDate" required>
                            <div class="form-text text-primary">
                                <i class="fa fa-info-circle"></i> Bạn chọn ngày bất kỳ, hệ thống sẽ tự chọn Thứ 2 của tuần đó.
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="endDate" class="form-label">End Date (Sunday)</label>
                            <!-- FIX: bg-light cho vào class -->
                            <input type="date" class="form-control bg-light" id="endDate" name="endDate" readonly>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                        <button type="submit" class="btn btn-success">Save Timesheet</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%@ include file="/nghiapages/layout_footer.jsp" %>

    <!-- Script phải đặt trong Body, hoặc sau footer -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <script>
        // Hàm format ngày chuẩn yyyy-mm-dd
        function formatDateLocal(date) {
            const year = date.getFullYear();
            const month = String(date.getMonth() + 1).padStart(2, '0');
            const day = String(date.getDate()).padStart(2, '0');

            // --- SỬA LỖI TẠI ĐÂY ---
            // Không dùng `${year}-${month}-${day}` vì JSP sẽ nuốt mất biến.
            // Dùng cộng chuỗi bình thường:
            return year + "-" + month + "-" + day;
        }

        document.getElementById('startDate').addEventListener('change', function () {
            if (!this.value)
                return;

            const oldValue = this.value;

            // 1. Cắt chuỗi để tạo ngày chính xác theo giờ địa phương
            const parts = this.value.split('-');
            const year = parseInt(parts[0], 10);
            const month = parseInt(parts[1], 10) - 1;
            const dayDate = parseInt(parts[2], 10);

            const inputDate = new Date(year, month, dayDate);

            // 2. Logic tính toán về Thứ 2
            const currentDay = inputDate.getDay(); // 0: CN, 1: T2...

            const diffToMonday = inputDate.getDate() - (currentDay === 0 ? 6 : currentDay - 1);

            const monday = new Date(inputDate);
            monday.setDate(diffToMonday);

            const sunday = new Date(monday);
            sunday.setDate(monday.getDate() + 6);

            const mondayStr = formatDateLocal(monday);
            const sundayStr = formatDateLocal(sunday);

            // 3. Cập nhật giao diện
            if (oldValue !== mondayStr) {
                this.value = mondayStr;
            }
            document.getElementById('endDate').value = sundayStr;

            console.log("Selected raw: " + oldValue + " -> Snapped to: " + mondayStr);
        });
    </script>

</html>