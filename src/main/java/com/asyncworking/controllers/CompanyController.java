package com.asyncworking.controllers;

import com.asyncworking.dtos.CompanyModificationDto;
import com.asyncworking.services.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
@Validated
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/company")
    public ResponseEntity<?> companyCreate(@Valid @RequestBody CompanyModificationDto CompanyModificationDto) {
        companyService.createCompanyAndEmployee(CompanyModificationDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/company/profile")
    public ResponseEntity<CompanyModificationDto> prefillDescription(@RequestParam("companyId") @NotNull Long companyId) {
        return ResponseEntity.ok(companyService.fetchCompanyProfileById(companyId));
    }

    @PutMapping("/company/profile")
    public ResponseEntity<?> updateCompanyDescription(@Valid @RequestBody CompanyModificationDto CompanyModificationDto) {
        companyService.updateCompany(CompanyModificationDto);
        return ResponseEntity.ok("success");
    }
}
