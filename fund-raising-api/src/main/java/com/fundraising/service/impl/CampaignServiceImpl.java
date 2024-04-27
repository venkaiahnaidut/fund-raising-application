package com.fundraising.service.impl;

import com.fundraising.dto.CampaignAnalyticsDto;
import com.fundraising.dto.CampaignDto;
import com.fundraising.dto.DonationDto;
import com.fundraising.dto.UserDto;
import com.fundraising.entity.CampaignEntity;
import com.fundraising.entity.DonationEntity;
import com.fundraising.entity.UserEntity;
import com.fundraising.exception.ResourceNotFoundException;
import com.fundraising.repository.CampaignRepository;
import com.fundraising.repository.DonationRepository;
import com.fundraising.repository.UserRepository;
import com.fundraising.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final ModelMapper modelMapper;
    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    @Override
    public List<CampaignDto> getAllCampaigns() {
        List<CampaignEntity> campaigns = campaignRepository.findAllByOrderByCreatedAtDesc();
        Set<Long> userIds = campaigns.stream()
                .map(CampaignEntity::getUserId)
                .collect(Collectors.toSet());
        List<UserEntity> users = userRepository.findAllById(userIds);

        // Create a map of userId to UserDetailsDto for efficient lookup
        Map<Long, UserDto> userDetailsMap = users.stream()
                .collect(Collectors.toMap(UserEntity::getUserId,
                        user -> {
                            UserDto userDetailsDto = new UserDto();
                            userDetailsDto.setId(user.getUserId());
                            userDetailsDto.setFirstName(user.getFirstName());
                            userDetailsDto.setLastName(user.getLastName());
                            userDetailsDto.setEmail(user.getEmail());
                            return userDetailsDto;
                        }));

        return campaigns.parallelStream()
                .map(campaignEntity -> {
                    CampaignDto campaignDto = modelMapper.map(campaignEntity, CampaignDto.class);
                    String categoryName = campaignEntity.getCategory().getName();
                    campaignDto.setCategoryName(categoryName);

                    // Set user details from the map
                    UserDto userDto = userDetailsMap.get(campaignEntity.getUserId());
                    if (userDto != null) {
                        campaignDto.setUserDto(userDto);
                    }

                    return campaignDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CampaignAnalyticsDto getCampaignAnalytics() {
        List<CampaignEntity> campaigns = campaignRepository.findAll();

        // Calculate campaign data by year using parallel stream
        Map<Integer, Integer> campaignCountsByYear = campaigns.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        campaign -> campaign.getCreatedAt().getYear(),
                        Collectors.summingInt(campaign -> 1)
                ));

        Map<String, Map<String, BigDecimal>> donationByCategory = campaigns.parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        campaign -> campaign.getCategory().getName(),
                        Collector.of(
                                HashMap::new,
                                (map, campaign) -> {
                                    String category = campaign.getCategory().getName();
                                    BigDecimal goalAmount = campaign.getGoalAmount();
                                    BigDecimal currentAmount = campaign.getCurrentAmount();

                                    map.put("totalGoalAmount", map.getOrDefault("totalGoalAmount", BigDecimal.ZERO).add(goalAmount));
                                    map.put("totalCurrentAmount", map.getOrDefault("totalCurrentAmount", BigDecimal.ZERO).add(currentAmount));
                                },
                                (map1, map2) -> {
                                    map1.putAll(map2);
                                    return map1;
                                }
                        )
                ));

        return null;
    }

    @Override
    public List<CampaignDto> getCampaignByUserId(Long userId) {
        List<CampaignEntity> campaigns = campaignRepository.findByUserIdOrderByEndDateAsc(userId);

        return campaigns.parallelStream()
                .map(campaignEntity -> {
                    CampaignDto campaignDto = modelMapper.map(campaignEntity, CampaignDto.class);
                    String categoryName = campaignEntity.getCategory().getName();
                    campaignDto.setCategoryName(categoryName);
                    return campaignDto;
                })
                .collect(Collectors.toList());
    }



    @Override
    public CampaignDto getCampaignById(Long id) {
        CampaignEntity campaignEntity = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign with id " + id + " not found"));
        return modelMapper.map(campaignEntity, CampaignDto.class);
    }

    @Override
    public CampaignDto createCampaign(CampaignDto campaignDto) {
        CampaignEntity campaignEntity = modelMapper.map(campaignDto, CampaignEntity.class);
        campaignRepository.save(campaignEntity);
        return modelMapper.map(campaignEntity, CampaignDto.class);
    }

    @Override
    public CampaignDto createUserCampaign(CampaignDto campaignDto) {
        // Generate the next SQL id
        Long nextId = campaignRepository.getMaxCampaignId();
        if (nextId == null) {
            nextId = 0L; // If no campaigns exist yet
        }
        nextId = nextId+1L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(campaignDto.getStartDate() + " 00:00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse(campaignDto.getEndDate() + " 23:59:59", formatter);

        CampaignEntity campaignEntity = modelMapper.map(campaignDto, CampaignEntity.class);
        campaignEntity.setCampaignId(nextId);
        campaignEntity.setStartDate(startDate);
        campaignEntity.setEndDate(endDate);
        campaignEntity.setStatus("Pending");
        campaignEntity.setCreatedAt(LocalDateTime.now());


        // Handle file upload
        handleFileUpload(campaignDto.getCampaignImage(), campaignEntity);

        campaignEntity = campaignRepository.save(campaignEntity);
        return modelMapper.map(campaignEntity, CampaignDto.class);
    }

    @Override
    public CampaignDto updateUserCampaign(Long id, CampaignDto campaignDto) {

        CampaignEntity existingCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign with id " + id + " not found"));
        Long campaignId = existingCampaign.getCampaignId();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(campaignDto.getStartDate() + " 00:00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse(campaignDto.getEndDate() + " 23:59:59", formatter);

        CampaignEntity campaignEntity = modelMapper.map(campaignDto, CampaignEntity.class);
        campaignEntity.setCampaignId(campaignId);
        campaignEntity.setStartDate(startDate);
        campaignEntity.setEndDate(endDate);
        campaignEntity.setStatus(existingCampaign.getStatus());
        campaignEntity.setCreatedAt(existingCampaign.getCreatedAt());
        // Handle file upload
        handleFileUpload(campaignDto.getCampaignImage(), campaignEntity);

        campaignEntity = campaignRepository.save(campaignEntity);
        return modelMapper.map(campaignEntity, CampaignDto.class);
    }

    @Override
    public CampaignDto updateUserCampaignStatus(Long id, String status) {

        CampaignEntity existingCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign with id " + id + " not found"));
        existingCampaign.setStatus(status);

        campaignRepository.save(existingCampaign);
        return modelMapper.map(existingCampaign, CampaignDto.class);
    }

    @Override
    public List<DonationDto> getCampaignDonationsById(Long id) {
        List<DonationEntity> donations = donationRepository.findByCampaignId(id);
        return donations.parallelStream()
                .map(donationEntity -> {
                    DonationDto donationDto = new DonationDto();
                    donationDto.setDonationStatus(donationEntity.getDonationStatus());
                    donationDto.setUserId(donationEntity.getUser().getUserId());
                    donationDto.setUsername(donationEntity.getUsername());
                    donationDto.setCampaignId(donationEntity.getCampaign().getCampaignId());
                    donationDto.setAmount(donationEntity.getAmount());
                    donationDto.setDonationDate(donationEntity.getDonationDate());
                    return donationDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public CampaignDto updateCampaign(Long id, CampaignDto campaignDto) {
        CampaignEntity existingCampaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign with id " + id + " not found.."));
        existingCampaign.setTitle(campaignDto.getTitle());
        existingCampaign.setDescription(campaignDto.getDescription());
        existingCampaign.setGoalAmount(campaignDto.getGoalAmount());

        // Parse string dates into LocalDateTime objects
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime startDate = LocalDateTime.parse(campaignDto.getStartDate() + " 00:00:00", formatter);
        LocalDateTime endDate = LocalDateTime.parse(campaignDto.getEndDate() + " 23:59:59", formatter);

        existingCampaign.setStartDate(startDate);
        existingCampaign.setEndDate(endDate);
        existingCampaign.setPhotoUrl(campaignDto.getPhotoUrl());

        campaignRepository.save(existingCampaign);
        return modelMapper.map(existingCampaign, CampaignDto.class);
    }

    @Override
    public void deleteCampaign(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new ResourceNotFoundException("Campaign with id " + id + " not found");
        }
        campaignRepository.deleteById(id);
    }

    private void handleFileUpload(MultipartFile imageFile, CampaignEntity campaignEntity) {
        String originalFileName = imageFile.getOriginalFilename();
        assert originalFileName != null;
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String uniqueFileName = campaignEntity.getCampaignId() + fileExtension;

        String uploadDir = "";

        try {
            byte[] bytes = imageFile.getBytes();
            Path path = Paths.get(uploadDir, uniqueFileName);

            Files.walk(Paths.get(uploadDir))
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().startsWith(
                            String.valueOf(campaignEntity.getCampaignId())))
                    .forEach(existingFile -> {
                        try {
                            Files.delete(existingFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            Files.write(path, bytes);
            campaignEntity.setPhotoUrl(uniqueFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
