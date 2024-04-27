package com.fundraising.service;

import com.fundraising.dto.DonationDto;
import com.fundraising.dto.UserDto;
import com.fundraising.exception.ResourceNotFoundException;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
    UserDto findByEmail(String email) throws ResourceNotFoundException;
    UserDto login(UserDto userDto);
    List<DonationDto> getUserDonations(Long id);
}
