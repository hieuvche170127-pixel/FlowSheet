<jsp:include page="/nghiapages/layout_header.jsp" />

<%@page import="java.util.Map"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List, entity.UserAccount, entity.Team, entity.Project"%>

<%
    Team team = (Team) request.getAttribute("team");
    List<UserAccount> userAccounts = (List<UserAccount>) request.getAttribute("userAccounts");
    List<Project> projects = (List<Project>) request.getAttribute("projects");

    String activeTab = (String) request.getAttribute("activeTab");
    if (activeTab == null) {
        activeTab = "members";
    }

    Map<Integer, String> roleMap = (Map<Integer, String>) request.getAttribute("roleMap");

    Boolean canManageTeamObj = (Boolean) request.getAttribute("canManageTeam");
    boolean canManageTeam = (canManageTeamObj != null && canManageTeamObj);
%>



<style>
    /* IMPORTANT: do NOT style body/html here */
    .page-wrapper {
        max-width: 1200px;
        margin: 20px auto;
        background-color: #ffffff;
        border-radius: 12px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.06);
        padding: 20px 24px 32px;
        box-sizing: border-box;
    }

    .top-bar {
        display: flex;
        align-items: center;
        margin-bottom: 18px;
        gap: 10px;
    }

    .back-link {
        display: inline-flex;
        align-items: center;
        text-decoration: none;
        color: #333;
        font-size: 14px;
    }

    .back-arrow {
        display: inline-block;
        width: 26px;
        height: 26px;
        border-radius: 50%;
        border: 1px solid #ccc;
        text-align: center;
        line-height: 26px;
        margin-right: 6px;
        font-weight: bold;
    }

    .team-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 14px 0 10px;
        border-bottom: 1px solid #e1e4f0;
        margin-bottom: 14px;
        gap: 16px;
    }

    /* Ensure the left header section is a flex row */
    .team-header-left {
        display: flex;
        align-items: center;
        gap: 16px;
        flex: 1 1 auto;
        min-width: 320px;
    }

    .team-info-left {
        display: flex;
        align-items: center;
        gap: 14px;
    }

    .team-avatar {
        width: 56px;          /* pick 52–64 */
        height: 56px;
        min-width: 56px;
        min-height: 56px;
        border-radius: 50%;
        flex: 0 0 56px;        /* IMPORTANT: prevents shrink */
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 22px;
        font-weight: 700;
    }

    .team-name {
        font-size: 18px;
        font-weight: 600;
    }

    .team-actions-right {
        display: flex;
        gap: 10px;
    }

    .btn-outline {
        padding: 8px 16px;
        border-radius: 20px;
        border: 1px solid #ddd;
        background-color: #fff;
        font-size: 13px;
        cursor: pointer;
    }

    .btn-danger {
        border-color: #ff5a6b;
        color: #ff5a6b;
    }

    .tab-row {
        display: flex;
        align-items: center;
        margin-top: 14px;
        margin-bottom: 16px;
        gap: 8px;
    }

    .tab {
        padding: 8px 18px;
        border-radius: 20px;
        border: 1px solid #00bfa5;
        font-size: 13px;
        cursor: pointer;
        background-color: #fff;
        color: #00bfa5;
        text-decoration: none;
        display: inline-block;
    }

    .tab.active {
        background-color: #00bfa5;
        color: #fff;
    }

    .search-wrapper {
        margin-left: auto;
        display: flex;
        align-items: center;
        gap: 6px;
    }

    .search-input {
        padding: 7px 10px;
        border-radius: 18px;
        border: 1px solid #ccd2e0;
        font-size: 13px;
        min-width: 230px;
    }

    .search-btn {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        border: none;
        background-color: #00bfa5;
        color: #fff;
        font-size: 16px;
        cursor: pointer;
    }

    .cards-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
        gap: 16px;
        margin-top: 10px;
    }

    .member-card {
        background-color: #fff;
        border-radius: 12px;
        border: 1px solid #e0e4f0;
        padding: 18px 16px;
        text-align: center;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
    }

    .member-avatar {
        width: 48px;
        height: 48px;
        border-radius: 50%;
        margin: 0 auto 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 600;
        font-size: 18px;
        color: #fff;
        background-color: #4c6fff;
    }

    .member-username {
        font-size: 14px;
        font-weight: 700;
        margin-bottom: 2px;
        color: #222;
    }
    .member-fullname {
        font-size: 13px;
        color: #555;
        margin-bottom: 2px;
    }
    .member-email {
        font-size: 12px;
        color: #888;
        margin-bottom: 8px;
    }
    .member-role {
        font-size: 12px;
        color: #00bfa5;
        font-weight: 600;
        margin-bottom: 10px;
    }

    .member-actions {
        margin-top: 10px;
        display: flex;
        flex-direction: column;
        gap: 8px;
    }

    .member-actions select {
        padding: 6px 10px;
        border-radius: 10px;
        border: 1px solid #ccd2e0;
        font-size: 13px;
    }

    .btn-primary {
        padding: 7px 12px;
        border-radius: 12px;
        border: none;
        background: #00bfa5;
        color: #fff;
        cursor: pointer;
        font-size: 13px;
    }

    .btn-kick {
        padding: 7px 12px;
        border-radius: 12px;
        border: 1px solid #ff5a6b;
        background: #fff;
        color: #ff5a6b;
        cursor: pointer;
        font-size: 13px;
    }

    .team-meta {
        display: flex;
        flex-direction: column;
        gap: 4px;
    }

    .team-desc {
        max-width: 560px;
        font-size: 13px;
        color: #6b7280;
        line-height: 1.35;
        white-space: normal;
        overflow-wrap: anywhere; /* very important for long Vietnamese/URLs */
    }

    .team-actions-right form {
        align-items: flex-start !important; /* so textarea aligns nicely */
    }

    .team-desc-input {
        width: 340px;
        min-height: 38px;
        max-height: 120px;
        resize: vertical;
        padding: 6px 10px;
        border-radius: 10px;
        border: 1px solid #ccd2e0;
        font-size: 13px;
        line-height: 1.3;
        outline: none;
    }

    .team-desc-input:focus {
        border-color: #00bfa5;
    }

    @media (max-width: 900px) {
        .team-header {
            flex-direction: column;
            align-items: flex-start;
            gap: 12px;
        }
        .team-actions-right {
            width: 100%;
            flex: 0 0 auto;
        }
        .team-desc-input {
            width: 100%;
        }
        .team-desc {
            max-width: 100%;
        }
    }

    .team-actions-right form {
        display: flex;
        flex-wrap: wrap;     /* allow wrapping instead of squeezing */
        gap: 10px;
        align-items: center;
    }

    /* Control textarea width so it doesn't explode */
    .team-desc-input {
        width: 360px;
        max-width: 42vw;     /* responsive */
    }
