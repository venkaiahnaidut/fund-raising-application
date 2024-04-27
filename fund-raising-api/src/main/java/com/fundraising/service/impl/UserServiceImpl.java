package com.fundraising.service.impl;

import com.fundraising.dto.DonationDto;
import com.fundraising.dto.UserDto;
import com.fundraising.entity.DonationEntity;
import com.fundraising.entity.UserEntity;
import com.fundraising.exception.ResourceNotFoundException;
import com.fundraising.repository.DonationRepository;
import com.fundraising.repository.UserRepository;
import com.fundraising.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DonationRepository donationRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<UserDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAllByOrderByCreatedAtDesc();
        return users.parallelStream()
                .map(userEntity -> modelMapper.map(userEntity, UserDto.class))
                .collect(Collectors.toList());
    }

//    @Override
//    public List<UserDto> getAllUsers(int page, int limit) {
//        if(page <= 0 ){
//            List<UserEntity> users = userRepository.findAll();
//            return users.parallelStream()
//                    .map(userEntity -> modelMapper.map(userEntity, UserDto.class))
//                    .collect(Collectors.toList());
//        }
//
//        Pageable pageable = PageRequest.of(page - 1, limit);
//        Page<UserEntity> userPage = userRepository.findAll(pageable);
//        return userPage.getContent().stream()
//                .map(user -> modelMapper.map(user, UserDto.class))
//                .collect(Collectors.toList());
//    }

    @Override
    public UserDto getUserById(Long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        UserDto user = modelMapper.map(userEntity, UserDto.class);
        user.setRole("ROLE_USER");
        return user;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity = userRepository.save(userEntity);
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        UserEntity existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
        existingUser.setFirstName(userDto.getFirstName());
        existingUser.setLastName(userDto.getLastName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setPassword(userDto.getPassword());
        userRepository.save(existingUser);
        UserDto user = modelMapper.map(existingUser, UserDto.class);
        user.setRole("ROLE_USER");
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDto findByEmail(String email) throws ResourceNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + email + " not found"));
        UserDto user = modelMapper.map(userEntity, UserDto.class);
        user.setRole("ROLE_USER");
        return user;
    }

    @Override
    public UserDto login(UserDto userDto) {
        UserEntity userEntity = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + userDto.getEmail() + " not found"));
        if (!userEntity.getPassword().equals(userDto.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }
        UserDto user = modelMapper.map(userEntity, UserDto.class);
        user.setRole("ROLE_USER");
        return user;
    }

    @Override
    public List<DonationDto> getUserDonations(Long id) {
        List<DonationEntity> donations = donationRepository.findByUserId(id);
        return donations.parallelStream()
                .map(donationEntity -> {
                    DonationDto donationDto = new DonationDto();
                    donationDto.setDonationStatus(donationEntity.getDonationStatus());
                    donationDto.setUserId(donationEntity.getUser().getUserId());
                    donationDto.setUsername(donationEntity.getUsername());
                    donationDto.setCampaignName(donationEntity.getCampaign().getTitle());
                    donationDto.setCampaignId(donationEntity.getCampaign().getCampaignId());
                    donationDto.setAmount(donationEntity.getAmount());
                    donationDto.setDonationDate(donationEntity.getDonationDate());
                    return donationDto;
                })
                .collect(Collectors.toList());
    }


}
