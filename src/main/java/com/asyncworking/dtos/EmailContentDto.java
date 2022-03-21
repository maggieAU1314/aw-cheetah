package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailContentDto {
    private String email;
    private String userName;
    private String companyName;
    private String companyOwnerName;
    private String verificationLink;
    private String templateType;
}
