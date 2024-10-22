package com.weatherclaus.be.user.controller;

import com.weatherclaus.be.common.ResponseDto;
import com.weatherclaus.be.user.dto.request.CurrentPasswordRequest;
import com.weatherclaus.be.user.dto.request.UpdatePasswordRequest;
import com.weatherclaus.be.user.dto.request.UpdateUserRequest;
import com.weatherclaus.be.user.dto.response.UserInfoResponse;
import com.weatherclaus.be.user.dto.validation.UpdatePasswordRequestValidator;
import com.weatherclaus.be.user.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    private final UpdatePasswordRequestValidator updatePasswordRequestValidator;

    @InitBinder("updatePasswordDTO")
    protected void initBinderForUpdatePassword(WebDataBinder binder) {
        binder.addValidators(updatePasswordRequestValidator);
    }



    //    정보 보여주기
//  username ( login할 때 쓰는ID )
//    email
//    imageUrl
//    보류 - nickname ( 회원가입 추가 )
    @GetMapping("/myPage")
    public ResponseEntity<ResponseDto<?>> myPage() {


        UserInfoResponse userInfoDTO = profileService.getUserInfo();

        return new ResponseEntity<>(
                new ResponseDto<>("success", "user info success", userInfoDTO, null, 200),
                HttpStatus.OK);
    }


    //회원 수정 정보 저장 요청 url 응답 ( 나중에 닉네임, 프로필사진 )
//    	1.	@ModelAttribute를 사용해서 DTO로 받는 경우:
//            •	파일과 다른 데이터를 함께 DTO로 매핑하고 싶을 때 사용합니다.
    //	•	이 방식에서는 파일뿐만 아니라 여러 필드를 묶어서 DTO로 처리할 수 있습니다.
//	•	이때 요청의 Content-Type은 반드시 **multipart/form-data**여야 합니다.
//    변수명  file
    @PatchMapping()
    public ResponseEntity<ResponseDto<?>> updateUserInfo(@Valid @ModelAttribute UpdateUserRequest updateUserDTO) throws IOException {

        profileService.updateUserInfo(updateUserDTO);


        return new ResponseEntity<>(
                new ResponseDto<>("success", "user info success", null, null, 200),
                HttpStatus.OK);
    }


//    우측 상단 바 표시 (요청,응답)
//    request
//    토큰검증은 다해주고 재발급은 http코드에 따라 다시 요청
    /// api/users/profile 유저사진데이터를 받아오는거
//    응답 s3 url
    @GetMapping("/image")
    public ResponseEntity<ResponseDto<?>> profileUrl() {

        UserInfoResponse userInfo = profileService.getUserInfo();

        String imageUrl = userInfo.getImageUrl();

        return new ResponseEntity<>(
                new ResponseDto<>("success", "user info success", imageUrl, null, 200),
                HttpStatus.OK);
    }


//    - 비밀번호 변경하기(회원가입 때 쓴 api) 현재 비밀번호 확인(”/api/users/password”) post password(json)→ 200,400
//            → 새로운 비번 (”/api/users/newpassword”) post password,password2(json) → 200,400
//           - pw변경 버튼 → 현재 pw 확인page → 새로운 pw 입력 page → navigate(’/myPage’)

    @PostMapping("/password")
    public ResponseEntity<ResponseDto<?>> changePassword(@Valid @RequestBody CurrentPasswordRequest currentPasswordDTO) {


        profileService.isCurrentPassword(currentPasswordDTO);


        return new ResponseEntity<>(
                new ResponseDto<>("success", "user Current check password correct", null, null, 200),
                HttpStatus.OK);
    }

    @PutMapping("/password")
    public ResponseEntity<ResponseDto<?>> newPassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordDTO) {

        profileService.updatePassword(updatePasswordDTO);


        return new ResponseEntity<>(
                new ResponseDto<>("success", "user Change new password correct", null, null, 200),
                HttpStatus.OK);
    }


    //    탈퇴하기(”/api/users/remove) post password (json) 200,400
    //      계정삭제  (요청,응답) → active 컬럼 추가.
    //    삭제 요청 시 비밀번호 입력 받아서 체크하기.
    @Operation(summary = "Protected API", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping()
    public ResponseEntity<ResponseDto<?>> remove() {

        profileService.deleteUser();

        return new ResponseEntity<>(
                new ResponseDto<>("success", "delete complete ", null, null, 200),
                HttpStatus.OK);
    }
}
