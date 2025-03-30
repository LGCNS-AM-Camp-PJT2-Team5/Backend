package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.InterestRequestDto;
import com.team9.jobbotdari.dto.response.UserInterestResponseDto;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.UserInterest;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.exception.userinterest.DuplicateUserInterestException;
import com.team9.jobbotdari.repository.UserInterestRepository;
import com.team9.jobbotdari.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInterestService {

    private final UserInterestRepository userInterestRepository;
    private final UserRepository userRepository;

    public UserInterestResponseDto getUserInterests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<UserInterest> interests = userInterestRepository.findByUser(user);
        List<Long> companyIds = interests.stream().map(UserInterest::getCompanyId).toList();

        UserInterestResponseDto userInterestResponseDto = new UserInterestResponseDto();
        userInterestResponseDto.setUserId(user.getId());
        userInterestResponseDto.setCompanyIds(companyIds);

        return userInterestResponseDto;
    }

    public void addUserInterest(Long userId, InterestRequestDto interestRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

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

    @Transactional
    public void deleteUserInterest(Long userId, Long companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userInterestRepository.deleteByUserAndCompanyId(user, companyId);
    }
}
