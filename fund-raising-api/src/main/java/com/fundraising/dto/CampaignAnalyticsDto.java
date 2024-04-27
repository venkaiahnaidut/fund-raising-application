package com.fundraising.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampaignAnalyticsDto {
    private  Map<Integer, Integer> campaignChartData;
    private  Map<String, Map<String, BigDecimal>> donationByCategoryData;

    public Map<Integer, Integer> getCampaignChartData() {
        return campaignChartData;
    }

    public Map<String, Map<String, BigDecimal>> getDonationByCategoryData() {
        return donationByCategoryData;
    }
}
