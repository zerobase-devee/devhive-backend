package com.devee.devhive.domain.nonuser.service;

import static com.devee.devhive.global.exception.ErrorCode.NOT_FOUND_USER;

import com.devee.devhive.domain.user.entity.User;
import com.devee.devhive.domain.nonuser.dto.RankUserDto;
import com.devee.devhive.domain.user.entity.dto.UserInfoDto;
import com.devee.devhive.domain.user.entity.dto.UserInformationDto;
import com.devee.devhive.domain.user.repository.UserRepository;
import com.devee.devhive.domain.user.service.UserService;
import com.devee.devhive.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NonUserService {

    private final UserService userService;
    private final UserRepository userRepository;



    // 랭킹 목록 조회
    public Page<RankUserDto> getRankUsers(Pageable pageable) {
        return userRepository.findAllByOrderByRankPointDesc(pageable)
            .map(RankUserDto::from);
    }

    public UserInfoDto getUserInfo(Long userId) {
        User targetUser = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        UserInformationDto userInformation = userService.getUserInformation(targetUser);

        return UserInfoDto.of(targetUser, userInformation, false);
    }
}
