package com.asyncworking.controllers;

import com.asyncworking.dtos.*;
import com.asyncworking.services.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity createProject(@Valid @RequestBody ProjectDto projectDto){
        return ResponseEntity.ok(projectService.createProjectAndProjectUser(projectDto));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity getProjectList(@PathVariable("companyId")
                                                @NotNull Long companyId) {
        return ResponseEntity.ok(projectService.fetchProjectInfoListByCompanyId(companyId));
    }

    @GetMapping("/{projectId}/project-info")
    public ResponseEntity getProjectInfo(@PathVariable("projectId") @NotNull Long projectId) {
        log.info("projectId: {}", projectId);
        ProjectInfoDto projectInfoDto = projectService.fetchProjectInfoByProjectId(projectId);
        return ResponseEntity.ok(projectInfoDto);
    }


    @PutMapping("/{projectId}/project-info")
    public ResponseEntity updateProjectProfile(@PathVariable("projectId") Long projectId,
                                               @Valid @RequestBody ProjectModificationDto projectModificationDto) {
        projectService.updateProjectInfo(projectModificationDto);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/{projectId}/members")
    public ResponseEntity getMembersByProjectId(@PathVariable Long projectId) {
        log.info("Project ID: {}", projectId);
        List<EmployeeGetDto> members = projectService.findAllMembersByProjectId(projectId);
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{projectId}/members")
    public ResponseEntity<?> createMembersByProjectIdAndUserId(@PathVariable Long projectId,
                                                               @RequestParam("userIds") @NotNull List<Long> userIds) {
       log.info("projectID: {}, userIds: {}", projectId, userIds);
       projectService.createProjectUsers(projectId, userIds);
       return ResponseEntity.ok("success");
    }
}

