package com.fundraising.service;

import com.fundraising.dto.AdminDto;
import com.fundraising.exception.ResourceNotFoundException;

import java.util.List;

public interface AdminService {
    List<AdminDto> getAllAdmins();
    AdminDto getAdminById(Long id);
    AdminDto createAdmin(AdminDto adminDto);
    AdminDto updateAdmin(Long id, AdminDto adminDto);
    void deleteAdmin(Long id);
    AdminDto findByEmail(String email) throws ResourceNotFoundException;

    AdminDto login(AdminDto adminDto);
}
