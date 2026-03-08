package com.secondscore.modules.base.controller;

import com.secondscore.common.api.ApiResponse;
import com.secondscore.modules.base.dto.CategorySaveRequest;
import com.secondscore.modules.base.dto.ClassSaveRequest;
import com.secondscore.modules.base.dto.CollegeSaveRequest;
import com.secondscore.modules.base.dto.MajorSaveRequest;
import com.secondscore.modules.base.dto.TermSaveRequest;
import com.secondscore.modules.base.entity.BaseActivityCategory;
import com.secondscore.modules.base.entity.BaseClassInfo;
import com.secondscore.modules.base.entity.BaseCollege;
import com.secondscore.modules.base.entity.BaseMajor;
import com.secondscore.modules.base.entity.BaseTerm;
import com.secondscore.modules.base.service.BaseInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/base")
@RequiredArgsConstructor
public class BaseInfoController {

    private final BaseInfoService baseInfoService;

    @GetMapping("/colleges")
    public ApiResponse<List<BaseCollege>> listColleges() {
        return ApiResponse.success(baseInfoService.listColleges());
    }

    @PostMapping("/colleges")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Long> createCollege(@Valid @RequestBody CollegeSaveRequest request) {
        return ApiResponse.success(baseInfoService.createCollege(request));
    }

    @PutMapping("/colleges/{id}")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Void> updateCollege(@PathVariable Long id, @Valid @RequestBody CollegeSaveRequest request) {
        baseInfoService.updateCollege(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/majors")
    public ApiResponse<List<BaseMajor>> listMajors(@RequestParam(required = false) Long collegeId) {
        return ApiResponse.success(baseInfoService.listMajors(collegeId));
    }

    @PostMapping("/majors")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Long> createMajor(@Valid @RequestBody MajorSaveRequest request) {
        return ApiResponse.success(baseInfoService.createMajor(request));
    }

    @PutMapping("/majors/{id}")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Void> updateMajor(@PathVariable Long id, @Valid @RequestBody MajorSaveRequest request) {
        baseInfoService.updateMajor(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/classes")
    public ApiResponse<List<BaseClassInfo>> listClasses(@RequestParam(required = false) Long majorId) {
        return ApiResponse.success(baseInfoService.listClasses(majorId));
    }

    @PostMapping("/classes")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Long> createClass(@Valid @RequestBody ClassSaveRequest request) {
        return ApiResponse.success(baseInfoService.createClass(request));
    }

    @PutMapping("/classes/{id}")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Void> updateClass(@PathVariable Long id, @Valid @RequestBody ClassSaveRequest request) {
        baseInfoService.updateClass(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/terms")
    public ApiResponse<List<BaseTerm>> listTerms() {
        return ApiResponse.success(baseInfoService.listTerms());
    }

    @PostMapping("/terms")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Long> createTerm(@Valid @RequestBody TermSaveRequest request) {
        return ApiResponse.success(baseInfoService.createTerm(request));
    }

    @PutMapping("/terms/{id}")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Void> updateTerm(@PathVariable Long id, @Valid @RequestBody TermSaveRequest request) {
        baseInfoService.updateTerm(id, request);
        return ApiResponse.success();
    }

    @GetMapping("/activity-categories")
    public ApiResponse<List<BaseActivityCategory>> listCategories() {
        return ApiResponse.success(baseInfoService.listCategories());
    }

    @PostMapping("/activity-categories")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Long> createCategory(@Valid @RequestBody CategorySaveRequest request) {
        return ApiResponse.success(baseInfoService.createCategory(request));
    }

    @PutMapping("/activity-categories/{id}")
    @PreAuthorize("hasAuthority('base:manage')")
    public ApiResponse<Void> updateCategory(@PathVariable Long id, @Valid @RequestBody CategorySaveRequest request) {
        baseInfoService.updateCategory(id, request);
        return ApiResponse.success();
    }
}
