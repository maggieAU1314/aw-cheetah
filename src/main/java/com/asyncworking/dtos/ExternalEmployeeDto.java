package com.asyncworking.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalEmployeeDto {

    public Long companyId;

    public String email;

    public String name;

    public String title;

    public String companyName;
}
