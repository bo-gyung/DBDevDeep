package com.dbdevdeep.student.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.employee.domain.TeacherHistoryDto;
import com.dbdevdeep.employee.service.TeacherHistoryService;
import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.StudentClassDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.SubjectDto;
import com.dbdevdeep.student.domain.TimeTableDto;
import com.dbdevdeep.student.service.StudentService;

@Controller
public class StudentViewController {
	
	private final StudentService studentService;
	private final TeacherHistoryService teacherHistoryService;
	
	@Autowired
	public StudentViewController(StudentService studentService, TeacherHistoryService teacherHistoryService) {
		this.studentService = studentService;
		this.teacherHistoryService = teacherHistoryService;
	}
	
	// 학생관리 메인 페이지로 이동
	@GetMapping("/student")
	public String studentMainPage() {
		return "student/student_homepage";
	}
	
	// 학생등록 페이지로 이동
	@GetMapping("/student/create")
	public String createStudentPage() {
		return "student/student_create";
	}
	
	// 학생 목록 페이지로 이동
	@GetMapping("/student/list")
	public String listStudentPage(Model model, StudentClassDto dto) {
		List<StudentClassDto> resultList = studentService.selectStudentList(dto);
		model.addAttribute("resultList",resultList);
		return "student/student_list";
	}
	
	// 학생 정보 상세 페이지로 이동
	@GetMapping("/student/{student_no}")
	public String selectStudentOne(Model model,
			@PathVariable("student_no") Long student_no) {
		StudentDto dto = studentService.selectStudentOne(student_no);
		List<StudentClassDto> studentClassResultList= studentService.selectStudentClassList(student_no);
		model.addAttribute("dto",dto);
		model.addAttribute("cdto",studentClassResultList);
		return "student/student_detail";
	}
	
	// 학생 정보 수정 페이지로 이동
	@GetMapping("/student/update/{student_no}")
	public String updateStudentInfo(@PathVariable("student_no") Long student_no,Model model) {
		StudentDto dto = studentService.selectStudentOne(student_no);
		model.addAttribute("dto",dto);
		return "student/student_update";
	}
	
	// 학년 이력 정보 수정 페이지로 이동(반배정)
	@GetMapping("/student/class/{student_no}")
	public String classAssign(@PathVariable("student_no") Long student_no, Model model) {
	    List<TeacherHistoryDto> resultList = teacherHistoryService.selectClassList();
	    StudentDto sdto = studentService.selectStudentOne(student_no);
	    List<StudentClassDto> studentClassResultList= studentService.selectStudentClassList(student_no);
	    
	    // 학년도 목록 중복 제거 후 역순 정렬
	    List<String> Tyear = resultList.stream()
	            .map(TeacherHistoryDto::getT_year)
	            .distinct()
	            .sorted(Comparator.reverseOrder())
	            .collect(Collectors.toList());
	    
	    model.addAttribute("cdto",studentClassResultList);
	    model.addAttribute("Tyear", Tyear);
	    model.addAttribute("sdto", sdto);
	    return "student/student_class";
	}
	
	// 학부모 정보 등록 페이지
	@GetMapping("/student/parent/{student_no}")
	public String parentInfo(@PathVariable("student_no") Long student_no, Model model) {
		List<ParentDto> resultList = studentService.selectStudentParentList(student_no);
		StudentDto sdto = studentService.selectStudentOne(student_no);
		
		model.addAttribute("sdto",sdto);
		model.addAttribute("resultList",resultList);
		return "student/student_parent";
	}
	
	// 과목 리스트 조회 페이지로 이동
	@GetMapping("/student/subject")
	public String listSubjectPage(Model model, SubjectDto dto) {
		/*
		 * Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication(); User user = (User)authentication.getPrincipal();
		 * 
		 */		
		List<SubjectDto> subjectList = studentService.mySubjectList();
		model.addAttribute("subjectList",subjectList);
		return "student/subject_list";
	}

	// 과목 정보 상세 페이지로 이동
		@GetMapping("/subject/{subject_no}")
		public String selectSubjectOne(Model model, @PathVariable("subject_no") Long subject_no) {
			SubjectDto dto = studentService.selectSubjectOne(subject_no);
			List<CurriculumDto> cdto = studentService.selectCurriOne(subject_no);
			List<TimeTableDto> tdto = studentService.selectTimeTableOne(subject_no);
			model.addAttribute("timetableDetail",tdto);
			model.addAttribute("subjectDetail",dto);
			model.addAttribute("curriDetail",cdto);
			return "student/subject_detail";
		}

	// 과목 등록 페이지로 이동
		@GetMapping("/subject/create")
		public String createSubjectPage() {
			return "student/subject_create";
		}
}
