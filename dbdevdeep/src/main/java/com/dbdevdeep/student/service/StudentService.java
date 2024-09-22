package com.dbdevdeep.student.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.employee.repository.TeacherHistoryRepository;
import com.dbdevdeep.student.domain.Curriculum;
import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.Parent;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.Student;
import com.dbdevdeep.student.domain.StudentClass;
import com.dbdevdeep.student.domain.StudentClassDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.Subject;
import com.dbdevdeep.student.domain.SubjectDto;
import com.dbdevdeep.student.domain.TimeTable;
import com.dbdevdeep.student.domain.TimeTableDto;
import com.dbdevdeep.student.repository.CurriculumRepository;
import com.dbdevdeep.student.repository.ParentRepository;
import com.dbdevdeep.student.repository.StudentClassRepository;
import com.dbdevdeep.student.repository.StudentRepository;
import com.dbdevdeep.student.repository.SubjectRepository;
import com.dbdevdeep.student.repository.TimeTableRepository;

import jakarta.transaction.Transactional;

@Service
public class StudentService {
	
	// 의존성 주입
	private final StudentRepository studentRepository;
	private final TeacherHistoryRepository teacherHistoryRepository;
	private final StudentClassRepository studentClassRepository;
	private final ParentRepository parentRepository;
	private final SubjectRepository subjectRepository;
	private final CurriculumRepository curriculumRepository;
	private final TimeTableRepository timeTableRepository;
	
	@Autowired
	public StudentService(StudentRepository studentRepository, TeacherHistoryRepository teacherHistoryRepository, StudentClassRepository studentClassRepository, ParentRepository parentRepository,SubjectRepository subjectRepository,CurriculumRepository curriculumRepository, TimeTableRepository timeTableRepository) {
		this.studentRepository = studentRepository;
		this.teacherHistoryRepository = teacherHistoryRepository;
		this.studentClassRepository = studentClassRepository;
		this.parentRepository = parentRepository;
		this.subjectRepository = subjectRepository;
		this.curriculumRepository = curriculumRepository;
		this.timeTableRepository = timeTableRepository;
	}
	
	// 입력 form에서 받아온 dto data를 Student로 바꿔서 저장하는 절차
	public Student createStudent(StudentDto dto) {
		Student student = Student.builder()
				.studentName(dto.getStudent_name())
				.studentBirth(dto.getStudent_birth())
				.studentGender(dto.getStudent_gender())
				.studentPostCode(dto.getStudent_post_code())
				.studentAddr(dto.getStudent_addr())
				.studentDetailAddr(dto.getStudent_detail_addr())
				.studentPhone(dto.getStudent_phone())
				.studentOriPic(dto.getStudent_ori_pic())
				.studentNewPic(dto.getStudent_new_pic())
				.studentStatus(dto.getStudent_status())
				.build();
		return studentRepository.save(student);
	}
	
	// 학생리스트를 옮겨주기 위해 dto로 변환하여 담아주는 절차
	public List<StudentClassDto> selectStudentList(StudentClassDto studentClassDto){
	    List<Object[]> studentClassList = studentClassRepository.findRecentYearAll();
	    List<StudentClassDto> studentClassDtoList = new ArrayList<>();
	    
	    for(Object[] entry : studentClassList) {
	        Student student = (Student) entry[0];
	        StudentClass studentClass = (StudentClass) entry[1];  // 반 배정이 안된 학생일 경우 null일 수 있음
	        
	        // DTO 변환
	        StudentClassDto dto = new StudentClassDto();
	        dto.setStudent_no(student.getStudentNo());
	        dto.setStudent(student);
	        dto.setStudent_id(studentClass != null ? studentClass.getStudentId() : null);
	        if (studentClass != null) {
	            dto.setTeacher_history(studentClass.getTeacherHistory()); // teacher_history 설정
	        } else {
	            dto.setTeacher_history(null);  // 반 배정되지 않은 경우
	        }
	        
	        studentClassDtoList.add(dto);
	    }
	    return studentClassDtoList;
	}
	
