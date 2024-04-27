package com.fundraising.repository;

import com.fundraising.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<CampaignEntity, Long> {

    List<CampaignEntity> findByUserIdOrderByEndDateAsc(Long userId);
    @Query("SELECT MAX(campaign.campaignId) FROM CampaignEntity campaign")
    Long getMaxCampaignId();

    List<CampaignEntity> findAllByOrderByCreatedAtDesc();
}

