package com.asyncworking.repositories;

import com.asyncworking.models.Employee;
import com.asyncworking.models.EmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface EmployeeRepository extends JpaRepository<Employee, EmployeeId> {
}
