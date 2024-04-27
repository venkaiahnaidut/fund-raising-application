package com.fundraising.entity;

import com.fundraising.entity.DonationEntity;
import com.fundraising.entity.UserEntity;
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
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long paymentId;
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "donation_id", referencedColumnName = "id")
    private DonationEntity donation;


    private BigDecimal amount;

    private LocalDateTime paymentDate;

    private String paymentStatus;

    private String cardNumber;

    private Integer cardExpiryMonth;

    private Integer cardExpiryYear;
}