</style>

<div class="page-wrapper">

    <div class="top-bar">
        <a href="<%= request.getContextPath()%>/team" class="back-link">
            <span class="back-arrow">&#8592;</span>
            Back to all teams
        </a>
    </div>

    <div class="team-header">
        <div class="team-info-left">
            <%
                String teamInitial = "T";
                if (team != null && team.getTeamName() != null && !team.getTeamName().isEmpty()) {
                    teamInitial = team.getTeamName().substring(0, 1).toUpperCase();
                }
            %>
            <div class="team-avatar"><%= teamInitial%></div>
            <div class="team-meta">
                <div class="team-name"><%= (team != null && team.getTeamName() != null) ? team.getTeamName() : "Team"%></div>

                <div class="team-desc">
                    <%= (team != null && team.getDescription() != null && !team.getDescription().trim().isEmpty())
                            ? team.getDescription()
                            : "No description"%>
                </div>
            </div>
        </div>

        <% if (canManageTeam) {%>
        <div class="team-actions-right">
            <form method="post" action="<%= request.getContextPath()%>/teamMember" style="display:flex; gap:6px; align-items:center;">
                <input type="hidden" name="action" value="updateTeam">
                <input type="hidden" name="teamId" value="<%= team.getTeamID()%>">

                <input type="text" name="teamName"
                       value="<%= team.getTeamName()%>"
                       required
                       style="padding:6px 10px;border-radius:10px;border:1px solid #ccd2e0;">

                <textarea name="description" class="team-desc-input"
                          placeholder="Team description..."><%= team.getDescription() != null ? team.getDescription() : ""%></textarea>

                <button class="btn-outline" type="submit">Update Team</button>
            </form>

            <form method="post"
                  action="<%= request.getContextPath()%>/teamMember"
                  onsubmit="return confirm('Are you sure you want to delete this team?');">
                <input type="hidden" name="action" value="deleteTeam">
                <input type="hidden" name="teamId" value="<%= team.getTeamID()%>">
                <button class="btn-outline btn-danger" type="submit">Delete Team</button>
            </form>
        </div>
        <% }%>
    </div>

    <div class="tab-row">
        <a class="tab <%= "members".equals(activeTab) ? "active" : ""%>"
           href="<%= request.getContextPath()%>/teamMember?teamId=<%= team.getTeamID()%>&tab=members">
            Team Members
        </a>

        <a class="tab <%= "projects".equals(activeTab) ? "active" : ""%>"
           href="<%= request.getContextPath()%>/teamMember?teamId=<%= team.getTeamID()%>&tab=projects">
            Assigned Projects
        </a>

        <div class="search-wrapper">
            <form method="get" action="<%= request.getContextPath()%>/teamMember" style="display:flex;align-items:center;gap:6px;">
                <input type="hidden" name="teamId" value="<%= team.getTeamID()%>">
                <input type="hidden" name="tab" value="members">
                <input type="text"
                       class="search-input"
                       name="q"
                       value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : ""%>"
                       placeholder="Search by member name..." />
                <button class="search-btn" type="submit">&#128269;</button>
            </form>
        </div>
    </div>

    <% if ("members".equals(activeTab)) { %>
    <div class="cards-grid">
        <%
            if (userAccounts != null) {
                for (UserAccount u : userAccounts) {
                    String userName = u.getUsername();
                    String fullName = u.getFullName();
                    String email = u.getEmail();

                    int roleId = u.getRoleID();
                    String roleName = (roleMap != null) ? roleMap.get(roleId) : "";

                    String initials = "";
                    if (userName != null && !userName.isEmpty()) {
                        initials = userName.substring(0, Math.min(2, userName.length())).toUpperCase();
                    }
        %>
        <div class="member-card">
            <div class="member-avatar"><%= initials%></div>
            <div class="member-username"><%= userName%></div>
            <div class="member-fullname"><%= fullName%></div>
            <div class="member-email"><%= email%></div>
            <div class="member-role"><%= roleName != null ? roleName : ""%></div>

            <% if (canManageTeam) {%>
            <div class="member-actions">

                <form action="<%= request.getContextPath()%>/teamMember" method="post">
                    <input type="hidden" name="action" value="changeRole"/>
                    <input type="hidden" name="teamId" value="<%= team.getTeamID()%>"/>
                    <input type="hidden" name="userId" value="<%= u.getUserID()%>"/>

                    <select name="roleId">
                        <option value="4" <%= (u.getRoleID() == 4) ? "selected" : ""%>>Team Member</option>
                        <option value="5" <%= (u.getRoleID() == 5) ? "selected" : ""%>>Team Leader</option>
                    </select>

                    <button class="btn-primary" type="submit">Change role</button>
                </form>

                <button type="button"
                        class="btn-kick"
                        onclick="openKickModal(<%= team.getTeamID()%>, <%= u.getUserID()%>, '<%= u.getUsername()%>')">
                    Kick Team Member
                </button>

            </div>
            <% } %>
        </div>
        <%
                }
            }
        %>
    </div>
    <% } %>

    <% if ("projects".equals(activeTab)) { %>
    <div class="cards-grid">
        <%
            if (projects != null) {
                for (Project p : projects) {
                    String code = p.getProjectCode();
                    String name = p.getProjectName();
                    String desc = p.getDescription();
                    boolean active = p.isIsActive();

                    String initials = "";
                    if (code != null && !code.isEmpty()) {
                        initials = code.substring(0, Math.min(2, code.length())).toUpperCase();
                    }
        %>
        <div class="member-card">
            <div class="member-avatar"><%= initials%></div>
            <div class="member-username"><%= code%></div>
            <div class="member-fullname"><%= name%></div>
            <div class="member-email"><%= desc%></div>
            <div class="member-role"><%= active ? "Active" : "Inactive"%></div>
        </div>
        <%
                }
            }
        %>
    </div>
    <% }%>

