package com.tudu.tudu;

public class Task {

    private String title,content,taskUid,userUid;
    private int importance;

    public Task(String title, String content, String taskUid, String userUid,int importance) {
        this.title = title;
        this.content = content;
        this.taskUid = taskUid;
        this.userUid = userUid;
        this.importance = importance;
    }



    //For firebase use
    public Task() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTaskUid() {
        return taskUid;
    }

    public void setTaskUid(String taskUid) {
        this.taskUid = taskUid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }
}
