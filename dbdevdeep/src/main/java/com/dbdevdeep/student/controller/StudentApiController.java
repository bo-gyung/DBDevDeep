package com.dbdevdeep.student.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.employee.domain.TeacherHistoryDto;
import com.dbdevdeep.employee.repository.TeacherHistoryRepository;
import com.dbdevdeep.employee.service.TeacherHistoryService;
import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.Score;
import com.dbdevdeep.student.domain.ScoreDto;
import com.dbdevdeep.student.domain.StudentClassDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.SubjectDetailsDto;
import com.dbdevdeep.student.domain.SubjectDto;
import com.dbdevdeep.student.repository.ScoreRepository;
import com.dbdevdeep.student.service.StudentService;

@Controller
public class StudentApiController {
	
	// 의존성 주입
	private final StudentService studentService;
	private final TeacherHistoryService teacherHistoryService;
	private final TeacherHistoryRepository teacherHistoryRepository;
	private final FileService fileService;
	private final ScoreRepository scoreRepository;
	
	@Autowired
	public StudentApiController(StudentService studentService, TeacherHistoryService teacherHistoryService,TeacherHistoryRepository teacherHistoryRepository, FileService fileService, ScoreRepository scoreRepository) {
		this.studentService = studentService;
		this.teacherHistoryService = teacherHistoryService;
		this.teacherHistoryRepository = teacherHistoryRepository;
		this.fileService = fileService;
		this.scoreRepository = scoreRepository;
	}
	
	
	// 학생 등록
	@ResponseBody
	@PostMapping("/student")
	public Map<String,String> createStudent(StudentDto dto,
	        @RequestParam("file") MultipartFile file){
	    Map<String,String> resultMap = new HashMap<String,String>();
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "학생 정보 등록 중 오류가 발생했습니다.");
	    
