/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.nghia.timesheetEntry;

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
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

import entity.TimeSheet;
import entity.TimesheetEntry;
import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class AddTImesheetEntry extends HttpServlet {

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
            out.println("<title>Servlet AddTImesheetEntry</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AddTImesheetEntry at " + request.getContextPath() + "</h1>");
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
        // thằng jsp có thể nhận:
        // 1. errorList
        // 2. info (trạn thái của hành động - giả sử như add thành công)
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8"); // Đặt Content Type và Encoding

        HttpSession session = request.getSession(true);
        UserAccount user = (UserAccount) session.getAttribute("user");
        TimesheetDAO timesheetDao = new TimesheetDAO();
        TimesheetEntryDAO tsEntryDao = new TimesheetEntryDAO();

        ArrayList<String> errorList = new ArrayList<>();
        // ko cần else do hết luồng r 
//        validate session
        if (user == null) {
            response.sendRedirect("login.jsp");
            // tuy không cần return (hoặc là có) nhưng mà để đây để đánh dấu kết thúc luồng 
            return;
        }
        int roleId = user.getRoleID();

        //validadate role
        if (roleId == 2 || roleId == 3) {
            response.sendRedirect(request.getContextPath() + "/supervisor/dashboard");
            return;
        }
        // if role ==1 thì vào việc 
        if (roleId == 1) {
            // trong này có khoảng 4 exception lớn: 
            // NPE - cái này chuyển về (timesheet detail - hay lỗi chugn nhỉ ? :))) lỗi lớn đấy, chắc về Error page)
            // numberformat exception - chuyển về timesheet detail (do có lỗi khi lấy data)
            // sql exception (hình như ko bắn do bên DAO nó tự log rồi :)) ) (cái này cũng lớn nhưng ko biết có ko :))) )
            // parse exception (do parse date từ String về) (gửi về timesheet detail do vấn đề về data)
            // else chắc để chung chung (thôi cứ gửi về bên timesheet detail vậy)
            // còn lại sẽ là 1 cái exception dở hơi nào đó tôi ko biết :))
            try {
                // Lấy các tham số từ request dựa theo thuộc tính name trong HTML
                // tuwj nhieen dang nghi la NPE no ngan duoc het nhung ma biet dau cai data field
                // ko duoc su dung luc validate -> co the loi sau hon (SQL exception cac thu. )
                String timesheetIdString = request.getParameter("timesheetId");
                String workDateString = request.getParameter("workDate");
                String startTimeString = request.getParameter("startTime");
                // casi end nay cho phep null - ko nhapj gi ke ca the thi van phai co param, chi la param day rong thoi :)) 
                String endTimeString = request.getParameter("endTime");
                String delayMinuteString = request.getParameter("delayTime"); // Ở JSP anh đặt name="delayTime"
                String note = request.getParameter("summary");   // Ở JSP anh đặt name="summary"

                int timesheetId = 0;
                // validate thang timesheet id truoc vi no la thang key, có thể ảnh hưởng khi mình chuyern về
                // timesheet detail, còn mấy thk còn lại thì cứ làm như bth
                if (timesheetIdString == null || timesheetIdString.isBlank()) {
                    errorList.add("Không lấy được timesheet id");
                } else {
                    try {
                        timesheetId = Integer.parseInt(timesheetIdString);
                    } catch (Exception e) {
                        errorList.add("Không lấy được timesheet id");
                    }
                }
                // ko cần else do ngắt luồng r 
                if (!errorList.isEmpty()) {
                    session.setAttribute("errorList", errorList);
                    response.sendRedirect("/nghiapages/errorPage.jsp");
                    return;
                }

                // check 4 thk param kia xem thk nào null ko 1 vài thk check xem có empty hay ko?
                // như thk endTime là cho empty, delay thì jsp set = 0 trước r
                if (workDateString == null || workDateString.isBlank()
                        || startTimeString == null || startTimeString.isBlank()
                        // empty được
                        || endTimeString == null
                        || // thk này set default ben jsp la 0 roi
                        delayMinuteString == null || delayMinuteString.isBlank()
                        || // thk này empty được
                        note == null) {
                    errorList.add("Lỗi khi nhận dữ liệu: ");
                }

                Date workDate = null;
                Time startTime = null;
                Time endTime = null;
                int delayMinute = 0;
                try {
                    workDate = java.sql.Date.valueOf(workDateString);
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
                    // do delay empty được nên nếu nó khác thì.. tại khởi tạo rồi.
                    if (!delayMinuteString.isBlank()) {
                        delayMinute = Integer.parseInt(delayMinuteString);
                        if(delayMinute>1440){
                            errorList.add("ko thể nghỉ trên 1440 phút được, nghỉ nguyên ngày còn gìiiii");
                        }
                    }

                    // validate ngày được tạo sao cho cùng với tuần hiện tại
                    // 1. Lấy ngày hiện tại
                    LocalDate today = LocalDate.now();
                    // 2. Tìm ngày Thứ 2 của tuần này
                    LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                    // 3. Tìm ngày Chủ Nhật của tuần này
                    LocalDate sunday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                    // 4. Chuyển sang java.sql.Date để so sánh với workDate của anh
                    java.sql.Date tsStart = java.sql.Date.valueOf(monday);
                    java.sql.Date tsEnd = java.sql.Date.valueOf(sunday);
                    // 5. Kiểm tra như cũ
                    if (workDate.before(tsStart) || workDate.after(tsEnd)) {
                        errorList.add("Chỉ thêm được thuộc tuần hiện tại (Từ " + tsStart + " đến " + tsEnd + ")");
                    }
                    // check xem ngày được add có sau ngày hôm nay không, 
                    // nếu mà bằng ngày hôm nay thì check cả endTime và start time nếu endTime ko null
                    // kiểm tra xem giá trị thời gian được add có phải là tương lai hay ko ? 
                    // check cả end lẫn start time - vì nãy tôi suýt add được :))). 

                    // 1. Lấy mốc thời gian hiện tại
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
                } catch (Exception e) {
                    errorList.add("Có lỗi xảy ra khi xử lý (validate) dữ liệu trước khi vào xử lý nghiệp vụ: ");
                }
                if (!errorList.isEmpty()) {
                    session.setAttribute("errorList", errorList);
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                    return;
                }
                // oghe validate xong phan data input từ bên client

                // giờ xuống tầng business :))
                TimeSheet getTimesheetById = timesheetDao.getTimesheetByTimesheetId(timesheetId);
                // edit able:
                // 1.check xem timesheet có còn edit được ko ? có là đúng tuần này ko 
                // (reason) do ko cho thay đổi nếu từ tuần trước.
                // 3. check xem người lấy thông qua user session có đúng là người add hay ko ?
                // 4. xem có cái trùng khớp chưa ? - trùng timesheet entry ?

                // 3. check xem người lấy thông qua user session có đúng là người add hay ko ?
                if (getTimesheetById.getUserId() != user.getUserID()) {
                    errorList.add("người tạo và người add timesheet ko trùng khớp");

                }
                // 1.check xem timesheet có còn edit được ko ? có là đúng tuần này ko 
                LocalDate currentMonday = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                // 1.2. Chuyển dayStart của Timesheet sang LocalDate để so sánh cho mượt
                LocalDate tsMonday = getTimesheetById.getDayStart().toLocalDate();
                // 1.3. So sánh
                if (!tsMonday.equals(currentMonday)) {
                    errorList.add("Timesheet này thuộc tuần cũ, không thể chỉnh sửa!");
                }
                //4. trả về true nếu đã tồn tại 1 tsE có cùng workdate
                boolean isAnytsEntryHaveTheSameWorkDate = tsEntryDao.existEntryByDateAndTimesheetId(workDate, timesheetId);
                if (isAnytsEntryHaveTheSameWorkDate) {
                    errorList.add("Đã tồn tại 1 timesheetEntry có cùng workDate.");
                }
                if (!errorList.isEmpty()) {
                    session.setAttribute("errorList", errorList);
                    response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                    return;
                }
                // đã validate hết và okeee hahahahahaaaa. 
                // tạo 1 instance mới, các trường sau có thể null hoặc empty (có gì xuống chỉnh lại DAO)
                TimesheetEntry added = new TimesheetEntry();
                added.setTimesheetId(timesheetId);
                added.setWorkDate(workDate);
                // null able - đã có check trong DAO.
                added.setEndTime(endTime);
                added.setStartTime(startTime);
                added.setDelayMinutes(delayMinute);
                added.setNote(note);

                boolean addStatus = tsEntryDao.addTimeSheetEntry(added);
                if (addStatus) {
                    session.setAttribute("info", "add timesheet entry thành cônggg");
                } else {
                    session.setAttribute("info", "add thất bại - không rõ nguyên nhân");
                }
                response.sendRedirect("ViewDetailTimesheet?timesheetId=" + timesheetId);
                return;
            } catch (Exception e) {
                errorList.add("đã có excpetion xảy ra :)), vui lòng liên hệ team dev " + e.getMessage());
                session.setAttribute("errorList", errorList);
                response.sendRedirect("/nghiapages/errorPage.jsp");
                return;
            }
        } // nếu role id ko phải 1-2-3
        else {
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
     * Returns a short description of the servlet .
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
