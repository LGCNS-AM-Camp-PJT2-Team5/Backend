package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.AddInterestRequest;
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

@Service
@RequiredArgsConstructor
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;

    public void addUserInterest(AddInterestRequest addInterestRequest) {
        User user = getCurrentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }

        boolean exists = userInterestRepository.existsByUserAndCompanyId(user, addInterestRequest.getCompanyId());
        if (exists) {
            throw new DuplicateUserInterestException();
        }
        UserInterest userInterest = UserInterest.builder()
                .user(user)
                .companyId(addInterestRequest.getCompanyId())
                .build();
        userInterestRepository.save(userInterest);
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
