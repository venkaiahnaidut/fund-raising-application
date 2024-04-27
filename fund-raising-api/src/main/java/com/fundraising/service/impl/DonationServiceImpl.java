package com.fundraising.service.impl;

import com.fundraising.dto.DonationDto;
import com.fundraising.entity.CampaignEntity;
import com.fundraising.entity.DonationEntity;
import com.fundraising.entity.PaymentEntity;
import com.fundraising.exception.ResourceNotFoundException;
import com.fundraising.repository.CampaignRepository;
import com.fundraising.repository.DonationRepository;
import com.fundraising.repository.PaymentRepository;
import com.fundraising.service.DonationService;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;


@Service
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {

    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private  CampaignRepository campaignRepository;

    private final ModelMapper modelMapper;

    @Override
    public List<DonationDto> getAllDonations() {
        List<DonationEntity> donations = donationRepository.findAll();
        return donations.parallelStream()
                .map(donationEntity -> {
                    DonationDto donationDto = modelMapper.map(donationEntity, DonationDto.class);
                    // Map user information
                    donationDto.setUserId(donationEntity.getUser().getUserId());
                    donationDto.setUsername(donationEntity.getUsername());
                    // Map campaign information
                    donationDto.setCampaignId(donationEntity.getCampaign().getCampaignId());
                    // Add other mappings as needed
                    return donationDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, BigDecimal> getAllDonationsAnalytics() {
        List<Object[]> donations = donationRepository.findAllForAnalytics();

        // Perform data transformation
        Map<Integer, BigDecimal> donationByYear = new ConcurrentHashMap<>();
        donations.parallelStream().forEach(donation -> {
            BigDecimal amount = (BigDecimal) donation[0];
            LocalDateTime donationDate = (LocalDateTime) donation[1];
            int year = donationDate.getYear(); // Extract year from LocalDateTime
            donationByYear.merge(year, amount, BigDecimal::add); // Add amount to existing value for the year
        });

        return donationByYear;
    }

    @Override
    public DonationDto getDonationById(Long id) {
        DonationEntity donationEntity = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation with id " + id + " not found"));
        return modelMapper.map(donationEntity, DonationDto.class);
    }

    @Transactional
    @Override
    public DonationDto createDonation(DonationDto donationDto) {
        try {
            Optional<CampaignEntity> campaignEntity = campaignRepository.findById(donationDto.getCampaignId());
            if(campaignEntity.isEmpty()) throw new ResourceNotFoundException("Campaign not found " + donationDto.getCampaignId());

            CampaignEntity campaign = campaignEntity.get();
            if (campaign.getCurrentAmount() != null && donationDto.getAmount() != null) {
                campaign.setCurrentAmount(campaign.getCurrentAmount().add(donationDto.getAmount()));
            }else if(donationDto.getAmount() != null){
                campaign.setCurrentAmount(donationDto.getAmount());
            }
            campaignRepository.save(campaign);

            DonationEntity donationEntity = modelMapper.map(donationDto, DonationEntity.class);
            donationEntity.setDonationDate(LocalDateTime.now());
            donationEntity.setCampaign(campaignEntity.get());
            donationEntity = donationRepository.save(donationEntity);
            entityManager.flush();

            Long donationId = donationEntity.getDonationId();

            PaymentEntity paymentEntity = new PaymentEntity();
            paymentEntity.setUserId(donationDto.getUserId());
            paymentEntity.setAmount(donationDto.getAmount());
            paymentEntity.setCardNumber(donationDto.getPaymentDto().getCardNumber());
            paymentEntity.setCardExpiryMonth(donationDto.getPaymentDto().getCardExpiryMonth());
            paymentEntity.setCardExpiryYear(donationDto.getPaymentDto().getCardExpiryYear());
            paymentEntity.setDonation(donationEntity);
            paymentEntity.setPaymentStatus("Success");
            paymentEntity.setPaymentDate(LocalDateTime.now());
            //paymentEntity.setDonationId(donationId); // Set the donationId obtained from the saved DonationEntity

             paymentRepository.save(paymentEntity);

            return donationDto;
        } catch (Exception e) {
            throw new RuntimeException("Error creating donation and payment entities", e);
        }
    }


    @Override
    public DonationDto updateDonation(Long id, DonationDto donationDto) {
        DonationEntity existingDonation = donationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Donation with id " + id + " not found"));
        existingDonation.setAmount(donationDto.getAmount());
        existingDonation.setDonationStatus(donationDto.getDonationStatus());
        donationRepository.save(existingDonation);
        return modelMapper.map(existingDonation, DonationDto.class);
    }

    @Override
    public void deleteDonation(Long id) {
        if (!donationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Donation with id " + id + " not found");
        }
        donationRepository.deleteById(id);
    }
}
