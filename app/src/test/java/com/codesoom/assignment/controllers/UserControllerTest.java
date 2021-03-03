package com.codesoom.assignment.controllers;

import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.request.UserModificationData;
import com.codesoom.assignment.dto.request.UserRegistrationData;
import com.codesoom.assignment.errors.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        given(userService.registerUser(any(UserRegistrationData.class)))
                .will(invocation -> {
                    UserRegistrationData registrationData = invocation.getArgument(0);
                    return User.builder()
                            .id(13L)
                            .email(registrationData.getEmail())
                            .name(registrationData.getName())
                            .build();
        });

        given(userService.updateUser(eq(1L), any(UserModificationData.class)))
                .will(invocation -> {
                    Long id = invocation.getArgument(0);
                    UserModificationData modificationData =
                            invocation.getArgument(1);
                    return User.builder()
                            .id(id)
                            .email("test@gmail.com")
                            .name(modificationData.getName())
                            .build();
                });
//                .willReturn(User.builder()
//                        .id(1L)
//                        .email("test@gmail.com")
//                        .name("TEST")
//                        .build());

        // 존재하지 않는 id 수정 요청시 NOT_FOUND(404) 발생
        given(userService.updateUser(eq(100L), any(UserModificationData.class)))
                .willThrow(new UserNotFoundException(100L));

        given(userService.deleteUser(100L))
                .willThrow(new UserNotFoundException(100L));
    }

    @Test
    @DisplayName("사용자를 추가")
    void registerUserWithValidAttribute() throws Exception {
//        User user = User.builder()
//                .email("admin@gmail.com").build();

        // 방법 1 : 위에서 만든것을 willReturn으로 보내주는 방법
//        given(userService.registerUser(any(UserData.class))).willReturn(user);

        // 방법 2 : 받은것을 가지고 will(invocation() -> {}) 보내주는 바업
//        given(userService.registerUser(any(UserRegistrationData.class)))
//                .will(invocation -> {
//                    UserRegistrationData userData = invocation.getArgument(0);
//                    User user = User.builder()
//                                    .email(userData.getEmail())
//                                    .build();
//                    return user;
//        });

        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"test@gmail.com\", " +
                                "\"name\":\"Tester\", \"password\":\"test\"}")
        )
        .andExpect(status().isCreated())
        .andExpect(content().string(
                containsString("\"id\":13")
        ))
        .andExpect(content().string(
                containsString("\"email\":\"test@gmail.com\"")
        ))
        .andExpect(content().string(
                containsString("\"name\":\"Tester\"")
        ));

        verify(userService).registerUser(any(UserRegistrationData.class));
    }

    @Test
    @DisplayName("비어있는 값으로 가입 요청시 400 Error")
    void registerUserWithInvalidAttribute() throws Exception {
        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
        )
                .andExpect(status().isBadRequest()); // HttpStatus.BAD_REQUEST == 400
    }

    @Test
    @DisplayName("회원정보 수정")
    void updateUserWithValidAttribute() throws Exception {
        mvc.perform(
                patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"TEST\", \"password\":\"test\"}")
        )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("\"id\":1")
                ))
                .andExpect(content().string(
                        containsString("\"name\":\"TEST\"")
                ));

        verify(userService).updateUser(eq(1L), any(UserModificationData.class));
    }



    // 에러 #2 - 속성 오류 (name 값이 틀린경우)
    @Test
    @DisplayName("비어있는 값으로 수정 요청시 400 Error") // BAD_REQUEST == 400
    void updateUserWithInvalidAttribute() throws Exception{
        mvc.perform(
                patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\", \"password\":\"\"}")
        )
                .andExpect(status().isBadRequest());
    }

    // 에러 #1 - ID 없음
    @Test
    @DisplayName("존재하지 않는 id로 수정 요청시 404 Error") // NOT_FOUND == 404
    void updateUserWithNotExistedId() throws Exception {
        mvc.perform(
                patch("/users/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Tester\", \"password\":\"test\"}")
        )
                .andExpect(status().isNotFound());

        verify(userService)
                .updateUser(eq(100L), any(UserModificationData.class));
    }

    @Test
    @DisplayName("존재하는 사용자 삭제")
    void destroyWithExistedId() throws Exception {
        mvc.perform(
                delete("/users/1"))
                .andExpect(status().isNoContent());

        // UserController -> destory -> userService.deleteUser(id)가 없으면 error발생
        verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제 요청시 404(NOT_FOUND) error") // NOT_FOUND == 404
    void destroyWithNotExistedId() throws Exception {
        mvc.perform(delete("/users/100"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(100L);
    }
}
