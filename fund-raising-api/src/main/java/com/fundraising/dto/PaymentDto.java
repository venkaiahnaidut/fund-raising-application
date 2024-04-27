package com.fundraising.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {
    private Long id;
    private Long userId;
    private Long donationId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String paymentStatus;
    private String cardNumber;
    private Integer cardExpiryMonth;
    private Integer cardExpiryYear;
}
