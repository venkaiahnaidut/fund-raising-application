package com.fundraising.service.impl;

import com.fundraising.dto.PaymentDto;
import com.fundraising.entity.PaymentEntity;
import com.fundraising.exception.ResourceNotFoundException;
import com.fundraising.repository.PaymentRepository;
import com.fundraising.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<PaymentDto> getAllPayments() {
        List<PaymentEntity> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentEntity -> modelMapper.map(paymentEntity, PaymentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDto getPaymentById(Long id) {
        PaymentEntity paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with id " + id + " not found"));
        return modelMapper.map(paymentEntity, PaymentDto.class);
    }

    @Override
    public PaymentDto createPayment(PaymentDto paymentDto) {
        PaymentEntity paymentEntity = modelMapper.map(paymentDto, PaymentEntity.class);
        paymentEntity = paymentRepository.save(paymentEntity);
        return modelMapper.map(paymentEntity, PaymentDto.class);
    }

    @Override
    public PaymentDto updatePayment(Long id, PaymentDto paymentDto) {
        PaymentEntity existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with id " + id + " not found"));
        existingPayment.setAmount(paymentDto.getAmount());
        existingPayment.setPaymentStatus(paymentDto.getPaymentStatus());
        paymentRepository.save(existingPayment);
        return modelMapper.map(existingPayment, PaymentDto.class);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Payment with id " + id + " not found");
        }
        paymentRepository.deleteById(id);
    }
}
