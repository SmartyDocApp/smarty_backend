package com.app.documentservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "folders")
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "parent_id", length = 64)
    private String parentId; // Hiérarchie: un dossier peut avoir un parent et un ou plusieurs enfants

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "workspace_id", length = 64)
    private String workspaceId; // null si personnel

    public Folder() {
    }

    public Folder(String name, String parentId, String userId, String workspaceId) {
        this.name = name;
        this.parentId = parentId;
        this.userId = userId;
        this.workspaceId = workspaceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }
}
