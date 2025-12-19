<%-- 
    Document   : view_all_invitation_sent_to_me
    Created on : Dec 18, 2025, 9:28:11 PM
    Author     : Admin
--%>
<%-- 
    Document   : view_all_invitation_sent_to_me
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Invitation Sent To Me</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet"> 
    </head>

    <%-- INCLUDE HEADER --%>
    <%@ include file="/nghiapages/layout_header.jsp" %>

    <!-- 1. Đặt biến thời gian hiện tại RA NGOÀI vòng lặp để tránh lỗi Duplicate Bean -->
    <jsp:useBean id="now" class="java.util.Date" />

    <div class="container mt-5">

        <%-- Kiểm tra nếu có tồn tại biến replyStatus trong session --%>
        <c:if test="${not empty sessionScope.replyStatus}">
            <c:choose>
                <%-- Nếu thành công (true) --%>
                <c:when test="${sessionScope.replyStatus == true}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <strong>Thành công!</strong> Bạn đã cập nhật trạng thái lời mời thành công.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:when>

                <%-- Nếu thất bại (false) --%>
                <c:otherwise>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <strong>Thất bại!</strong> Có lỗi xảy ra khi cập nhật lời mời, vui lòng thử lại.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:otherwise>
            </c:choose>

            <%-- Xóa ngay để khi F5 không hiện lại thông báo nữa --%>
            <c:remove var="replyStatus" scope="session" />
        </c:if>

        <!-- 2. HIỂN THỊ VÀ XÓA LỖI TỪ SESSION -->
        <c:if test="${not empty sessionScope.sessionError}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <strong>Thông báo:</strong> ${sessionScope.sessionError}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <%-- Xóa ngay khỏi session --%>
            <c:remove var="sessionError" scope="session" />
        </c:if>

        <h2 class="mb-4 text-center">Danh Sách Lời Mời Của Tôi</h2>

        <div class="table-responsive">
            <table class="table table-hover table-bordered align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>STT</th>
                        <th>Người mời</th>
                        <th>Vai trò</th>
                        <th>Dự án / Nhóm</th>
                        <th>Ngày mời</th>
                        <th>Hạn chót</th>
                        <th>Trạng thái</th>
                        <th>Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${invitedInvitationList}" var="invite" varStatus="loop">
                        <tr>
                            <td>${loop.index + 1}</td>

                            <%-- Sửa: invitedById (chữ d thường) --%>
                            <td>${invite.invitedById} <br> <small class="text-muted">(ID Người mời)</small></td>

                            <%-- Sửa: roleId (chữ d thường) --%>
                            <td>
                                <span class="text-dark">${roleMap[invite.roleId]}</span>
                            </td>


                            <%-- Sửa: projectId, teamId (chữ d thường) --%>
                            <td>
                                <c:if test="${not empty invite.projectId}">Project ID: ${invite.projectId}</c:if>
                                <c:if test="${not empty invite.teamId}">Team ID: ${invite.teamId}</c:if>
                                </td>

                            <%-- Sửa: Dùng createdAtAsDate để format được --%>
                            <td>
                                <fmt:formatDate value="${invite.createdAtAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                            </td>

                            <%-- Sửa: Dùng expiresAtAsDate --%>
                            <td>
                                <fmt:formatDate value="${invite.expiresAtAsDate}" pattern="dd/MM/yyyy HH:mm"/>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${invite.status == 'PENDING'}">
                                        <span class="text-dark">Đang chờ</span>
                                    </c:when>
                                    <c:when test="${invite.status == 'ACCEPTED'}">
                                        <span class="s">Đã chấp nhận</span>
                                    </c:when>
                                    <c:when test="${invite.status == 'REJECT'}">
                                        <span class=" ">Đã từ chối</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="">${invite.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>
                                <%-- LOGIC SO SÁNH NGÀY THÁNG --%>
                                <%-- invite.expiresAtAsDate là Date, now là Date => so sánh được --%>
                                <c:choose>
                                    <c:when test="${invite.status == 'PENDING' && invite.expiresAtAsDate.after(now)}">
                                        <div class="btn-group btn-group-sm">
                                            <a href="${pageContext.request.contextPath}/ReplyInvitation?action=ACCEPTED&id=${invite.invitationId}" class="btn btn-outline-success">Chấp nhận</a>
                                            <a href="${pageContext.request.contextPath}/ReplyInvitation?action=reject&id=${invite.invitationId}" class="btn btn-outline-danger">Từ chối</a>
                                        </div>
                                    </c:when>
                                    <c:when test="${invite.status == 'PENDING' && invite.expiresAtAsDate.before(now)}">
                                        <small class="text-danger fw-bold">Hết hạn</small>
                                    </c:when>
                                    <c:otherwise>
                                        <small class="text-muted">N/A</small>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty invitedInvitationList}">
                        <tr>
                            <td colspan="8" class="text-center italic">Bạn hiện không có lời mời nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <%@ include file="/nghiapages/layout_footer.jsp" %>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</html>