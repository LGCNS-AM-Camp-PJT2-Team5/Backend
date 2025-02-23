package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.InterestRequestDto;
import com.team9.jobbotdari.dto.response.UserInterestResponseDto;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.UserInterest;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.exception.userinterest.DuplicateUserInterestException;
import com.team9.jobbotdari.repository.UserInterestRepository;
import com.team9.jobbotdari.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;

    public UserInterestResponseDto getUserInterests() {
        User user = getCurrentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        List<UserInterest> interests = userInterestRepository.findByUser(user);
        List<Long> companyIds = interests.stream().map(UserInterest::getCompanyId).toList();
        UserInterestResponseDto userInterestResponseDto = new UserInterestResponseDto();
        userInterestResponseDto.setUserId(user.getId());
        userInterestResponseDto.setCompanyIds(companyIds);
        return userInterestResponseDto;
    }

    public void addUserInterest(InterestRequestDto interestRequestDto) {
        User user = getCurrentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        boolean exists = userInterestRepository.existsByUserAndCompanyId(user, interestRequestDto.getCompanyId());
        if (exists) {
            throw new DuplicateUserInterestException();
        }
        UserInterest userInterest = UserInterest.builder()
                .user(user)
                .companyId(interestRequestDto.getCompanyId())
                .build();
        userInterestRepository.save(userInterest);
    }

    public void deleteUserInterest(Long companyId) {
        User user = getCurrentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }
        userInterestRepository.deleteByUserAndCompanyId(user, companyId);
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            return customUserDetails.getUser();
        }
        return null;
    }
}
