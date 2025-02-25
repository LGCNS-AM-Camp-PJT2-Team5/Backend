package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.AddCompanyRequestDto;
import com.team9.jobbotdari.dto.response.BaseResponseDto;
import com.team9.jobbotdari.dto.response.LogListResponseDto;
import com.team9.jobbotdari.dto.response.UserListResponseDto;

import java.util.List;

public interface AdminService {
    List<UserListResponseDto> getUserList();

    void deleteUserById(Long userId);

    List<LogListResponseDto> getLogs();

    BaseResponseDto addCompany(AddCompanyRequestDto addCompanyRequestDto);
}
