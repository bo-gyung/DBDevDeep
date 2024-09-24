package com.dbdevdeep.employee.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.employee.domain.TeacherHistoryDto;
import com.dbdevdeep.employee.mybatis.mapper.TeacherHistoryVoMapper;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.employee.repository.TeacherHistoryRepository;
import com.dbdevdeep.employee.vo.GradeClassGroup;

@Service
public class TeacherHistoryService {

	private final TeacherHistoryRepository teacherHistoryRepository;
	private final TeacherHistoryVoMapper teacherHistoryVoMapper;
	private final EmployeeRepository employeeRepository;

	@Autowired
	public TeacherHistoryService(TeacherHistoryRepository teacherHistoryRepository,
			TeacherHistoryVoMapper teacherHistoryVoMapper, EmployeeRepository employeeRepository) {
		this.teacherHistoryRepository = teacherHistoryRepository;
		this.teacherHistoryVoMapper = teacherHistoryVoMapper;
		this.employeeRepository = employeeRepository;
	}

	public List<TeacherHistoryDto> selectClassByYearList(String t_year) {
		List<TeacherHistory> teacherHistoryList = teacherHistoryRepository.findByClassByYear(t_year);

		List<TeacherHistoryDto> teacherHistoryDtoList = new ArrayList<TeacherHistoryDto>();
		for (TeacherHistory t : teacherHistoryList) {
			TeacherHistoryDto dto = new TeacherHistoryDto().toDto(t);

			if (t.getEmployee() != null) {
				dto.setTeach_emp_id(t.getEmployee().getEmpId());
				dto.setTeach_emp_name(t.getEmployee().getEmpName());
			} else {
				dto.setTeach_emp_id(null);
				dto.setTeach_emp_name("null");
			}
			teacherHistoryDtoList.add(dto);
		}

		return teacherHistoryDtoList;
	}

	public List<GradeClassGroup> GroupByYearList() {
		List<GradeClassGroup> gradeClassGroupList = teacherHistoryVoMapper.selectGroupByYear();

		return gradeClassGroupList;
	}

	public int tYearCheck(String t_year) {
		int result = -1;

		int countYear = teacherHistoryRepository.findByTYearForCreateClass(t_year);

		if (countYear == 0) {
			result = 0; // 중복 X
		} else {
			result = 1; // 중복 O
		}

		return result;
	}

	public int saveTeacherHistory(int grade, int gradeClass, String t_year) {
		int result = -1;

		try {
			TeacherHistoryDto dto = new TeacherHistoryDto();

			dto.setGrade(grade);
			dto.setGrade_class(gradeClass);
			dto.setT_year(t_year);

			TeacherHistory th = dto.toEntity();

			teacherHistoryRepository.save(th);

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int addTeacher(TeacherHistoryDto dto) {
		int result = -1;

		try {

			Employee emp = employeeRepository.findByempId(dto.getTeach_emp_id());

			TeacherHistory th = TeacherHistory.builder().teacherNo(dto.getTeacher_no()).grade(dto.getGrade())
					.gradeClass(dto.getGrade_class()).tYear(dto.getT_year()).employee(emp).build();

			teacherHistoryRepository.save(th);

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public List<TeacherHistoryDto> selectClassByOrderLastesList() {
		String recentYear = teacherHistoryRepository.findMostRecentYear();

		List<TeacherHistory> teacherHistoryList = teacherHistoryRepository.findByClassByYear(recentYear);

		List<TeacherHistoryDto> teacherHistoryDtoList = new ArrayList<TeacherHistoryDto>();
		for (TeacherHistory t : teacherHistoryList) {
			TeacherHistoryDto dto = new TeacherHistoryDto().toDto(t);

			if (t.getEmployee() != null) {
				dto.setTeach_emp_id(t.getEmployee().getEmpId());
				dto.setTeach_emp_name(t.getEmployee().getEmpName());
			} else {
				dto.setTeach_emp_id(null);
				dto.setTeach_emp_name("null");
			}
			teacherHistoryDtoList.add(dto);
		}

		return teacherHistoryDtoList;
	}
	
	public List<String> selectClassByOrderLastesListForAddressBook() {
		String recentYear = teacherHistoryRepository.findMostRecentYear();

		List<TeacherHistory> teacherHistoryList = teacherHistoryRepository.findByClassByYear(recentYear);

		List<String> teacherHistoryDtoList = new ArrayList<>();
		
		for (TeacherHistory t : teacherHistoryList) {
			String list = t.getEmployee().getEmpId();

			teacherHistoryDtoList.add(list);
		}

		return teacherHistoryDtoList;
	}

	public TeacherHistoryDto selectHistoryOne(EmployeeDto dto) {
		TeacherHistoryDto resultDto = null;

		String t_year = teacherHistoryRepository.findMostRecentYear();

		TeacherHistory th = teacherHistoryRepository.selectHistoryOne(dto.getEmp_id(), t_year);

		if (th != null) {
			resultDto.toDto(th);
		}

		return resultDto;
	}

	public int deleteByTYear(String t_year) {
		int result = -1;

		try {
			teacherHistoryRepository.deleteByTYear(t_year);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int deleteByGradeClassTyear(int grade, int grade_class, String t_year) {
		int result = -1;

		try {
			Long teacherNo = teacherHistoryRepository.selectByGradeClassTyear(grade, grade_class, t_year);
			teacherHistoryRepository.deleteById(teacherNo);

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int tYearGradeCount(String t_year, int grade) {
		return teacherHistoryRepository.countByTyearGrade(t_year, grade);
	}

	public List<TeacherHistoryDto> selectTeacherHistoryByEmployee(String emp_id) {
		List<TeacherHistoryDto> thListDto = new ArrayList<TeacherHistoryDto>();

		List<TeacherHistory> th = teacherHistoryRepository.selectTeacherHistoryByEmployee(emp_id);
		
		for (TeacherHistory t : th) {
			TeacherHistoryDto thDto = new TeacherHistoryDto().toDto(t);
			
			thListDto.add(thDto);
		}

		return thListDto;
	}

	public List<TeacherHistoryDto> selectClassList() {
		List<TeacherHistory> thList = teacherHistoryRepository.findAll();
		List<TeacherHistoryDto> thDtoList = new ArrayList<TeacherHistoryDto>();
		for (TeacherHistory t : thList) {
			TeacherHistoryDto dto = new TeacherHistoryDto().toDto(t);
			thDtoList.add(dto);
		}
		return thDtoList;
	}

	public List<TeacherHistoryDto> getDataByYearAndGradeList(String t_year, String grade) {
		List<TeacherHistory> teacherHistoryList = teacherHistoryRepository.findByYearAndGrade(t_year, grade);
		List<TeacherHistoryDto> teacherHistoryDtoList = new ArrayList<TeacherHistoryDto>();

		for (TeacherHistory t : teacherHistoryList) {
			if (t.getEmployee() != null) {
				TeacherHistoryDto dto = new TeacherHistoryDto().toDto(t);
				dto.setTeach_emp_id(t.getEmployee().getEmpId());
				dto.setTeach_emp_name(t.getEmployee().getEmpName());
				teacherHistoryDtoList.add(dto); // 리스트에 추가
			}
		}
		return teacherHistoryDtoList;
	}
	
	// 모든 신청인(직원) 조회
    public List<TeacherHistory> findAllTeachers() {
        return teacherHistoryRepository.findAll();
    }

    

}
