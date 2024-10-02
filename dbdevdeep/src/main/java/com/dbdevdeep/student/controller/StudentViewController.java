package com.dbdevdeep.student.controller;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.employee.domain.TeacherHistoryDto;
import com.dbdevdeep.employee.service.TeacherHistoryService;
import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.ScoreDto;
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
	
	@GetMapping("/student")
	public String studentMainPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String empId = authentication.getName(); // 현재 로그인된 사용자의 emp_id 가져오기
		
	    // emp_id로 Employee 조회 후 emp_name 가져오기
	    String empName = studentService.findEmpNameByEmpId(empId);
	    List<SubjectDto> mysubjectList = studentService.findMySubjectList(empId);
	    List<StudentClassDto> mystudentList = studentService.findMyStudentList(empId);
	    
	    Map<String, String> timeTableMap = new HashMap<>();
	    Map<String, String> subjectMap = new HashMap<>();  // 과목번호와 과목명을 매핑할 Map

	    // 시간표 데이터를 요일 및 교시 기준으로 매핑
	    for (SubjectDto subject : mysubjectList) {
	        Long subjectNo = subject.getSubject_no();
	        String subjectName = subject.getSubject_name();  // 과목 이름 가져오기
	        List<TimeTableDto> timeTable = studentService.selectTimeTableOne(subjectNo);
	        subjectMap.put(subjectNo.toString(), subjectName);  // 과목번호와 과목명 매핑

	        for (TimeTableDto t : timeTable) {
	            String key = t.getDay() + "-" + t.getPeriod();
	            timeTableMap.put(key, t.getSubject_no().toString());
	        }
	    }
	    
	    
	    model.addAttribute("mystudentList",mystudentList);
	    model.addAttribute("empname",empName);
	    model.addAttribute("timeTableMap", timeTableMap);
	    model.addAttribute("subjectMap", subjectMap);
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
	
	// 학생 목록 페이지로 이동(직원)
		@GetMapping("/student/list/emp")
		public String listStudentPageEmp(Model model, StudentClassDto dto) {
			List<StudentClassDto> resultList = studentService.selectStudentList(dto);
			model.addAttribute("resultList",resultList);
			return "student/student_list_for_emp";
		}
	
	// 학생 정보 상세 페이지로 이동
	@GetMapping("/student/{student_no}")
	public String selectStudentOne(Model model,
			@PathVariable("student_no") Long student_no) {
		StudentDto dto = studentService.selectStudentOne(student_no);
		List<StudentClassDto> studentClassResultList= studentService.selectStudentClassList(student_no);
		List<ParentDto> resultList = studentService.selectStudentParentList(student_no);
		model.addAttribute("dto",dto);
		model.addAttribute("pdto",resultList);
		model.addAttribute("cdto",studentClassResultList);
		return "student/student_detail";
	}
	
	// 학생 정보 상세 페이지로 이동
	@GetMapping("/student/emp/{student_no}")
	public String selectStudentOneEmp(Model model,
			@PathVariable("student_no") Long student_no) {
		StudentDto dto = studentService.selectStudentOne(student_no);
		List<StudentClassDto> studentClassResultList= studentService.selectStudentClassList(student_no);
		List<ParentDto> resultList = studentService.selectStudentParentList(student_no);
		model.addAttribute("dto",dto);
		model.addAttribute("pdto",resultList);
		model.addAttribute("cdto",studentClassResultList);
		return "student/student_detail_for_emp";
	}
	
	// 학생 정보 수정 페이지로 이동
	@GetMapping("/student/update/{student_no}")
	public String updateStudentInfo(@PathVariable("student_no") Long student_no,Model model) {
		StudentDto dto = studentService.selectStudentOne(student_no);
		model.addAttribute("dto",dto);
		return "student/student_update";
	}
	
	// 학생 정보 수정 페이지로 이동
	@GetMapping("/student/update/emp/{student_no}")
	public String updateStudentInfoEmp(@PathVariable("student_no") Long student_no,Model model) {
		StudentDto dto = studentService.selectStudentOne(student_no);
		model.addAttribute("dto",dto);
		return "student/student_update_for_emp";
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
	
	// 학년 이력 정보 수정 페이지로 이동(반배정)(교직원)
		@GetMapping("/student/class/emp/{student_no}")
		public String classAssignEmp(@PathVariable("student_no") Long student_no, Model model) {
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
		    return "student/student_class_for_emp";
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
	
	// 학부모 정보 등록 페이지(교직원)
		@GetMapping("/student/parent/emp/{student_no}")
		public String parentInfoEmp(@PathVariable("student_no") Long student_no, Model model) {
			List<ParentDto> resultList = studentService.selectStudentParentList(student_no);
			StudentDto sdto = studentService.selectStudentOne(student_no);
			
			model.addAttribute("sdto",sdto);
			model.addAttribute("resultList",resultList);
			return "student/student_parent_for_emp";
		}
	
	// 과목 리스트 조회 페이지로 이동
	@GetMapping("/student/subject")
	public String listSubjectPage(Model model, SubjectDto dto) {

		List<SubjectDto> subjectList = studentService.mySubjectList();
		model.addAttribute("subjectList",subjectList);
		return "student/subject_list";
	}

	// 과목 정보 상세 페이지로 이동
		@GetMapping("/student/subject/{subject_no}")
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
		@GetMapping("/student/subject/create")
		public String createSubjectPage() {
			return "student/subject_create";
		}
		
	// 성적 등록 페이지로 이동
		@GetMapping("/student/score")
		public String listScorePage(Model model, SubjectDto sdto, StudentClassDto cdto) {
			List<StudentClassDto> resultList = studentService.selectStudentList(cdto);
			List<SubjectDto> subjectList = studentService.mySubjectList();
			model.addAttribute("subjectList",subjectList);
			model.addAttribute("resultList",resultList);
			return "student/score_list";
		}
		
	// 과목 별 성적 등록 페이지로 이동
		@GetMapping("/student/score/subject/{subject_no}")
		public String scoreSubjectOne(Model model, @PathVariable("subject_no") Long subject_no, SubjectDto sdto, StudentClassDto cdto) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); 
			String username = authentication.getName();
			
			List<StudentClassDto> resultList = studentService.selectStudentListBySubject(subject_no);
			SubjectDto subjectDto = studentService.selectSubjectOne(subject_no);
			List<CurriculumDto> curriDto = studentService.selectCurriOne(subject_no);
			List<ScoreDto> scoreList = studentService.selectScoreBySubject(subject_no);
			Map<Long, Map<Long, String>> scoreMap = new HashMap<>();

			for (ScoreDto score : scoreList) {
			    Long studentNo = score.getStudent_no(); // 각 ScoreDto에서 student_no를 가져옴
			    Long curriculumNo = score.getCurriculum_no(); // 각 ScoreDto에서 curriculum_no를 가져옴
			    String scoreValue = score.getScore(); // 각 ScoreDto에서 점수를 가져옴
			    
			    // studentNo에 해당하는 Map이 없으면 새로 생성
			    scoreMap.putIfAbsent(studentNo, new HashMap<>());
			    
			    // studentNo에 해당하는 curriculumNo와 score를 저장
			    scoreMap.get(studentNo).put(curriculumNo, scoreValue);
			}
			
			// 모델에 scoreMap을 추가
			model.addAttribute("username",username);
			model.addAttribute("scoreList", scoreMap);
			model.addAttribute("subject",subjectDto);
			model.addAttribute("resultList",resultList);
			model.addAttribute("curriList",curriDto);
			return "student/score_subject";
		}
		
	// 학생 별 성적 등록 페이지로 이동
		@GetMapping("/student/score/student/{student_no}")
		public String scoreStudentOne(Model model, @PathVariable("student_no") Long student_no, SubjectDto sdto, StudentClassDto cdto) {
			StudentDto studentDto = studentService.selectStudentOne(student_no);
			StudentClassDto studentClassResult= studentService.selectStudentClass(student_no);
			List<SubjectDto> subjectList = studentService.studentSubjectRecentList(studentClassResult);
			List<CurriculumDto> curriList = studentService.selectCurriAll();
			List<ScoreDto> scoreList = studentService.selectScoreByStudent(student_no);
			
			Map<Long, String> scoreMap = new HashMap<>();
		    for (ScoreDto score : scoreList) {
		        scoreMap.put(score.getCurriculum_no(), score.getScore());
		    }
		    
		    double totalScore = 0;
		    Map<Long, String> totalScoreMap = new HashMap<>();

		    // 각 과목별로 총점을 계산
		    for (SubjectDto subject : subjectList) {
		        totalScore = 0;  // 과목별 총점 초기화

		        // 해당 과목의 커리큘럼을 필터링하여 총점 계산
		        for (CurriculumDto curri : curriList) {
		            if (curri.getSubject().getSubjectNo().equals(subject.getSubject_no())) {
		                // 해당 커리큘럼의 점수를 누적하여 계산
		                for (ScoreDto score : scoreList) {
		                    if (score.getCurriculum_no().equals(curri.getCurriculum_no())) {
		                        // 총점을 누적 ( += 사용하여 점수를 더함)
		                        totalScore += ((Double.parseDouble(score.getScore())) 
		                                      / (Double.parseDouble(curri.getCurriculum_max_score())))
		                                      * (Double.parseDouble(curri.getCurriculum_ratio()));  // 반영 비율 적용
		                    }
		                }
		            }
		        }
		        // 계산된 총점을 문자열로 변환하여 Map에 저장
		        totalScoreMap.put(subject.getSubject_no(), String.format("%.2f", totalScore));
		    }

			model.addAttribute("scoreList",scoreMap);
			model.addAttribute("subjectList",subjectList);
			model.addAttribute("resultList",studentDto);
			model.addAttribute("curriList",curriList);
			model.addAttribute("totalScoreMap", totalScoreMap);
			return "student/score_student";
		}
		
		// 학생 목록 페이지로 이동
		@GetMapping("/student/record")
		public String listStudentRecord(Model model, StudentClassDto dto) {
			List<StudentClassDto> resultList = studentService.selectStudentList(dto);
			model.addAttribute("resultList",resultList);
			return "student/student_record_list";
		}
		
		// 학생 생활기록부 상세보기 페이지로 이동
		@GetMapping("/student/record/{student_no}")
		public String detailStudentRecord(Model model, 
				@PathVariable("student_no") Long student_no) {
			StudentDto dto = studentService.selectStudentOne(student_no);
			List<StudentClassDto> studentClassResultList= studentService.selectStudentClassList(student_no);
			List<ParentDto> resultList = studentService.selectStudentParentList(student_no);
			List<SubjectDto> subjectList = studentService.studentSubjectList(studentClassResultList);
			List<CurriculumDto> curriList = studentService.selectCurriAll();
			List<ScoreDto> scoreList = studentService.selectScoreByStudent(student_no);
			
			
			Map<Long, String> scoreMap = new HashMap<>();
		    for (ScoreDto score : scoreList) {
		        scoreMap.put(score.getCurriculum_no(), score.getScore());
		    }
		    
		    
		    double totalScore = 0;
		    Map<Long, String> totalScoreMap = new HashMap<>();

		    // 각 과목별로 총점을 계산
		    for (SubjectDto subject : subjectList) {
		        totalScore = 0;  // 과목별 총점 초기화

		        // 해당 과목의 커리큘럼을 필터링하여 총점 계산
		        for (CurriculumDto curri : curriList) {
		            if (curri.getSubject().getSubjectNo().equals(subject.getSubject_no())) {
		                // 해당 커리큘럼의 점수를 누적하여 계산
		                for (ScoreDto score : scoreList) {
		                    if (score.getCurriculum_no().equals(curri.getCurriculum_no())) {
		                        // 총점을 누적 ( += 사용하여 점수를 더함)
		                        totalScore += ((Double.parseDouble(score.getScore())) 
		                                      / (Double.parseDouble(curri.getCurriculum_max_score())))
		                                      * (Double.parseDouble(curri.getCurriculum_ratio()));  // 반영 비율 적용
		                    }
		                }
		            }
		        }
		        // 계산된 총점을 문자열로 변환하여 Map에 저장
		        totalScoreMap.put(subject.getSubject_no(), String.format("%.2f", totalScore));
		    }
		    
		    model.addAttribute("scoreList",scoreMap);
			model.addAttribute("subjectList",subjectList);
		    model.addAttribute("curriList",curriList);
			model.addAttribute("dto",dto);
			model.addAttribute("pdto",resultList);
			model.addAttribute("cdto",studentClassResultList);
			model.addAttribute("totalScoreMap", totalScoreMap);
			
			return "student/student_record_detail";
		}
		
		// 통계 페이지로 이동
		@GetMapping("/student/stat")
		public String studentStatisticPage() {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			String empId = authentication.getName();
			
			
			
			return "student/student_statistic";
		}

}
