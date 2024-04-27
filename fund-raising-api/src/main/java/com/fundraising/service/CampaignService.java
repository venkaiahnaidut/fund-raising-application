package com.fundraising.service;

import com.fundraising.dto.CampaignAnalyticsDto;
import com.fundraising.dto.CampaignDto;
import com.fundraising.dto.DonationDto;

import java.util.List;

public interface CampaignService {
    List<CampaignDto> getAllCampaigns();
    CampaignDto getCampaignById(Long id);
    CampaignDto createCampaign(CampaignDto campaignDto);
    CampaignDto updateCampaign(Long id, CampaignDto campaignDto);
    void deleteCampaign(Long id);
    public CampaignAnalyticsDto getCampaignAnalytics();

    List<CampaignDto> getCampaignByUserId(Long id);

    CampaignDto createUserCampaign(CampaignDto campaign);

    CampaignDto updateUserCampaign(Long id, CampaignDto campaign);

    List<DonationDto> getCampaignDonationsById(Long id);
    public CampaignDto updateUserCampaignStatus(Long id, String status);
}
