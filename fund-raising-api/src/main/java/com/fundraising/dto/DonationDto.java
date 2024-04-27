package com.fundraising.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationDto {
    private Long id;
    private Long campaignId;
    private Long userId;
    private String username;
    private String campaignName;
    private BigDecimal amount;
    private String donationStatus;
    private LocalDateTime donationDate;
    private String categoryName;
    private PaymentDto paymentDto;
}
