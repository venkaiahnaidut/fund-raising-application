package com.fundraising.repository;

import com.fundraising.entity.DonationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRepository extends JpaRepository<DonationEntity, Long> {
    @Query("SELECT d.amount, d.donationDate FROM DonationEntity d")
    List<Object[]> findAllForAnalytics();
    @Query("SELECT d FROM DonationEntity d WHERE d.campaign.campaignId = :campaignId")
    List<DonationEntity> findByCampaignId(@Param("campaignId") Long campaignId);
    @Query("SELECT d FROM DonationEntity d WHERE d.user.id = :userId")
    List<DonationEntity> findByUserId(@Param("userId") Long userId);

}
