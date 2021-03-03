package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.request.UserRegistrationData;
import com.codesoom.assignment.dto.request.UserModificationData;
import com.codesoom.assignment.errors.UserEmailDuplicationException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UserServiceTest {
    private static final String EXISTED_EMAIL_ADDRESS = "existed@gmail.com";
    private static final Long DELETED_USER_ID = 200L;

    private UserService userService;
    private final UserRepository userRepository = mock(UserRepository.class);

    @BeforeEach
    void setUp() {
        Mapper mapper = DozerBeanMapperBuilder.buildDefault();
        userService = new UserService(mapper, userRepository);

        // 입력 받은 값을 가지고 return 시키고 싶을때는 will을 사용한다.
        given(userRepository.save(any(User.class))).will(invocation -> {
            User user = invocation.getArgument(0);
            return user;
        });

        given(userRepository.existsByEmail(EXISTED_EMAIL_ADDRESS))
                .willReturn(true);

        given(userRepository.findByIdAndDeletedIsFalse(1L))
                .willReturn(Optional.of(
                        User.builder()
                            .id(1L)
                            .email(EXISTED_EMAIL_ADDRESS)
                            .name("Tester")
                            .password("TEST")
                            .build()));

        // TODO : 잘못했음..
        // 없는 사용자를 요청할 때는 비어있는 값을 리턴해주어야 한다.
        given(userRepository.findById(100L)).willReturn(Optional.empty());

        // userRepository.findByIdAndDeletedIsFalse(id)를 추가하면서 아래처럼 안주어도 된다.
//        given(userRepository.findById(DELETED_USER_ID)).willReturn(Optional.of(
//                User.builder()
//                .id(DELETED_USER_ID)
//                .deleted(true)
//                .build()));

        given(userRepository.findByIdAndDeletedIsFalse(DELETED_USER_ID))
                .willReturn(Optional.empty());

    }

    @Test
    void registerUser() {
        UserRegistrationData registrationData = UserRegistrationData.builder()
                .email("test@gmail.com")
                .name("Tester")
                .password("test")
                .build();

        User user = userService.registerUser(registrationData);

//        assertThat(user.getId()).isEqualTo(13L);
        assertThat(user.getEmail()).isEqualTo("test@gmail.com");
        assertThat(user.getName()).isEqualTo("Tester");
        assertThat(user.getPassword()).isEqualTo("test");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복된 가입자라면 UserEmailDuplicationException 400 error를 반환") // BAD_REQUEST == 400
    void registerUserWithDuplicatedEmail() {
        UserRegistrationData userRegistrationData = UserRegistrationData.builder()
                .email(EXISTED_EMAIL_ADDRESS)
                .name("Tester")
                .password("test")
                .build();

//        userService.registerUser(userRegistrationData);

        // 중복 email exception을 발생시키기 위해 email duplication Exception class를 생성해준다.
        assertThatThrownBy(() -> userService.registerUser(userRegistrationData))
                .isInstanceOf(UserEmailDuplicationException.class);

        verify(userRepository).existsByEmail(EXISTED_EMAIL_ADDRESS);
    }

    @Test
    @DisplayName("사용자 정보를 수정합니다.")
    void updateUserWithExistedId() {
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .password("test")
                .build();

        User user = userService.updateUser(1L, modificationData);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo(EXISTED_EMAIL_ADDRESS);
        assertThat(user.getName()).isEqualTo("TEST");

        verify(userRepository).findByIdAndDeletedIsFalse(1L);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 정보 수정을 요청시 400 error") // BAD_REQUEST == 400
    void updateUserWithNotExistedId() {
        // 1. client modify Data
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .password("test")
                .build();

        //2. Not Existed Id와 변경 속성값을 service에게 요청
        // No value present == 값이 없습니다.
//        userService.updateUser(1000L, modificationData);

        // 망했다.
        assertThatThrownBy(() -> userService.updateUser(100L, modificationData))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(100L);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 정보 수정을 요청시 400 error") // BAD_REQUEST == 400
    void updateUserWithNotDeletedId() {
        // 1. client modify Data
        UserModificationData modificationData = UserModificationData.builder()
                .name("TEST")
                .password("test")
                .build();


        // 망했다.
        assertThatThrownBy(
                () -> userService.updateUser(DELETED_USER_ID, modificationData)
        )
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }

    @Test
    @DisplayName("사용자를 삭제 합니다.")
    void deleteUserWithExistedId() {
        User user = userService.deleteUser(1L);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제요청시 404(NOT_FOUND) error ")
    void deleteUserWithNotExistedId() {
        assertThatThrownBy(() -> userService.deleteUser(100L))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(100L);
    }

    @Test
    @DisplayName("이미 삭제된 사용자라도 404 error")
    void deleteUserWithDeletedId() {
        assertThatThrownBy(() -> userService.deleteUser(DELETED_USER_ID))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository).findByIdAndDeletedIsFalse(DELETED_USER_ID);
    }
}