</div>

<!-- Kick Reason Modal -->
<div id="kickModalOverlay" style="display:none; position:fixed; inset:0; background:rgba(0,0,0,0.45); z-index:9998;"></div>

<div id="kickModal" style="display:none; position:fixed; top:50%; left:50%; transform:translate(-50%,-50%);
     width:420px; max-width:92vw; background:#fff; border-radius:14px; padding:16px; z-index:9999;
     box-shadow:0 10px 40px rgba(0,0,0,0.2);">

    <div style="display:flex; align-items:center; justify-content:space-between; gap:10px;">
        <h3 style="margin:0; font-size:18px;">Kick Team Member</h3>
        <button type="button" onclick="closeKickModal()"
                style="border:none;background:transparent;font-size:20px;cursor:pointer;">&times;</button>
    </div>

    <p style="margin:10px 0 8px;">
        You are about to kick <b id="kickUsername"></b>.
    </p>

    <form id="kickForm" method="post" action="<%= request.getContextPath()%>/teamMember">
        <input type="hidden" name="action" value="kick">
        <input type="hidden" name="teamId" id="kickTeamId">
        <input type="hidden" name="userId" id="kickUserId">

        <label for="kickReason" style="display:block; font-weight:600; margin:10px 0 6px;">Reason</label>
        <textarea id="kickReason" name="reason" rows="4" required
                  style="width:100%; resize:none; padding:10px; border-radius:10px; border:1px solid #ccd2e0;"
                  placeholder="Enter the reason why this member is being kicked..."></textarea>

        <div style="display:flex; justify-content:flex-end; gap:8px; margin-top:12px;">
            <button type="button" class="btn-outline" onclick="closeKickModal()">Cancel</button>
            <button type="submit" class="btn-outline btn-danger">Confirm Kick</button>
        </div>
    </form>
</div>
<%
    String error = request.getParameter("msg");
    if (error != null) {
%>
<div style="margin:12px 0;padding:10px;border:1px solid #f5c2c7;background:#f8d7da;color:#842029;border-radius:6px;">
    <%= error%>
</div>
<%
    }
%>
<script>
    function openKickModal(teamId, userId, username) {
        document.getElementById("kickTeamId").value = teamId;
        document.getElementById("kickUserId").value = userId;
        document.getElementById("kickUsername").innerText = username || "";
        document.getElementById("kickReason").value = "";

        document.getElementById("kickModalOverlay").style.display = "block";
        document.getElementById("kickModal").style.display = "block";
        document.getElementById("kickReason").focus();
    }

    function closeKickModal() {
        document.getElementById("kickModal").style.display = "none";
        document.getElementById("kickModalOverlay").style.display = "none";
    }

    // Optional: close on overlay click / ESC
    document.getElementById("kickModalOverlay")?.addEventListener("click", closeKickModal);
    document.addEventListener("keydown", function (e) {
        if (e.key === "Escape")
            closeKickModal();
    });
</script>
<jsp:include page="/nghiapages/layout_footer.jsp" />
