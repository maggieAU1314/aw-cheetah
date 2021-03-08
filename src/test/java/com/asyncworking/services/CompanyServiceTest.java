package com.asyncworking.services;

import com.asyncworking.AwCheetahApplication;
import com.asyncworking.dtos.UserInfoDto;
import com.asyncworking.models.Company;
import com.asyncworking.models.Employee;
import com.asyncworking.models.UserEntity;
import com.asyncworking.repositories.CompanyRepository;
import com.asyncworking.repositories.EmployeeRepository;
import com.asyncworking.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AwCheetahApplication.class)
@AutoConfigureMockMvc
public class CompanyServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    CompanyService companyService;

    @Test
    @Transactional
    public void createCompanyAndEmployeeGivenProperUserInfoDto() {
        UserInfoDto userPostInfoDto = UserInfoDto.builder()
                .email("lengary@asyncworking.com")
                .company("AW")
                .title("VI")
                .build();

        UserEntity mockReturnedUserEntity = UserEntity.builder()
                .email("lengary@asyncworking.com")
                .name("ven").build();

        when(userRepository.findUserEntityByEmail(userPostInfoDto.getEmail()))
                .thenReturn(Optional.of(mockReturnedUserEntity));

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);
        ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
        companyService.createCompanyAndEmployee(userPostInfoDto);
        verify(companyRepository).save(companyCaptor.capture());
        verify(employeeRepository).save(employeeCaptor.capture());
        Employee savedEmployee = employeeCaptor.getValue();
        Company savedCompany = companyCaptor.getValue();

        assertEquals("VI", savedEmployee.getTitle());
        assertEquals(mockReturnedUserEntity.getId(), savedCompany.getAdminId());
    }
}
