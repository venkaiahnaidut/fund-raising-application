package com.fundraising.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDto {
    private Long campaignId;
    private Long userId;
    private String title;
    private String description;
    private BigDecimal goalAmount;
    private BigDecimal currentAmount;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")

    private String startDate;
    private String endDate;
    private Long categoryId;
    private String photoUrl;
    private String categoryName;
    private String createdAt;
    private MultipartFile campaignImage;

    private UserDto userDto;
}
