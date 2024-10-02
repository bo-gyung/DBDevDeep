package com.dbdevdeep.student.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.ScoreDto;
import com.dbdevdeep.student.domain.StudentClassDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.SubjectDto;
import com.dbdevdeep.student.service.PdfService;
import com.dbdevdeep.student.service.StudentService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PdfDownloadController {

    private final StudentService studentService;
    private final PdfService pdfService;

    @Autowired
    public PdfDownloadController(StudentService studentService, PdfService pdfService) {
        this.studentService = studentService;
        this.pdfService = pdfService;
    }

    @GetMapping("/student/pdf/{student_no}")
    public void generatePdf(@PathVariable("student_no") Long student_no, HttpServletResponse response) throws Exception {
        // 학생 정보와 학년 이력 정보 조회
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
	    
        // PDF 생성
        byte[] pdfBytes = pdfService.createPdf(dto, studentClassResultList,resultList,subjectList,curriList,scoreList,totalScoreMap);

        // PDF 응답 설정
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=student_" + student_no + ".pdf");
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
