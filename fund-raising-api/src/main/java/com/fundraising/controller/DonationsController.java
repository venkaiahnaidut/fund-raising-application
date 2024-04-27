package com.fundraising.controller;

import com.fundraising.dto.DonationDto;
import com.fundraising.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/donations")
public class DonationsController {

    @Autowired
    private DonationService donationService;

    @GetMapping
    public ResponseEntity<List<DonationDto>> getAllDonations() {
        List<DonationDto> donations = donationService.getAllDonations();
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<Integer, BigDecimal>> getAllDonationsAnalytics() {
        Map<Integer, BigDecimal> donations = donationService.getAllDonationsAnalytics();
        return ResponseEntity.ok(donations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DonationDto> getDonationById(@PathVariable("id") Long id) {
        DonationDto donation = donationService.getDonationById(id);
        return ResponseEntity.ok(donation);
    }

    @PostMapping
    public ResponseEntity<DonationDto> createDonation(@RequestBody DonationDto donationDto) {
        DonationDto createdDonation = donationService.createDonation(donationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDonation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DonationDto> updateDonation(@PathVariable("id") Long id, @RequestBody DonationDto donationDto) {
        DonationDto updatedDonation = donationService.updateDonation(id, donationDto);
        return ResponseEntity.ok(updatedDonation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable("id") Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }
}
