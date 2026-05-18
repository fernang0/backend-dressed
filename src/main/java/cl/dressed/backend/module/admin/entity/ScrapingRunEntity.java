package cl.dressed.backend.module.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scraping_runs")
public class ScrapingRunEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "store_id")
    private Integer storeId;
    
    @Column(name = "type")
    private String type;
    
    @Column(name = "status")
    private String status;
    
    @Column(name = "new_garments")
    private Integer newGarments = 0;
    
    @Column(name = "updated_garments")
    private Integer updatedGarments = 0;
    
    @Column(name = "skipped_duplicates")
    private Integer skippedDuplicates = 0;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
    
    // ─────────────────────────────────────────────────────
    // Constructores
    // ─────────────────────────────────────────────────────
    
    public ScrapingRunEntity() {}
    
    public ScrapingRunEntity(Integer storeId, String type) {
        this.storeId = storeId;
        this.type = type;
        this.status = "RUNNING";
        this.startedAt = LocalDateTime.now();
        this.newGarments = 0;
        this.updatedGarments = 0;
        this.skippedDuplicates = 0;
    }
    
    // ─────────────────────────────────────────────────────
    // Getters & Setters
    // ─────────────────────────────────────────────────────
    
    public Integer getId() {
        return id;
    }
    
    public Integer getStoreId() {
        return storeId;
    }
    
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getNewGarments() {
        return newGarments;
    }
    
    public void setNewGarments(Integer newGarments) {
        this.newGarments = newGarments;
    }
    
    public Integer getUpdatedGarments() {
        return updatedGarments;
    }
    
    public void setUpdatedGarments(Integer updatedGarments) {
        this.updatedGarments = updatedGarments;
    }
    
    public Integer getSkippedDuplicates() {
        return skippedDuplicates;
    }
    
    public void setSkippedDuplicates(Integer skippedDuplicates) {
        this.skippedDuplicates = skippedDuplicates;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }
    
    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}