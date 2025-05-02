package com.app.documentservice.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "document_tags",
        uniqueConstraints = @UniqueConstraint(columnNames = {"document_id", "tag_id"})
)
public class DocumentTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name =  "document_id", nullable = false, length = 64)
    private String documentId;

    @Column(name = "tag_id", nullable = false, length = 64)
    private String tagId;

    public DocumentTag() {
    }

    public DocumentTag(String documentId, String tagId) {
        this.documentId = documentId;
        this.tagId = tagId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }
}
