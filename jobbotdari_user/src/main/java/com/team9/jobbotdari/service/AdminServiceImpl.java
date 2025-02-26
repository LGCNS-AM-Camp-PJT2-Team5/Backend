package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.AddCompanyRequestDto;
import com.team9.jobbotdari.dto.response.BaseResponseDto;
import com.team9.jobbotdari.dto.response.LogListResponseDto;
import com.team9.jobbotdari.dto.response.UserListResponseDto;
import com.team9.jobbotdari.entity.Log;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.repository.LogRepository;
import com.team9.jobbotdari.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final LogRepository logRepository;

    private final JobbotariFeignClient jobbotariFeignClient;

    private static final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<UserListResponseDto> getUserList() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserListResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }
        userRepository.deleteById(userId);
    }

    @Override
    public List<LogListResponseDto> getLogs() {
        Pageable pageable = (Pageable) PageRequest.of(0, 10);
        Page<Log> logPages = logRepository.findAll(pageable);
        List<Log> logs = logPages.getContent();

        return logs.stream()
                .map(log -> LogListResponseDto.builder()
                        .userId(log.getUser() != null ? log.getUser().getId() : null)
                        .name(log.getUser() != null ? log.getUser().getName() : null)
                        .description(log.getDescription())
                        .action(log.getAction())
                        .createdAt(log.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList());
    }

    @Override
    public BaseResponseDto addCompany(AddCompanyRequestDto addCompanyRequestDto) {
       return jobbotariFeignClient.addCompany(addCompanyRequestDto);
    }
}
