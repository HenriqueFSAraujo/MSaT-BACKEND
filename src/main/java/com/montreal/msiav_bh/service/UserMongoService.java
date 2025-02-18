package com.montreal.msiav_bh.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.montreal.oauth.domain.dto.response.UserResponse;
import com.montreal.oauth.domain.entity.UserInfo;
import com.montreal.oauth.domain.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserMongoService {

    private final UserService userService;

    public Page<UserResponse> getAllUsersWithFilters(Pageable pageable, String searchTerm) {
        return userService.getAllUsersWithFilters(pageable, null);
    }

    public UserResponse findByEmail(String email) {
        return userService.findByEmail(email);
    }

    public UserResponse getAuthenticatedUser() {
        return userService.getUser();
    }

    public UserInfo findById(Long userId) {
        return userService.findById(userId);
    }

    public UserInfo getLoggedInUser() {
    	return userService.getLoggedInUser();
    }

    public List<UserInfo> findUsersByCompanyId(String companyId) {
    	return userService.findUsersByCompanyId(companyId);
    }
    
}
