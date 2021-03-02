// ToDo
// 1. 가입 -> POST /users => 가입정보 (DTO) -> email이 unique key! => 완료
// 2. 목록, 상세보기 -> ADMIN!
// 3. 사용자 정보 갱신 -> PUT/PATCH /users/{id} => 정보 갱신 (DTO) -> 이름만수정 ==> 완료
// ==> 권한 확인. Authorization -->
// 4. 탈퇴 -> DELETE /users/{id} => soft delete

package com.codesoom.assignment.controllers;


import com.codesoom.assignment.application.UserService;
import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.dto.request.UserRegistrationData;
import com.codesoom.assignment.dto.response.UserModificationData;
import com.codesoom.assignment.dto.response.UserResultData;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    public UserController (UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResultData create(@RequestBody @Valid UserRegistrationData userRegistrationData) {
        User user = userService.registerUser(userRegistrationData);

        return getUserResultData(user);
    }

    @PatchMapping("{id}")
    public UserResultData update(
            @PathVariable Long id,
            @RequestBody @Valid UserModificationData modificationData
    ) {
        User user = userService.updateUser(id, modificationData);

        return getUserResultData(user);

    }

    private UserResultData getUserResultData(User user) {
        if (user == null) {
            return null;
        }

        return UserResultData.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
