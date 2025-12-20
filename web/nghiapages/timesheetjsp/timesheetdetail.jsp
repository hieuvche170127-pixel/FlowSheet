<%-- 
    Document   : timesheetdetail
    Created on : Dec 18, 2025, 8:40:41 AM
    Author     : Admin
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chi tiết Timesheet</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> 
        <!-- Thêm icon cho xịn -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

        <style>
            body {
                background-color: #f4f7f6;
                color: #333;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            }
            .main-container {
                max-width: 1200px;
                margin: 30px auto;
                padding: 0 15px;
            }

            /* Card tổng quan */
            .ts-card {
                background: white;
                border-radius: 12px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.08);
                padding: 25px;
                margin-bottom: 30px;
                border: none;
            }

            /* Grid 7 cột đồng nhất cho cả Header và Body */
            .common-grid {
                display: grid;
                grid-template-columns: repeat(7, 1fr);
                gap: 12px;
            }

            .info-item label {
                font-size: 0.75rem;
                text-transform: uppercase;
                letter-spacing: 1px;
                color: #94a3b8;
                display: block;
                margin-bottom: 6px;
                font-weight: 600;
            }
            .info-item span {
                font-weight: 700;
                color: #1e293b;
                font-size: 1rem;
            }

            /* Định nghĩa vị trí các cột ở hàng trên */
            .col-status {
                grid-column: span 1;
            }
            .col-start  {
                grid-column: span 2;
            }
            .col-end    {
                grid-column: span 2;
            }
            .col-update {
                grid-column: span 2;
            }
            .col-summary {
                grid-column: span 7;
                margin-top: 15px;
                padding-top: 15px;
                border-top: 1px solid #f1f5f9;
            }

            .status-badge {
                padding: 6px 14px;
                border-radius: 50px;
                font-size: 0.75rem;
                font-weight: 700;
                background: #e0f2fe;
                color: #0369a1;
                display: inline-block;
            }

            /* Layout 7 cột cho các thẻ ngày */
            .day-card {
                background: white;
                border: 1px solid #e2e8f0;
                border-radius: 12px;
                padding: 20px 10px;
                text-align: center;
                transition: all 0.3s ease;
            }
            .day-card:hover {
                transform: translateY(-8px);
                box-shadow: 0 10px 20px rgba(0,0,0,0.1);
                border-color: #3b82f6;
            }
            .day-name {
                font-weight: 800;
                color: #1e293b;
                margin-bottom: 4px;
                display: block;
                text-transform: capitalize;
            }
            .day-date {
                font-size: 0.85rem;
                color: #64748b;
                margin-bottom: 15px;
                display: block;
            }
            .time-slot {
                background: #f8fafc;
                border: 1px solid #f1f5f9;
                border-radius: 8px;
                padding: 8px 4px;
                font-size: 0.85rem;
                margin-bottom: 8px;
                font-weight: 700;
                color: #334155;
            }
            .delay-tag {
                font-size: 0.7rem;
                color: #ef4444;
                background: #fef2f2;
                padding: 2px 8px;
                border-radius: 4px;
                font-weight: 600;
            }
            .note-text {
                font-size: 0.75rem;
                color: #64748b;
                margin-top: 12px;
                font-style: italic;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                min-height: 36px;
            }

            h3 {
                font-weight: 800;
                color: #0f172a;
                margin-bottom: 25px;
                display: flex;
                align-items: center;
            }
            h3::before {
                content: "";
                width: 6px;
                height: 24px;
                background: #3b82f6;
                margin-right: 12px;
                border-radius: 10px;
            }
        </style>
    </head>
    <%@ include file="/nghiapages/layout_header.jsp" %>

    <div class="d-flex justify-content-between">
        <h3>Thông tin Timesheet</h3>
        <a href="${pageContext.request.contextPath}/DeleteTimesheet?tid=${timesheet.timesheetId}" 
           class="btn btn-outline-danger d-inline-flex align-items-center justify-content-center"
           style="width: 35px; height: 35px; padding: 0;"
           onclick="return confirm('Anh có chắc chắn muốn xóa Timesheet trống này không?')">
            <i class="bi bi-trash3-fill"></i>
        </a>
    </div>

    <%-- Hiển thị danh sách LỖI (Màu đỏ) --%>
    <c:if test="${not empty sessionScope.errorList}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <ul class="mb-0">
                <c:forEach var="err" items="${sessionScope.errorList}">
                    <li>${err}</li>
                    </c:forEach>
            </ul>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="errorList" scope="session" />
    </c:if>

    <%-- Hiển thị thông báo trạng thái đơn lẻ (Thành công/Thất bại) --%>
    <c:if test="${not empty sessionScope.info}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            <i class="bi bi-info-circle-fill me-2"></i>
            ${sessionScope.info}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <%-- Xóa biến info khỏi session ngay sau khi hiển thị xong --%>
        <c:remove var="info" scope="session" />
    </c:if>


    <!-- Phần Card thông tin tổng quan - Cấu trúc 7 cột -->
    <div class="ts-card">
        <div class="common-grid">
            <!-- Hàng 1 -->
            <div class="info-item col-status">
                <label>Trạng thái</label>
                <span class="status-badge">${timesheet.status}</span>
            </div>

            <div class="info-item col-start">
                <label>Ngày bắt đầu</label>
                <span><i class="bi bi-calendar2-week me-1"></i> ${timesheet.dayStart}</span>
            </div>

            <div class="info-item col-end">
                <label>Ngày kết thúc</label>
                <span><i class="bi bi-calendar-check me-1"></i> ${timesheet.dayEnd}</span>
            </div>

            <div class="info-item col-update">
                <label>Cập nhật lần cuối</label>
                <span class="text-muted"><fmt:formatDate value="${timesheet.lastUpdatedAt}" pattern="dd/MM/yyyy HH:mm"/></span>
            </div>

            <!-- Hàng 2: Summary -->
            <div class="info-item col-summary">
                <label>Ghi chú tổng (Summary)</label>
                <div class="d-flex justify-content-between">
                    <span style="font-weight: 500; line-height: 1.6;">
                        ${timesheet.summary != null ? timesheet.summary : "<span class='text-muted'>Không có ghi chú.</span>"}
                    </span>
                    <button type="button" class="btn btn-sm btn-outline-primary" data-bs-toggle="modal" data-bs-target="#myModal">
                        <i class="bi bi-pencil-square"></i> Chỉnh sửa
                    </button>
                </div>
            </div>
        </div>
    </div>

    <h3>Chi tiết công việc</h3>

    <c:if test="${empty timesheetEntry}">
        <div class="alert alert-warning shadow-sm">
            <i class="bi bi-exclamation-triangle me-2"></i> Chưa có dữ liệu chi tiết công việc cho tuần này.
        </div>
    </c:if>

    <c:if test="${not empty timesheetEntry}">
        <!-- Grid 7 cột ứng với 7 ngày trong tuần -->
        <div class="common-grid">
            <c:forEach var="entry" items="${timesheetEntry}">
                <div class="day-card">
                    <!-- Hiển thị Thứ -->
                    <span class="day-name">
                        <fmt:setLocale value="vi_VN"/>
                        <fmt:formatDate value="${entry.workDate}" pattern="EEEE"/>
                    </span>
                    <!-- Hiển thị Ngày/Tháng -->
                    <span class="day-date">
                        <fmt:formatDate value="${entry.workDate}" pattern="dd/MM/yyyy"/>
                    </span>

                    <div class="time-slot">
                        <fmt:formatDate value="${entry.startTime}" pattern="HH:mm"/> - 
                        <fmt:formatDate value="${entry.endTime}" pattern="HH:mm"/>
                    </div>

                    <c:choose>
                        <c:when test="${entry.delayMinutes > 0}">
                            <div class="delay-tag">
                                <i class="bi bi-clock-history"></i> Trễ: ${entry.delayMinutes}p
                            </div>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-light text-success border">Đúng giờ</span>
                        </c:otherwise>
                    </c:choose>

                    <div class="note-text" title="${entry.note}">
                        <c:out value="${entry.note}" default="--"/>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>


    <div class="modal fade" id="myModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form action="UpdateTimesheet" method="get">
                        <div class="modal-body">
                            <h5>Nội dung ghi chú mới của timesheet:</h5>
                            <input type="hidden" name="timesheetId" value="${timesheet.timesheetId}"> <textarea name="summary" class="form-control">${timesheet.summary}</textarea>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>


    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <%@ include file="/nghiapages/layout_footer.jsp" %>

</html>
