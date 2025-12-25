/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.timesheetEntry;

import entity.TimeSheet;
import entity.TimesheetEntry;

import dal.TimesheetDAO;
import dal.TimesheetEntryDAO;
import entity.UserAccount;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 *
 * @author Admin
 */
public class UpdateTimesheetEntry extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UpdateTimesheetEntryDetail</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateTimesheetEntryDetail at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // chỉ cho cập nhật thông tin bên trong thôi, ko cho update ngày :))), muốn update thì phải tạo mới :)))))) - sorry user :))
        // thực ra update ngày thì nó hơi bựa ko nhỉ ?? :) hoặc là tôi chỉ đang tự biện minh cho bản thân vì phân tích ko kĩ :)

        // nhận các trường sau từ bên client:
        // 1. timesheetId - biết còn trả về sau khi thực hiện hành động xong - trả về cái detail
        // 2. entryId - biết chính xác là thằng nào được cập nhật
        // ko cần cái date vì thực ra là entryId là cái đấy r- lỗi do lúc thiết kế, phân tích chưa kĩ
        // 3. startTime để xem giờ start mới
        // 4. endTime
        // 5. delay time
        // 6. ghit chú công việc 
        // hết, nhìn chắc cũng giống giống bên add nhỉ ? có khác gì mấy ko còn coppy nguyên sang
        // check luồng bên kia trước này:
        // 1. check session
        // 2. check role của user
        // 3. validate qua các data từ client (validate bước 1)
        // 3.1: chỗ timesheetid với entry id thì ko cần kỹ đâu, dính exception thì tự nhảy xuống thôi
        // nếu mà timesheetId là thằng bị dính exception cũng ko sao vì cái controller bên kia
        // cũng validate data khá kĩ có gì bắn về trang error hay sao ý
        // chỗ này thì phải validate sau tại vì chưa lấy được cái workDate (thay vì lấy từ client thì lấy từ DB cho chắc.)
        // chứ biếu đâu nó chỉnh thì khó :)). ơ tính ra để 2 cái cũng hay nhỉ :))) - có phải vậy ko ?
        // 3.2: validate chỗ date start, date+ end time để check xem có qua thời điểm hiện tại ko 
        // ơ mà chỉ cần end< now và start<end là được mà nhỉ :) 
        // à ừ còn trường hợp end null nữa, lúc đấy thì cần thật, hợp lý 
        // mấy cái sau cho empty: (ko cho thk nào null hết nhé :) )
        // endtime, delayTime (thk này default 0 thì lát khởi tạo) à mà cái delay time ko cho trên 1440 phút nhé - tại nó ngang 1 ngày r 
        // ghi chú công việc cũng cho empty được, ngta thích làm gì thì làm
        // chưa chi thấy là coppy nguyên từ bên add sang là ngọt rồi :))))
        //
        // 4. validate nhưng sâu hơn:
        // 4.1 check xem timesheetid đúng là của thk user này ko (thông qua session)
        // 4.2 check xem cái timesheet id này có edit able ko ? có thuộc tuần này ko ? 
        // 4.3 check xem cái timesheetentry này có thuộc timesheet kia ko ? - hmmmm tuy hơi dài và có thể thừa nhưng thừa hơn thiếu hah
        //- check bằng cách chọc vào database và check xem với tsEntry kia thì có thật là đi với cái timesheet này ko? 
        // tại biết đâu bọn nó client nó nghịch thì gãy - nó edit cái timesheetid trong cái timesheetentry thì sao ? ko tin được
        // vì sẽ phải lấy khá nhiều lần nên lấy luôn data lên trên controller chứ ko chọc vào DAO nhiều
        // giải thuật của phần 4: lấy timesheet bằng tsDao và hàm get Timesheet by Id
        // sau đó check xem ngày start của nó có phải tuần này ko ? - xong 4.1
        // lấy timesheetentry bằng timesheetentryId sau đó check lại xem có đúng là thuộc thằng timesheet này ko ? 
        // check cái timesheet .get user id với cái session user id thế là xong, ngon.
        //nếu xong 3 cái trên rồi thì vào việc à ? ừ chắc vậy, cho cái biến status update thôi, ez.
        // tóm tắt: coppy nguyên từ bên add sang, khác mỗi cái là check end với start thì check bằng thằng date lấy từ dưới DB
        // bắt đầu implementttt 6h41- 24/12/2025 - nước đến chân mới nhảy
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8"); // Đặt Content Type và Encoding

        HttpSession session = request.getSession(true);
        UserAccount user = (UserAccount) session.getAttribute("user");
        TimesheetDAO timesheetDao = new TimesheetDAO();
        TimesheetEntryDAO tsEntryDao = new TimesheetEntryDAO();

        ArrayList<String> errorList = new ArrayList<>();

        if (user == null) {
            response.sendRedirect("login.jsp");
            // tuy không cần return (hoặc là có) nhưng mà để đây để đánh dấu kết thúc luồng 
            return;
        }

        int roleId = user.getRoleID();
        if (roleId == 2 || roleId == 3) {
            response.sendRedirect(request.getContextPath() + "/supervisor/dashboard");
            return;
        }
        if (roleId == 1) {
            try {
                String timesheetIdString = request.getParameter("timesheetId");
                String timesheetEntryIdString = request.getParameter("entryId");
                // check xem co con hang nao null ko
                String startTimeString = request.getParameter("startTime");
                String endTimeString = request.getParameter("endTime");
                String delayMinuteString = request.getParameter("delay");
                String note = request.getParameter("note");

                int timesheetId = Integer.parseInt(timesheetIdString);
                int timesheetEntryId = Integer.parseInt(timesheetEntryIdString);

                if (startTimeString == null || startTimeString.isBlank()
                        // empty được
                        || endTimeString == null
                        || // thk này set default ben jsp la 0 roi
                        delayMinuteString == null || delayMinuteString.isBlank()
                        || // thk này empty được
                        note == null) {
                    errorList.add("Lỗi khi nhận dữ liệu: ");
                }
                Time startTime = null;
                Time endTime = null;
                int delayMinute = 0;

                // đặt trong try catch và ở dưới có ngay có check error để check xem có lỗi gì không ?
                // validate đoạn đầu. 
                // nếu có thì rẽ nhánh luồng luôn- redirect về viewtimesheet detail
                try {
                    startTime = java.sql.Time.valueOf(startTimeString + ":00");
                    // do endtime empty được
                    if (endTimeString.isBlank()) {
                        endTime = null;
                    } else {
                        endTime = java.sql.Time.valueOf(endTimeString + ":00");
                    }
                    // check null trước vì ở trên có set bằng null nếu mà endtime ko nhận được cái gì.
                    if (endTime != null && !endTime.after(startTime)) {
                        errorList.add("thời gian end phải sau start!");
                    }
                    if (!delayMinuteString.isBlank()) {
                        delayMinute = Integer.parseInt(delayMinuteString);
                        if (delayMinute > 1440) {
                            errorList.add("ko thể nghỉ trên 1440 phút được, nghỉ nguyên ngày còn gìiiii");
                        }
                    }
                } catch (Exception e) {
                    errorList.add("Có lỗi xảy ra khi xử lý (validate) dữ liệu trước khi vào xử lý nghiệp vụ: ");
                }
                if (!errorList.isEmpty()) {
                    session.setAttribute("errorList", errorList);
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                    return;
                }
                // validate sâu 4.
                try {
                    TimeSheet tsGetById = timesheetDao.getTimesheetByTimesheetId(timesheetId);
                    TimesheetEntry tsEntry = tsEntryDao.getTimesheetEntryByTimesheetEntryId(timesheetEntryId);
                    // check đúng người
                    if (tsGetById.getUserId() != user.getUserID()) {
                        errorList.add("đây ko phải là timesheet và timesheet entry của cậu.");
                    }
                    // check đúng timesheet
                    if (tsEntry.getTimesheetId() != tsGetById.getTimesheetId()) {
                        errorList.add("timesheet entry và  timesheet không thuộc về nhau");
                    }
                    // check xem có đúng là của tuần này ko ?
                    Date startDateOfTimesheet = tsGetById.getDayStart();

                    LocalDate today = LocalDate.now();
                    LocalDate mondayOfCurrentWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    // 2. Chuyển cái sql.Date của anh sang LocalDate để so sánh
                    LocalDate tsStartDate = startDateOfTimesheet.toLocalDate();
                    // nếu ko đúng thì ko cho sửa
                    if (!tsStartDate.equals(mondayOfCurrentWeek)) {
                        errorList.add("timesheet này ko thể được chỉnh sửa.");
                    }
                    // check xem cái được add có quá ngày hôm nay hay ko (sau khi edit)
                    Date workDate = tsEntry.getWorkDate();
                    LocalDateTime now = LocalDateTime.now();
                    // 2. Kiểm tra nếu workDate vượt quá ngày hôm nay
                    if (workDate.toLocalDate().isAfter(now.toLocalDate())) {
                        errorList.add("Không thêm timesheet của tương lai!");
                    } else {
                        // 3. Nếu workDate hợp lệ, check tiếp mốc Giờ kết thúc (nếu có)
                        if (endTime != null) {
                            // Ghép Ngày đã chọn + Giờ kết thúc đã chọn
                            LocalDateTime combinedEndDateTime = LocalDateTime.of(workDate.toLocalDate(), endTime.toLocalTime());
                            // Nếu mốc này vượt quá thời điểm hiện tại thì báo lỗi
                            if (combinedEndDateTime.isAfter(now)) {
                                errorList.add("Thời gian kết thúc không được vượt quá thời điểm hiện tại!");
                            }
                        }
                        LocalDateTime combinedStartDateTime = LocalDateTime.of(workDate.toLocalDate(), startTime.toLocalTime());
                        if (combinedStartDateTime.isAfter(now)) {
                            errorList.add("Thời gian bắt đầu timesheet entry không được vượt quá thời điểm hiện tại!");
                        }
                    }
                    if (!errorList.isEmpty()) {
                        session.setAttribute("errorList", errorList);
                        response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                        return;
                    }
                    // chắc là xong rồi, giờ update thôi
                    // các field có thể null, rỗng : note empty được, ednTime null được, delay thì default là 0;
                    TimesheetEntry updated = new TimesheetEntry();

                    updated.setEntryId(timesheetEntryId);
                    updated.setDelayMinutes(delayMinute);
                    updated.setEndTime(endTime);
                    updated.setStartTime(startTime);
                    updated.setNote(note);

                    boolean updateStatus = tsEntryDao.updateTimesheetEntry(updated);
                    if (updateStatus) {
                        session.setAttribute("info", "update timesheet entry thành cônggg");
                    } else {
                        session.setAttribute("info", "update thất bại - không rõ nguyên nhân");
                    }
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                    return;

                } catch (Exception e) {
                    errorList.add("Có lỗi xảy ra khi vào xử lý nghiệp vụ: ");
                    session.setAttribute("errorList", errorList);
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                    return;
                }
            } // chỗ này dắt về my timesheet cho lành :))
            catch (Exception e) {
                errorList.add("Có lỗi xảy ra khi parse/nhận dữ liệu");
                session.setAttribute("errorList", errorList);
                response.sendRedirect(request.getContextPath() + "/ViewAndSearchTimesheet");
                return;
            }
        } else {
            session.invalidate();
            response.sendRedirect("login.jsp");
            return;
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
