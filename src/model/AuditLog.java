package model;

import java.sql.Timestamp;

public class AuditLog {
    private int logId;
    private Integer userId;
    private String userName;
    private String userRole;
    private String action;
    private Timestamp actionDate;

    public AuditLog() {}

    public AuditLog(int logId, Integer userId, String userName, String userRole, String action, Timestamp actionDate) {
        this.logId = logId;
        this.userId = userId;
        this.userName = userName;
        this.userRole = userRole;
        this.action = action;
        this.actionDate = actionDate;
    }

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Timestamp getActionDate() { return actionDate; }
    public void setActionDate(Timestamp actionDate) { this.actionDate = actionDate; }
}
