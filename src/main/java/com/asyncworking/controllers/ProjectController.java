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
    public ResponseEntity updateProjectProfile(@Valid @PathVariable("projectId") Long projectId,
                                                      @RequestBody ProjectModificationDto projectModificationDto) {
        projectService.updateProjectInfo(projectModificationDto);
        return ResponseEntity.ok("success");
    }
}

