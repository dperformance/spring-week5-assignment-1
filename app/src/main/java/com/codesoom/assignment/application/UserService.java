package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.request.UserRegistrationData;
import com.codesoom.assignment.dto.request.UserModificationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.Mapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserService {

    private final Mapper mapper;
    private final UserRepository userRepository;

    public UserService(Mapper dozerMapper,
                       UserRepository userRepository) {
        this.mapper = dozerMapper;
        this.userRepository = userRepository;
    }

    public User registerUser(UserRegistrationData userRegistrationData) {
//        User user = User.builder()
//                .email(userRegistrationData.getEmail())
//                .name(userRegistrationData.getName())
//                .password(userRegistrationData.getPassword())
//                .build();
        String email = userRegistrationData.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new UserEmailDuplicationException(email);
        }

        User user = mapper.map(userRegistrationData, User.class);
        return  userRepository.save(user);
    }

    public User updateUser(Long id, UserModificationData modificationData) {
        // 전달받은 id로 속성값을 찾아온다.
        // 하지만 비어있으면 NotFound Exception을 준다.
        User user = findUser(id);

        // 검색된 속성값에 전달받은 속성값으로 변경한다.
        User source = mapper.map(modificationData, User.class);

        user.changeWith(source);

        return user;
    }

    public User deleteUser(long id) {
        // 1. 전달 받은 ID가 존재하는 사용자인지 찾아온다.
        // 존재 하지 않는 ID일 경우 404 Error발생.
        User user = findUser(id);

        user.destroy();

        return user;
    }

    private User findUser(Long id) {
        // findByIdAndDeletedIsFalse(id)를 통해
        // 아래처럼 줄일 수 있다.
        return userRepository.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new UserNotFoundException(id));

//        User user = userRepository.findByIdAndDeletedIsFalse(id)
//                .orElseThrow(() -> new UserNotFoundException(id));
//
//        if (user.isDeleted()) {
//            throw new UserNotFoundException(id);
//        }
//        return user;
    }
}
