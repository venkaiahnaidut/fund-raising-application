package com.fundraising.service;

import com.fundraising.dto.PaymentDto;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> getAllPayments();
    PaymentDto getPaymentById(Long id);
    PaymentDto createPayment(PaymentDto paymentDto);
    PaymentDto updatePayment(Long id, PaymentDto paymentDto);
    void deletePayment(Long id);
}
