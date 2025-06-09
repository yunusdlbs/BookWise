package com.example.bookwise.models;

public class UserInfo {
    private String id;
    private String username;
    private int borrowedCount;
    private boolean isGraylisted;

    public UserInfo() {}

    public UserInfo(String id, String username, int borrowedCount, boolean isGraylisted) {
        this.id = id;
        this.username = username;
        this.borrowedCount = borrowedCount;
        this.isGraylisted = isGraylisted;
    }

    // Getters - Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getBorrowedCount() { return borrowedCount; }
    public void setBorrowedCount(int borrowedCount) { this.borrowedCount = borrowedCount; }

    public boolean isGraylisted() { return isGraylisted; }
    public void setGraylisted(boolean graylisted) { isGraylisted = graylisted; }
}
