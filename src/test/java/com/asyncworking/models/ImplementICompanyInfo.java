package com.asyncworking.models;

import lombok.*;
import org.hibernate.annotations.Type;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "company")
public class ImplementICompanyInfo implements ICompanyInfo {
    @Id
    @Type(type = "long")
    private Long id;
    private String name;
    private String description;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
