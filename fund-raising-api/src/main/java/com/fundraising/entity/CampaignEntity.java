package com.fundraising.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "campaigns")
public class CampaignEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long campaignId;
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id") // Specify the column name in the campaigns table
    private CategoryEntity category;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private BigDecimal goalAmount;

    private BigDecimal currentAmount;

    private String status;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String photoUrl;

    private LocalDateTime createdAt;
}
