package com.fundraising.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundraising.dto.CampaignAnalyticsDto;
import com.fundraising.dto.CampaignDto;
import com.fundraising.dto.DonationDto;
import com.fundraising.exception.ResourceNotFoundException;
import com.fundraising.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignsController {

    @Autowired
    private CampaignService campaignService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<CampaignDto>> getAllCampaigns() {
        List<CampaignDto> campaigns = campaignService.getAllCampaigns();
        return ResponseEntity.ok(campaigns);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CampaignDto> getCampaignById(@PathVariable("id") Long id) {
        CampaignDto campaign = campaignService.getCampaignById(id);
        return ResponseEntity.ok(campaign);
    }

    @GetMapping("/{id}/donations")
    public ResponseEntity<List<DonationDto>> getCampaignDonationsById(@PathVariable("id") Long id) {
        List<DonationDto> donationDtos = campaignService.getCampaignDonationsById(id);
        return ResponseEntity.ok(donationDtos);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<CampaignDto>> getCampaignByUserId(@PathVariable("id") Long id) {
        List<CampaignDto> campaigns = campaignService.getCampaignByUserId(id);
        return ResponseEntity.ok(campaigns);
    }

    @PostMapping("/user/create")
    public ResponseEntity<CampaignDto> createUserCampaign(
            @RequestPart("campaignDto") String campaignDto
            , @RequestPart("imageFile") MultipartFile file)  throws ResourceNotFoundException, JsonProcessingException {
        CampaignDto campaign = objectMapper.readValue(campaignDto, CampaignDto.class);
        campaign.setCampaignImage(file);

        CampaignDto createdCampaign = campaignService.createUserCampaign(campaign);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCampaign);
    }

    @PutMapping("/{id}/user/update")
    public ResponseEntity<CampaignDto> updateUserCampaign(@PathVariable("id") Long id,
            @RequestPart("campaignDto") String campaignDto
            , @RequestPart("imageFile") MultipartFile file)  throws ResourceNotFoundException, JsonProcessingException {
        CampaignDto campaign = objectMapper.readValue(campaignDto, CampaignDto.class);
        campaign.setCampaignImage(file);

        CampaignDto createdCampaign = campaignService.updateUserCampaign(id,campaign);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCampaign);
    }

    @PutMapping("/{id}/update-status/{status}")
    public ResponseEntity<CampaignDto> updateUserCampaignStatus(@PathVariable("id") Long id
            , @PathVariable("status") String status) {
        CampaignDto updatedCampaign = campaignService.updateUserCampaignStatus(id, status);
        return ResponseEntity.ok(updatedCampaign);
    }

    @PostMapping
    public ResponseEntity<CampaignDto> createCampaign(@RequestBody CampaignDto campaignDto) {
        CampaignDto createdCampaign = campaignService.createCampaign(campaignDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCampaign);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampaignDto> updateCampaign(@PathVariable("id") Long id, @RequestBody CampaignDto campaignDto) {
        CampaignDto updatedCampaign = campaignService.updateCampaign(id, campaignDto);
        return ResponseEntity.ok(updatedCampaign);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable("id") Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}
