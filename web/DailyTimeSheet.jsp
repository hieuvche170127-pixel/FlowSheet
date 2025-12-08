<%-- 
    Document   : TimeSheet
    Created on : Dec 7, 2025, 5:59:42 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
        <link rel="stylesheet" href="/css_files/Timesheet.css"/>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
        <link rel="stylesheet" href="css_files/DailyTimesheet.css"/>


    </head>
    <body>
        <div class="d-flex" >
            <div class="side_bar" style="width: 15%; border-right: 1px solid black; height: 100%">
                <div class="logo_image_container d-flex align-items-center" >
                    <img src="https://th.bing.com/th/id/OIP.wpJ9IG8zdEhYPBsy8X9wYwHaHa?o=7rm=3&rs=1&pid=ImgDetMain&o=7&rm=3" class="logo_image" alt="" style="height:100px; width: 100px; margin-left: 20px"/>
                </div>
                <!--arcodianpart-->
                <div class="accordion" id="accordionExample">
                    <div class="accordion-item">
                        <h2 class="accordion-header">
                            <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
                                Timesheets
                            </button>
                        </h2>
                        <div id="collapseOne" class="accordion-collapse collapse show" data-bs-parent="#accordionExample">
                            <div class="accordion-body justify-content-around flex-column d-flex">
                                <a href="${pageContext.request.contextPath}/DailyTimeSheet.jsp" style="text-decoration: none; color: black; ">Daily</a>
                                <br>
                                <a href="${pageContext.request.contextPath}/WeeklyTimeSheet.jsp" style="text-decoration: none; color: black">Weekly</a>
                                <br>
                                <a href="${pageContext.request.contextPath}/BiWeeklyTimeSheet.jsp" style="text-decoration: none; color: black">Bi-Weekly</a>
                                <br>
                                <a href="${pageContext.request.contextPath}/MonthlyTimeSheet.jsp" style="text-decoration: none; color: black">Monthly</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
                            
            <div class="main_content" style="width: 85%; padding: 20px 50px; background: #F0F0F0">
                <!--line 1-->
                <div class="justify-content-between d-flex align-items-center" style="margin-bottom: 20px">
                    <h1>Daily Timesheet</h1>
                    <!-- Button trigger modal -->
                    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#exampleModal" style="padding: 10px 20px">
                        + Add Timesheet
                    </button>

                    <!-- Modal -->
                    <div class="modal fade" id="exampleModal" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-header">
                                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                </div>
                                <div class="modal-body">
                                    <h1 class="modal-title fs-5" id="exampleModalLabel">Add Timesheet</h1>
                                    <form action="action" method="">
                                        <p style="margin-bottom: 0px; margin-top: 10px"> People </p>
                                        <select>
                                            <option>
                                                chỗ này để userid.getnamefull name
                                            </option>
                                        </select>
                                        <br/>

                                        <p style="margin-bottom: 0px; margin-top: 10px"> Project </p>
                                        <select>
                                            <option>
                                                chỗ này tạm thời lấy tất cả các project đã, cũng chưa thấy assigned to project ở đâu.
                                            </option>
                                        </select>
                                        <br>

                                        <p style="margin-bottom: 0px; margin-top: 10px"> Task </p>
                                        <select>
                                            <option>
                                                chỗ này lấy hết các Task có trong Project được chọn.
                                            </option>
                                        </select>
                                        <br>

                                        <p style="margin-bottom: 0px; margin-top: 10px"> Select Date </p>
                                            <input type="date" />
                                        <br>

                                        <p style="margin-bottom: 0px; margin-top: 10px"> Select Start Hour </p>
                                        <input type="text"/>
                                        <select>
                                            <option>AM</option>
                                            <option>PM</option>
                                        </select>
                                        <br>

                                        <p style="margin-bottom: 0px; margin-top: 10px"> Select End Hour </p>
                                        <input type="text" />
                                        <select>
                                            <option>AM</option>
                                            <option>PM</option>
                                        </select>
                                        <br>

                                    </form>



                                </div>

                            </div>
                        </div>
                    </div>
                </div>
                <!--line2-->
                <div class="d-flex line2">
                    <div style="width: 20%">
                        <p>Project</p>
                        <select style="width: 80%;padding: 5px;border: none; border-radius: 5px ">
                            <option >All</option>
                        </select>
                    </div>
                    <div style="width: 20%">
                        <p>Task</p> 
                        <select style="width: 80%;padding: 5px;border: none; border-radius: 5px ">
                            <option>choose the task</option>
                        </select>
                    </div>
                    <div style="width: 20%">
                        <p>Date</p>
                        <input type="date" style="width: 80%;padding: 5px;border: none; border-radius: 5px ">
                    </div>
                </div>
            </div>

        </div>

        <!--import bootstrap-->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"
                integrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" 
        crossorigin="anonymous"></script>
    </body>

</html>
