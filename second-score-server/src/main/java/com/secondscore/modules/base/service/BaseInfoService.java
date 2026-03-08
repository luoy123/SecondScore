package com.secondscore.modules.base.service;

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

import java.util.List;

public interface BaseInfoService {
    List<BaseCollege> listColleges();

    Long createCollege(CollegeSaveRequest request);

    void updateCollege(Long id, CollegeSaveRequest request);

    List<BaseMajor> listMajors(Long collegeId);

    Long createMajor(MajorSaveRequest request);

    void updateMajor(Long id, MajorSaveRequest request);

    List<BaseClassInfo> listClasses(Long majorId);

    Long createClass(ClassSaveRequest request);

    void updateClass(Long id, ClassSaveRequest request);

    List<BaseTerm> listTerms();

    Long createTerm(TermSaveRequest request);

    void updateTerm(Long id, TermSaveRequest request);

    List<BaseActivityCategory> listCategories();

    Long createCategory(CategorySaveRequest request);

    void updateCategory(Long id, CategorySaveRequest request);
}

