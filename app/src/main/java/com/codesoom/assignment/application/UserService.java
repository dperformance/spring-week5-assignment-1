package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.request.UserRegistrationData;
import com.codesoom.assignment.dto.response.UserModificationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
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
        User user = userRepository.findById(id).get();

        // 검색된 속성값에 전달받은 속성값으로 변경한다.
        User source = mapper.map(modificationData, User.class);

        user.changeWith(source);

        return user;
    }

    public User deleteUser(long id) {
        return null;
    }
}
