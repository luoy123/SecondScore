package com.secondscore.modules.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.secondscore.common.exception.BusinessException;
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
import com.secondscore.modules.base.mapper.BaseActivityCategoryMapper;
import com.secondscore.modules.base.mapper.BaseClassInfoMapper;
import com.secondscore.modules.base.mapper.BaseCollegeMapper;
import com.secondscore.modules.base.mapper.BaseMajorMapper;
import com.secondscore.modules.base.mapper.BaseTermMapper;
import com.secondscore.modules.base.service.BaseInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseInfoServiceImpl implements BaseInfoService {

    private final BaseCollegeMapper baseCollegeMapper;
    private final BaseMajorMapper baseMajorMapper;
    private final BaseClassInfoMapper baseClassInfoMapper;
    private final BaseTermMapper baseTermMapper;
    private final BaseActivityCategoryMapper baseActivityCategoryMapper;

    @Override
    public List<BaseCollege> listColleges() {
        return baseCollegeMapper.selectList(new LambdaQueryWrapper<BaseCollege>()
                .orderByAsc(BaseCollege::getId));
    }

    @Override
    public Long createCollege(CollegeSaveRequest request) {
        BaseCollege college = new BaseCollege();
        college.setCollegeCode(request.getCollegeCode());
        college.setCollegeName(request.getCollegeName());
        college.setStatus(request.getStatus());
        baseCollegeMapper.insert(college);
        return college.getId();
    }

    @Override
    public void updateCollege(Long id, CollegeSaveRequest request) {
        requireCollege(id);
        BaseCollege update = new BaseCollege();
        update.setId(id);
        update.setCollegeCode(request.getCollegeCode());
        update.setCollegeName(request.getCollegeName());
        update.setStatus(request.getStatus());
        baseCollegeMapper.updateById(update);
    }

    @Override
    public List<BaseMajor> listMajors(Long collegeId) {
        LambdaQueryWrapper<BaseMajor> wrapper = new LambdaQueryWrapper<>();
        if (collegeId != null) {
            wrapper.eq(BaseMajor::getCollegeId, collegeId);
        }
        return baseMajorMapper.selectList(wrapper.orderByAsc(BaseMajor::getId));
    }

    @Override
    public Long createMajor(MajorSaveRequest request) {
        requireCollege(request.getCollegeId());
        BaseMajor major = new BaseMajor();
        major.setCollegeId(request.getCollegeId());
        major.setMajorCode(request.getMajorCode());
        major.setMajorName(request.getMajorName());
        major.setStatus(request.getStatus());
        baseMajorMapper.insert(major);
        return major.getId();
    }

    @Override
    public void updateMajor(Long id, MajorSaveRequest request) {
        requireMajor(id);
        requireCollege(request.getCollegeId());
        BaseMajor update = new BaseMajor();
        update.setId(id);
        update.setCollegeId(request.getCollegeId());
        update.setMajorCode(request.getMajorCode());
        update.setMajorName(request.getMajorName());
        update.setStatus(request.getStatus());
        baseMajorMapper.updateById(update);
    }

    @Override
    public List<BaseClassInfo> listClasses(Long majorId) {
        LambdaQueryWrapper<BaseClassInfo> wrapper = new LambdaQueryWrapper<>();
        if (majorId != null) {
            wrapper.eq(BaseClassInfo::getMajorId, majorId);
        }
        return baseClassInfoMapper.selectList(wrapper.orderByAsc(BaseClassInfo::getId));
    }

    @Override
    public Long createClass(ClassSaveRequest request) {
        requireMajor(request.getMajorId());
        BaseClassInfo classInfo = new BaseClassInfo();
        classInfo.setMajorId(request.getMajorId());
        classInfo.setClassName(request.getClassName());
        classInfo.setGrade(request.getGrade());
        classInfo.setStatus(request.getStatus());
        baseClassInfoMapper.insert(classInfo);
        return classInfo.getId();
    }

    @Override
    public void updateClass(Long id, ClassSaveRequest request) {
        requireClassInfo(id);
        requireMajor(request.getMajorId());
        BaseClassInfo update = new BaseClassInfo();
        update.setId(id);
        update.setMajorId(request.getMajorId());
        update.setClassName(request.getClassName());
        update.setGrade(request.getGrade());
        update.setStatus(request.getStatus());
        baseClassInfoMapper.updateById(update);
    }

    @Override
    public List<BaseTerm> listTerms() {
        return baseTermMapper.selectList(new LambdaQueryWrapper<BaseTerm>()
                .orderByDesc(BaseTerm::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTerm(TermSaveRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("学期开始日期不能晚于结束日期");
        }
        if (request.getIsCurrent() != null && request.getIsCurrent() == 1) {
            clearCurrentTermFlag();
        }
        BaseTerm term = new BaseTerm();
        term.setTermName(request.getTermName());
        term.setStartDate(request.getStartDate());
        term.setEndDate(request.getEndDate());
        term.setIsCurrent(request.getIsCurrent());
        term.setStatus(request.getStatus());
        baseTermMapper.insert(term);
        return term.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTerm(Long id, TermSaveRequest request) {
        requireTerm(id);
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new BusinessException("学期开始日期不能晚于结束日期");
        }
        if (request.getIsCurrent() != null && request.getIsCurrent() == 1) {
            clearCurrentTermFlag();
        }
        BaseTerm update = new BaseTerm();
        update.setId(id);
        update.setTermName(request.getTermName());
        update.setStartDate(request.getStartDate());
        update.setEndDate(request.getEndDate());
        update.setIsCurrent(request.getIsCurrent());
        update.setStatus(request.getStatus());
        baseTermMapper.updateById(update);
    }

    @Override
    public List<BaseActivityCategory> listCategories() {
        return baseActivityCategoryMapper.selectList(new LambdaQueryWrapper<BaseActivityCategory>()
                .orderByAsc(BaseActivityCategory::getId));
    }

    @Override
    public Long createCategory(CategorySaveRequest request) {
        BaseActivityCategory category = new BaseActivityCategory();
        category.setCategoryName(request.getCategoryName());
        category.setDefaultCredit(request.getDefaultCredit());
        category.setMaxCreditPerTerm(request.getMaxCreditPerTerm());
        category.setStatus(request.getStatus());
        baseActivityCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateCategory(Long id, CategorySaveRequest request) {
        requireCategory(id);
        BaseActivityCategory update = new BaseActivityCategory();
        update.setId(id);
        update.setCategoryName(request.getCategoryName());
        update.setDefaultCredit(request.getDefaultCredit());
        update.setMaxCreditPerTerm(request.getMaxCreditPerTerm());
        update.setStatus(request.getStatus());
        baseActivityCategoryMapper.updateById(update);
    }

    private void clearCurrentTermFlag() {
        baseTermMapper.update(null, new LambdaUpdateWrapper<BaseTerm>()
                .set(BaseTerm::getIsCurrent, 0)
                .eq(BaseTerm::getIsCurrent, 1));
    }

    private BaseCollege requireCollege(Long id) {
        BaseCollege college = baseCollegeMapper.selectById(id);
        if (college == null) {
            throw new BusinessException(404, "学院不存在");
        }
        return college;
    }

    private BaseMajor requireMajor(Long id) {
        BaseMajor major = baseMajorMapper.selectById(id);
        if (major == null) {
            throw new BusinessException(404, "专业不存在");
        }
        return major;
    }

    private BaseClassInfo requireClassInfo(Long id) {
        BaseClassInfo classInfo = baseClassInfoMapper.selectById(id);
        if (classInfo == null) {
            throw new BusinessException(404, "班级不存在");
        }
        return classInfo;
    }

    private BaseTerm requireTerm(Long id) {
        BaseTerm term = baseTermMapper.selectById(id);
        if (term == null) {
            throw new BusinessException(404, "学期不存在");
        }
        return term;
    }

    private BaseActivityCategory requireCategory(Long id) {
        BaseActivityCategory category = baseActivityCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "活动类别不存在");
        }
        return category;
    }
}

