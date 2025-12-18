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
        <title>JSP Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> 
    </head>

    <%@ include file="/nghiapages/layout_header.jsp" %>
    meme
    <h3>Thông tin Timesheet</h3>

    <table class="ts-info">
        <tr>
            <th>Trạng thái</th>
            <td>
                ${timesheet.status}
            </td>
        </tr>
        <tr>
            <th>Ngày bắt đầu</th>
            <td>${timesheet.dayStart}</td>
        </tr>
        <tr>
            <th>Ngày kết thúc</th>
            <td>${timesheet.dayEnd}</td>
        </tr>
        <tr>
            <th>Ghi chú (Summary)</th>
            <td>${timesheet.summary}</td>
        </tr> 
        <tr>
            <th>Cập nhật lần cuối</th>
            <td>${timesheet.lastUpdatedAt}</td>
        </tr>
    </table>
    <h3>Chi tiết công việc (Timesheet Entries)</h3>

    <!-- Kiểm tra nếu danh sách trống thì báo lỗi, ngược lại thì hiện bảng -->
    <c:if test="${empty timesheetEntry}">
        <p style="color: red;">Chưa có dữ liệu chi tiết nào cho Timesheet này.</p>
    </c:if>

    <c:if test="${not empty timesheetEntry}">
        <table class="entry-table">
            <thead>
                <tr>
                    <th style="width: 5%">#</th>
                    <th style="width: 15%">Ngày làm việc</th>
                    <th style="width: 15%">Bắt đầu</th>
                    <th style="width: 15%">Kết thúc</th>
                    <th style="width: 10%">Đi trễ (Phút)</th>
                    <th>Ghi chú</th>
                </tr>
            </thead>
            <tbody>
                <!-- Vòng lặp duyệt qua ArrayList -->
                <c:forEach var="entry" items="${timesheetEntry}" varStatus="status">
                    <tr>
                        <!-- Số thứ tự (status.count bắt đầu từ 1) -->
                        <td class="text-center">${status.count}</td>

                        <!-- Ngày làm việc -->
                        <td class="text-center">
                            <fmt:formatDate value="${entry.workDate}" pattern="dd/MM/yyyy"/>
                        </td>

                        <!-- Giờ bắt đầu (Format HH:mm bỏ giây cho gọn) -->
                        <td class="text-center">
                            <fmt:formatDate value="${entry.startTime}" pattern="HH:mm"/>
                        </td>

                        <!-- Giờ kết thúc -->
                        <td class="text-center">
                            <fmt:formatDate value="${entry.endTime}" pattern="HH:mm"/>
                        </td>

                        <!-- Phút trễ -->
                        <td class="text-center">
                            <!-- Nếu trễ > 0 thì hiện màu đỏ -->
                            <c:choose>
                                <c:when test="${entry.delayMinutes > 0}">
                                    <span class="delay-warning">${entry.delayMinutes}</span>
                                </c:when>
                                <c:otherwise>
                                    0
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <!-- Ghi chú -->
                        <td>
                            <!-- Xử lý nếu null -->
                            <c:out value="${entry.note}" default="--"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>


    <%@ include file="/nghiapages/layout_footer.jsp" %>


</html>
