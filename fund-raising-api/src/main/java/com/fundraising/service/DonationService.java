package com.fundraising.service;

import com.fundraising.dto.DonationDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DonationService {
    List<DonationDto> getAllDonations();
    DonationDto getDonationById(Long id);
    DonationDto createDonation(DonationDto donationDto);
    DonationDto updateDonation(Long id, DonationDto donationDto);
    void deleteDonation(Long id);
    Map<Integer, BigDecimal> getAllDonationsAnalytics();
}