	// 학생번호를 통해 선택한 학생의 정보를 dto로 변환하여 담아주는 절차
	public StudentDto selectStudentOne(Long student_no) {
		Student student = studentRepository.findBystudentNo(student_no);
		StudentDto dto = StudentDto.builder()
				.student_no(student.getStudentNo())
				.student_name(student.getStudentName())
				.student_birth(student.getStudentBirth())
				.student_gender(student.getStudentGender())
				.student_post_code(student.getStudentPostCode())
				.student_addr(student.getStudentAddr())
				.student_detail_addr(student.getStudentDetailAddr())
				.student_phone(student.getStudentPhone())
				.student_ori_pic(student.getStudentOriPic())
				.student_new_pic(student.getStudentNewPic())
				.student_status(student.getStudentStatus())
				.build();
			return dto;
	}
	
	// 수정 정보를 담아 entity로 변환 후에 DB에 저장하는 과정
	@Transactional
	public Student updateStudentInfo(StudentDto dto) {
		StudentDto temp = selectStudentOne(dto.getStudent_no());
		temp.setStudent_name(dto.getStudent_name());
		temp.setStudent_birth(dto.getStudent_birth());
		temp.setStudent_gender(dto.getStudent_gender());
		temp.setStudent_post_code(dto.getStudent_post_code());
		temp.setStudent_addr(dto.getStudent_addr());
		temp.setStudent_detail_addr(dto.getStudent_detail_addr());
		temp.setStudent_phone(dto.getStudent_phone());
		temp.setStudent_ori_pic(dto.getStudent_ori_pic());
		temp.setStudent_new_pic(dto.getStudent_new_pic());
		temp.setStudent_status(dto.getStudent_status());
		if(dto.getStudent_ori_pic() != null
				&& "".equals(dto.getStudent_ori_pic()) == false) {
			temp.setStudent_ori_pic(dto.getStudent_ori_pic());
			temp.setStudent_new_pic(dto.getStudent_new_pic());
		}
		Student std = temp.toEntity();
		Student result = studentRepository.save(std);
		return result;
	}
	