	    try {
	        // 파일이 null이 아닌 경우 처리
	        if(file != null && !file.isEmpty()) {  
	            String originalFilename = file.getOriginalFilename();
	            
	            // 파일 이름이 비어있거나 null인지 확인
	            if (originalFilename != null && !originalFilename.isEmpty()) {
	                String savedFileName = fileService.studentPicUpload(file);
	                
	                if(savedFileName != null) {
	                    dto.setStudent_ori_pic(originalFilename);
	                    dto.setStudent_new_pic(savedFileName);
	                }
	            } else {
	                resultMap.put("res_msg", "학생 정보 중 파일 이름이 유효하지 않습니다.");
	                return resultMap;
	            }
	        }
	        // 학생 정보 생성
	        if(studentService.createStudent(dto) != null) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "학생 정보가 성공적으로 등록되었습니다.");
	        } else {
	            resultMap.put("res_msg", "학생 정보 등록에 실패하였습니다.");
	        }
	    } catch (Exception e) {
	        // 예외 발생 시 메시지 처리
	       e.printStackTrace();
	    }
	    
	    return resultMap;
	}
	
	// 학생 정보 수정
	@ResponseBody
	@PostMapping("/student/{student_no}")
	public Map<String,String> updateStudent(StudentDto dto,
			@RequestParam(name="file",required=false)MultipartFile file){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "학생 정보 수정 중 오류가 발생했습니다.");
		
		if(file != null && "".equals(file.getOriginalFilename()) == false) {
			String savedFileName = fileService.studentPicUpload(file);
			if(savedFileName != null) {
				dto.setStudent_ori_pic(file.getOriginalFilename());
				dto.setStudent_new_pic(savedFileName);
				
				if(fileService.studentPicDelete(dto.getStudent_no()) > 0){
					resultMap.put("res_msg", "학생 정보 중 파일이 정상적으로 삭제되었습니다");
				}
				
			}else {
				resultMap.put("res_msg", "새로운 학생 파일 업로드가 실패하였습니다.");
			}
		}
		
		if(studentService.updateStudentInfo(dto) != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "학생 정보가 성공적으로 수정되었습니다.");
		}
		
		return resultMap;
	}
	
	// 학생 삭제 처리
	@ResponseBody
	@DeleteMapping("/student/{student_no}")
	public Map<String,String> deleteStudent(@PathVariable("student_no") Long student_no){
		Map<String,String> map = new HashMap<String,String>();
		map.put("res_code", "404");
		map.put("res_msg", "학생 정보 삭제 중 오류가 발생했습니다");
	    try {
	        // 파일이 존재하면 삭제하고, 그렇지 않으면 건너뛰기
	        int fileDeleteResult = fileService.studentPicDelete(student_no); 

	        if (fileDeleteResult > 0) {
	            map.put("res_msg", "학생 정보 중 파일이 정상적으로 삭제되었습니다.");
	        } else {
	            map.put("res_msg", "학생 파일이 존재하지 않거나 삭제할 파일이 없습니다.");
	        }

	        // 파일 여부와 관계없이 학생 정보 삭제
	        if (studentService.deleteStudent(student_no) > 0) {  
	            map.put("res_code", "200");
	            map.put("res_msg", "정상적으로 학생 정보가 삭제되었습니다.");
	        } else {
	            map.put("res_msg", "학생 정보 삭제 중 오류가 발생했습니다.");
	        }

	    } catch (Exception e) {
	        // 에러 발생 시
	        map.put("res_msg", "삭제 중 오류가 발생했습니다: " + e.getMessage());
	        e.printStackTrace();
	    }
		return map;
	}
	
	// 학생 부모 정보 삭제 처리
	@ResponseBody
	@DeleteMapping("/student/parent/{parent_no}")
	public Map<String,String> deleteParent(@PathVariable("parent_no") Long parent_no){
		Map<String,String> map = new HashMap<String,String>();
		map.put("res_code", "404");
		map.put("res_msg", "부모 정보 삭제 중 오류가 발생했습니다");
		if(studentService.deleteParent(parent_no) > 0) {
			map.put("res_code", "200");
			map.put("res_msg","정상적으로 부모 정보가 삭제되었습니다.");
		}
		return map;
	}
	
	// 부모 정보 수정
		@ResponseBody
		@PostMapping("/student/parent/update/{student_no}")
		public Map<String,String> updateStudentParent(ParentDto dto){
			Map<String,String> resultMap = new HashMap<String,String>();
			resultMap.put("res_code", "404");
			resultMap.put("res_msg", "부모 정보 수정 중 오류가 발생했습니다.");
			
			if(studentService.updateStudentParentInfo(dto) != null) {
				resultMap.put("res_code", "200");
				resultMap.put("res_msg", "부모 정보가 성공적으로 수정되었습니다.");
			}
			
			return resultMap;
		}
	
	// 반배정시 학년도에 따른 학년 데이터 가져오기
	@GetMapping("/student/student_class/selectByYear/{t_year}")
	@ResponseBody
	public List<TeacherHistoryDto> getDataByYear(@PathVariable("t_year") String t_year) {
		// 학년도에 해당하는 데이터를 가져옵니다.
	    List<TeacherHistoryDto> data = teacherHistoryService.selectClassByYearList(t_year);
	    // 데이터를 JSON 형식으로 반환합니다.
	    return data;
	}
	
	// 반배정시 학년도에 따른 학년 데이터 가져오기(직원용)
	@GetMapping("/employee/student/student_class/selectByYear/{t_year}")
	@ResponseBody
	public List<TeacherHistoryDto> getDataByYearforEmp(@PathVariable("t_year") String t_year) {
		// 학년도에 해당하는 데이터를 가져옵니다.
	    List<TeacherHistoryDto> data = teacherHistoryService.selectClassByYearList(t_year);
	    // 데이터를 JSON 형식으로 반환합니다.
	    return data;
	}
	
	// 학년 이력 내림차순 조회
	@GetMapping("/student/student_class/selectByYearAndGrade/{t_year}/{grade}")
	@ResponseBody
	public List<TeacherHistoryDto> getDataByYearAndGrade(@PathVariable("t_year") String t_year, @PathVariable("grade") String grade) {
	    // t_year와 grade에 해당하는 데이터를 조회
	    List<TeacherHistoryDto> data = teacherHistoryService.getDataByYearAndGradeList(t_year, grade);
	    List<TeacherHistoryDto> filteredData = data.stream()
                .filter(Objects::nonNull) // null이 아닌 객체만 필터링
                .collect(Collectors.toList());

	    return filteredData;
	}
	
	// 학년 이력 내림차순 조회(직원)
	@GetMapping("/employee/student/student_class/selectByYearAndGrade/{t_year}/{grade}")
	@ResponseBody
	public List<TeacherHistoryDto> getDataByYearAndGradeforEmp(@PathVariable("t_year") String t_year, @PathVariable("grade") String grade) {
	    // t_year와 grade에 해당하는 데이터를 조회
	    List<TeacherHistoryDto> data = teacherHistoryService.getDataByYearAndGradeList(t_year, grade);
	    List<TeacherHistoryDto> filteredData = data.stream()
                .filter(Objects::nonNull) // null이 아닌 객체만 필터링
                .collect(Collectors.toList());

	    return filteredData;
	}
	
	// 반배정
	@ResponseBody
	@PostMapping("/student/class/assign")
	public Map<String,String> assignClassForStudent(StudentClassDto dto){
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "반 배정 중 오류가 발생했습니다.");
		
		if(studentService.assignClass(dto) != null) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "반 배정이 성공적으로 수행되었습니다.");
		}
		return resultMap;
	}
	
	// 반 이력 삭제 처리
	@ResponseBody
	@DeleteMapping("/student/class/{class_no}")
	public Map<String,String> deleteStudentClass(@PathVariable("class_no") Long class_no){
		Map<String,String> map = new HashMap<String,String>();
		map.put("res_code", "404");
		map.put("res_msg", "학급 이력 삭제 중 오류가 발생했습니다");			
		if(studentService.deleteStudentClass(class_no)>0) {				
			map.put("res_code", "200");
			map.put("res_msg","정상적으로 학급 이력이 삭제되었습니다.");
		}			
		return map;
	}	
	
	 // 부모 정보 등록
	 @ResponseBody
	 @PostMapping("/student/parent") 
	 public Map<String,String> createParent(ParentDto dto){
		 Map<String,String> resultMap = new HashMap<String,String>();
		 resultMap.put("res_code", "404");
		 resultMap.put("res_msg", "가족 사항 등록 중 오류가 발생했습니다.");
		 if(studentService.createParentInfo(dto) != null) {
			 resultMap.put("res_code","200");
			 resultMap.put("res_msg", "가족 사항 등록이 성공적으로 수행되었습니다.");
			 }
		 return resultMap;
		 }
	
	 // 과목 정보 등록
	 @ResponseBody
	 @PostMapping("/subject")
	 public Map<String,String> createSubject(@RequestBody SubjectDetailsDto detailsDto){
		 Map<String,String> resultMap = new HashMap<String,String>();
		 resultMap.put("res_code", "404");
		 resultMap.put("res_msg", "과목 정보 등록 중 오류가 발생했습니다");
		 try {
			 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		        User user = (User) authentication.getPrincipal();  // User 타입으로 캐스팅
		        String teacherNo = user.getUsername();  // 여기서 getUsername()으로 teacher_no 추출
		        
		        TeacherHistory teacherHistory = teacherHistoryRepository.selectLatestTeacherHistoryByEmployee(teacherNo);
		        if (teacherHistory == null) {
		            resultMap.put("res_code", "404");
		            resultMap.put("res_msg", "해당 교사의 이력이 존재하지 않습니다.");
		            return resultMap;
		        }
		     detailsDto.getSdto().setTeacher_history(teacherHistory);  // SubjectDto에 TeacherHistory 설정
			 studentService.saveSubjectWithDetail(detailsDto.getSdto(),detailsDto.getCdtoList(),detailsDto.getTdtoList());
			 resultMap.put("res_code", "200");
		     resultMap.put("res_msg", "과목 정보가 성공적으로 등록되었습니다.");
		 }catch(Exception e) {
			 e.printStackTrace();
			 resultMap.put("res_code", "500");
			 resultMap.put("res_msg", "과목 정보 등록 중 오류가 발생했습니다.");
		 }
		return resultMap;
	 }
	 
	// 과목(과목 정보, 시간표 정보, 교육과정 정보) 전부 삭제 처리
		@ResponseBody
		@DeleteMapping("/subject/{subject_no}")
		public Map<String,String> deleteSubject(@PathVariable("subject_no") Long subject_no){
			Map<String,String> map = new HashMap<String,String>();
			map.put("res_code", "404");
			map.put("res_msg", "과목 정보 삭제 중 오류가 발생했습니다");
			
			if(studentService.deleteSubject(subject_no) > 0) {
				map.put("res_code", "200");
				map.put("res_msg","정상적으로 과목 정보가 삭제되었습니다.");
				
			}
			return map;
		}
		
		// 성적 등록, 업데이트, 삭제 처리
		@ResponseBody
		@PostMapping("/score")
		public Map<String, String> createScore(@RequestBody List<ScoreDto> scoreDtoList) {
		    Map<String, String> resultMap = new HashMap<>();
		    resultMap.put("res_code", "404");
		    resultMap.put("res_msg", "성적 입력 중 오류가 발생했습니다.");

		    List<ScoreDto> newScores = new ArrayList<>();  // 새로 추가할 성적 리스트
		    List<ScoreDto> updatedScores = new ArrayList<>();  // 업데이트할 성적 리스트
		    List<ScoreDto> deleteScores = new ArrayList<>();  // 삭제할 성적 리스트

		    // 중복 성적 체크 및 분류
		    for (ScoreDto dto : scoreDtoList) {
		        // student_no와 curriculum_no로 기존 성적을 확인
		        Optional<Score> existingScoreOpt = scoreRepository.findByStudent_StudentNoAndCurriculum_CurriculumNo(
		                dto.getStudent_no(), dto.getCurriculum_no());

		        if (existingScoreOpt.isPresent()) {
		            Score existingScore = existingScoreOpt.get();
		            if (dto.getScore().trim().isEmpty()) {
		                // 빈 값이 들어오면 성적을 삭제할 리스트에 추가
		                deleteScores.add(dto);
		            } else {
		                // 기존 성적이 존재하는 경우 점수 업데이트
		                existingScore.setScore(dto.getScore());  // 새로운 점수로 업데이트
		                updatedScores.add(dto);
		            }
		        } else {
		            if (!dto.getScore().trim().isEmpty()) {
		                // 중복되지 않은 경우 빈 값이 아니면 새로 추가할 성적 리스트에 추가
		                newScores.add(dto);
		            }
		        }
		    }

		    // 새로 추가할 성적 저장
		    if (!newScores.isEmpty() && studentService.registScore(newScores) != null) {
		        resultMap.put("res_code", "200");
		        resultMap.put("res_msg", "새로운 성적이 성공적으로 추가되었습니다.");
		    }

		    // 업데이트할 성적 처리
		    if (!updatedScores.isEmpty() && studentService.updateScore(updatedScores) != null) {
		        resultMap.put("res_code", "200");
		        resultMap.put("res_msg", "기존 성적이 성공적으로 업데이트되었습니다.");
		    }

		    // 삭제할 성적 처리 및 결과 확인
		    int deleteResult = studentService.deleteScores(deleteScores);
		    if (deleteResult > 0) {
		        resultMap.put("res_code", "200");
		        resultMap.put("res_msg", "기존 성적이 성공적으로 삭제되었습니다.");
		    }

		    if (newScores.isEmpty() && updatedScores.isEmpty() && deleteScores.isEmpty()) {
		        resultMap.put("res_code", "300");
		        resultMap.put("res_msg", "변경사항이 없어 저장되지 않았습니다.");
		    }

		    return resultMap;
		}





		
}
