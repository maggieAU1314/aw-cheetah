package com.asyncworking.services;

import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Status;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import com.asyncworking.utility.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Autowired
    private Mapper mapper;

    private UserService userService;

    @BeforeEach()
    void setup() {
        userService = new UserService(userRepository, authenticationManager, mapper);
        ReflectionTestUtils.setField(userService, "jwtSecret", "securesecuresecuresecuresecuresecuresecure");
    }

    @Test
    public void shouldFindEmailExistSuccessful() {
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .email("a@qq.com")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder().email("a@gmail.com").build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockReturnedUserEntity));

        String email = userInfoDto.getEmail();
        assertTrue(userService.ifEmailExists(email));
    }

    @Test
    public void shouldLoginSuccessfulAndReturnDto() {
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .email("plus@gmail.com")
                .name("aName")
                .password("password")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("plus@gmail.com")
                .name("aName")
                .build();

        when(userRepository.findUserEntityByEmail(any())).thenReturn(Optional.of(mockReturnedUserEntity));

        UserInfoDto returnedUserInfoDto = userService.login(userInfoDto.getEmail(), userInfoDto.getPassword());
        String testName = returnedUserInfoDto.getName();

        assertEquals(testName, mockReturnedUserEntity.getName());
    }

    @Test
    public void shouldGenerateActivationLinkGivenUserDtoAndHttpServletRequest() {
        UserInfoDto userPostDto = UserInfoDto.builder()
                .email("user@gmail.com")
                .password("len123")
                .name("user")
                .build();
        String siteUrl = "http://localhost";
        String verifyLink = userService.generateVerifyLink(userPostDto, siteUrl);

        assertEquals(
                "http://localhost/verify?code="
                        .concat("eyJhbGciOiJIUzI1NiJ9.")
                        .concat("eyJzdWIiOiJzaWduVXAiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIn0.")
                        .concat("tC8BAIWlF8U5z5Ue-SPBZBxMUBqLwGeKbbLVCtMTmhw"),
                verifyLink
        );
    }

    @Test
    public void shouldCreateUserAndGenerateActivationLinkGivenProperUserDto() {
        UserInfoDto userPostDto = UserInfoDto.builder()
                .email("user@gmail.com")
                .password("len123")
                .name("user")
                .build();

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);

        userService.createUserAndGenerateVerifyLink(userPostDto, "http://localhost");

        verify(userRepository).save(captor.capture());
        UserEntity savedUser = captor.getValue();
        assertEquals("user@gmail.com", savedUser.getEmail());
        assertEquals("user", savedUser.getName());
    }

    @Test
    public void shouldDecodeEmailAndActiveUserStatus() throws Exception {
        String code = "eyJhbGciOiJIUzI1NiJ9."
                .concat("eyJzdWIiOiJzaWduVXAiLCJlbWFpbCI6InVzZXJAZ21haWwuY29tIn0.")
                .concat("tC8BAIWlF8U5z5Ue-SPBZBxMUBqLwGeKbbLVCtMTmhw");

        when(userRepository.updateStatusByEmail("user@gmail.com", Status.ACTIVATED)).thenReturn(1);

        userService.verifyAccountAndActiveUser(code);

        verify(userRepository).updateStatusByEmail("user@gmail.com", Status.ACTIVATED);
    }

    @Test
    public void throwExceptionWhenEmailNotExist() {
        String expectedMessage = "No such user!";

        UserInfoDto userPostInfoDto = UserInfoDto.builder()
                .email("lengary@asyncworking.com")
                .company("AW")
                .title("VI")
                .build();

        when(userRepository.findByEmail(userPostInfoDto.getEmail()))
                .thenThrow(new RuntimeException(expectedMessage));

        Exception exception = assertThrows(RuntimeException.class,
                () -> userService.ifEmailExists(userPostInfoDto.getEmail()));

        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void shouldFindEmploymentExistSuccessful() {
        String email = "a@gmail.com";
        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("a@gmail.com").build();
        when(userRepository.findEmploymentByEmail(anyString())).thenReturn(Optional.of(mockReturnedUserEntity));
        boolean testEmail = userService.ifCompanyExits(email);
        assertTrue(testEmail);
    }
}
