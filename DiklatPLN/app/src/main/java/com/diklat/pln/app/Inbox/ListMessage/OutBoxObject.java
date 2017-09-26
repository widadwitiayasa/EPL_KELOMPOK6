package com.diklat.pln.app.Inbox.ListMessage;

/**
 * Created by Fandy Aditya on 6/7/2017.
 */

public class OutBoxObject {
    String id;
    String name;
    String status;
    String create;
    String approvalDate;
    String start;
    String end;

    public OutBoxObject(String id, String name, String status, String create, String approvalDate, String start, String end) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.create = create;
        this.approvalDate = approvalDate;
        this.start = start;
        this.end = end;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getCreate() {
        return create;
    }

    public String getApprovalDate() {
        return approvalDate;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }
}