	// 학생 정보 삭제
	public int deleteStudent(Long student_no) {
		int result = 0;
		try {
			studentRepository.deleteById(student_no);
			result = 1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 부모 정보 삭제
	public int deleteParent(Long parent_no) {
		int result = 0;
		try {
			parentRepository.deleteById(parent_no);
			result = 1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 반배정
	public StudentClass assignClass(StudentClassDto dto) {
			Student sdt = studentRepository.findBystudentNo(dto.getStudent_no());
			TeacherHistory th = teacherHistoryRepository.findByteacherNo(dto.getTeacher_no());
			StudentClass sc= StudentClass.builder()
					.student(sdt)
					.teacherHistory(th)
					.studentId(dto.getStudent_id())
					.build();
			
			return studentClassRepository.save(sc);
	}
	
	// 반배정 이력 조회
	public List<StudentClassDto> selectStudentClassList(Long student_no){
		List<StudentClass> studentClassList = studentClassRepository.findByStudent_StudentNo(student_no);
		List<StudentClassDto> studentClassDtoList = new ArrayList<StudentClassDto>();
		for(StudentClass sc : studentClassList) {
			StudentClassDto dto = new StudentClassDto().toDto(sc);
			studentClassDtoList.add(dto);
		}
		return studentClassDtoList;
	}
	
	// 반배정 이력 삭제
	public int deleteStudentClass(Long class_no) {
		int result = 0;
		try {
			studentClassRepository.deleteById(class_no);
			result = 1;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	// 학생의 부모 리스트 조회
	public List<ParentDto> selectStudentParentList(Long student_no) {
		List<Parent> parentList = parentRepository.findByStudent_StudentNo(student_no);
		List<ParentDto> parentDtoList = new ArrayList<ParentDto>();
		for(Parent p : parentList) {
			ParentDto dto = new ParentDto().toDto(p);
			parentDtoList.add(dto);
		}
		return parentDtoList;
	}
	
	
	 // 학부모 등록 
	public Parent createParentInfo(ParentDto dto) { 
		Student sdt = studentRepository.findBystudentNo(dto.getStudent_no());
		Parent parent = Parent.builder() 
				.parentNo(dto.getParent_no())
				.parentName(dto.getParent_name())
				.student(sdt)
				.parentPhone(dto.getParent_phone()) 
				.parentBirth(dto.getParent_birth())
				.parentRelation(dto.getParent_relation())
				.build();
		
		return parentRepository.save(parent); 
	 }
	
	// 과목 리스트 페이지에서 목록 조회
	public List<SubjectDto> mySubjectList(){
		List<Subject> subjectList = subjectRepository.findAll();
		List<SubjectDto> subjectDtoList = new ArrayList<SubjectDto>();
		for(Subject s : subjectList) {
			SubjectDto dto = new SubjectDto().toDto(s);
			subjectDtoList.add(dto);
		}
		return subjectDtoList;
	}
	
	// 과목 정보 상세 조회시 과목 정보 조회
		public SubjectDto selectSubjectOne(Long subject_no) {
			Subject subjectDetail = subjectRepository.findBySubjectNo(subject_no);
			SubjectDto dto = new SubjectDto().toDto(subjectDetail);
			return dto;
		}
	// 과목 정보 상세 조회 시 교육과정 조회
		public List<CurriculumDto> selectCurriOne(Long subject_no) {
			List<Curriculum> curriDetail = curriculumRepository.findBySubject_SubjectNo(subject_no);
			List<CurriculumDto> curriDetaildto = new ArrayList<CurriculumDto>();
			for(Curriculum c : curriDetail) {
				CurriculumDto dto = new CurriculumDto().toDto(c);
				curriDetaildto.add(dto);
			}
			return curriDetaildto;
		}
		
	// 과목 정보 상세 조회 시 시간표 조회
		public List<TimeTableDto> selectTimeTableOne(Long subject_no){
			List<TimeTable> timeDetail = timeTableRepository.findBySubject_SubjectNo(subject_no);
			List<TimeTableDto> timeDetaildto = new ArrayList<TimeTableDto>();
			for(TimeTable t : timeDetail) {
				TimeTableDto dto = new TimeTableDto().toDto(t);
				timeDetaildto.add(dto);
			}
			return timeDetaildto;
		}
	
	// 과목 정보 등록 시에 과목 정보, 시간표 정보, 평가 과정 정보 동시에 저장
		@Transactional
		public Map<String,String> saveSubjectWithDetail(SubjectDto sdto, List<CurriculumDto> cdtoList, List<TimeTableDto> tdtoList){
			Map<String, String> resultMap = new HashMap<>();
			try {
	            // 1. Subject 저장
	            Subject subject = sdto.toEntity();
	            subjectRepository.save(subject);

	            // 2. Curriculum 저장
	            if(cdtoList != null && !cdtoList.isEmpty()){
		            for (CurriculumDto cdto : cdtoList) {
		            	if (cdto.getCurriculum_content() != null && !cdto.getCurriculum_content().trim().isEmpty() &&
		                        cdto.getCurriculum_ratio() != null && cdto.getCurriculum_max_score() != null) {

		                        Curriculum curriculum = cdto.toEntity();
		                        curriculum.setSubject(subject);
		                        curriculumRepository.save(curriculum);
		                    }
		            }
	            }
	            // 3. TimeTable 저장
	            if (tdtoList != null && !tdtoList.isEmpty()) {
	                for (TimeTableDto tdto : tdtoList) {
	                    if (tdto.getDay() != null && !tdto.getDay().trim().isEmpty() && tdto.getPeriod() != null) {

	                        TimeTable timeTable = tdto.toEntity();
	                        timeTable.setSubject(subject);
	                        timeTableRepository.save(timeTable);
	                    }
	                }
	            }

	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "과목이 성공적으로 등록되었습니다.");

	        } catch (Exception e) {
	            // 에러 발생 시 오류 메시지와 코드 반환
	            resultMap.put("res_code", "500");
	            resultMap.put("res_msg", "과목 등록 중 오류가 발생했습니다.");
	            e.printStackTrace();
	        }

	        return resultMap;
		}
		
		// 과목 정보 전부 삭제
		public int deleteSubject(Long subject_no) {
			int result = 0;
			try {
				subjectRepository.deleteById(subject_no);
				curriculumRepository.deleteById(subject_no);
				timeTableRepository.deleteById(subject_no);
				result = 1;
			}catch(Exception e) {
				e.printStackTrace();
			}
			return result;
		}
}
