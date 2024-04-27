package com.fundraising.service.impl;

import com.fundraising.dto.AdminDto;
import com.fundraising.entity.AdminEntity;
import com.fundraising.exception.AuthenticationException;
import com.fundraising.exception.ResourceNotFoundException;
import com.fundraising.repository.AdminRepository;
import com.fundraising.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<AdminDto> getAllAdmins() {
        List<AdminEntity> admins = adminRepository.findAll();
        return admins.stream()
                .map(adminEntity -> modelMapper.map(adminEntity, AdminDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AdminDto getAdminById(Long id) {
        AdminEntity adminEntity = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with id " + id + " not found"));
        return modelMapper.map(adminEntity, AdminDto.class);
    }

    @Override
    public AdminDto createAdmin(AdminDto adminDto) {
        AdminEntity adminEntity = modelMapper.map(adminDto, AdminEntity.class);
        adminEntity = adminRepository.save(adminEntity);
        return modelMapper.map(adminEntity, AdminDto.class);
    }

    @Override
    public AdminDto updateAdmin(Long id, AdminDto adminDto) {
        AdminEntity existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with id " + id + " not found"));
        existingAdmin.setFirstName(adminDto.getFirstName());
        existingAdmin.setLastName(adminDto.getLastName());
        existingAdmin.setEmail(adminDto.getEmail());
        existingAdmin.setPassword(adminDto.getPassword());
        adminRepository.save(existingAdmin);
        return modelMapper.map(existingAdmin, AdminDto.class);
    }

    @Override
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new ResourceNotFoundException("Admin with id " + id + " not found");
        }
        adminRepository.deleteById(id);
    }

    @Override
    public AdminDto findByEmail(String email) throws ResourceNotFoundException {
        AdminEntity adminEntity = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin with email " + email + " not found"));
        return modelMapper.map(adminEntity, AdminDto.class);
    }

    @Override
    public AdminDto login(AdminDto adminDto) throws ResourceNotFoundException {
        AdminEntity adminEntity = adminRepository.findByEmail(adminDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with email: " + adminDto.getEmail()));

        if (adminDto.getPassword().equals(adminEntity.getPassword())) {
            return modelMapper.map(adminEntity, AdminDto.class);
        } else {
            throw new AuthenticationException("Invalid credentials");
        }
    }

}
