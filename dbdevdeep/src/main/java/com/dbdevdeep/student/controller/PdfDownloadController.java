package com.dbdevdeep.student.controller;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.ScoreDto;
import com.dbdevdeep.student.domain.StudentClassDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.SubjectDto;
import com.dbdevdeep.student.service.StudentService;
import com.itextpdf.html2pdf.HtmlConverter;

@Controller
public class PdfDownloadController {
	
	private final StudentService studentService;
	
	@Autowired
	public PdfDownloadController(StudentService studentService) {
		this.studentService = studentService;
	}
	
	@GetMapping("/pdf/download/{student_no}")
    public ResponseEntity<Resource> generatePdf(Model model, 
                @PathVariable("student_no") Long student_no) throws Exception {
        
        // 데이터 조회
        StudentDto dto = studentService.selectStudentOne(student_no);
        List<StudentClassDto> studentClassResultList= studentService.selectStudentClassList(student_no);
        List<ParentDto> resultList = studentService.selectStudentParentList(student_no);
        List<SubjectDto> subjectList = studentService.mySubjectList();
        List<CurriculumDto> curriList = studentService.selectCurriAll();
        List<ScoreDto> scoreList = studentService.selectScoreByStudent(student_no);

        Map<Long, String> scoreMap = new HashMap<>();
        for (ScoreDto score : scoreList) {
            scoreMap.put(score.getCurriculum_no(), score.getScore());
        }

        // 모델에 데이터 추가
        model.addAttribute("scoreList", scoreMap);
        model.addAttribute("subjectList", subjectList);
        model.addAttribute("curriList", curriList);
        model.addAttribute("dto", dto);
        model.addAttribute("pdto", resultList);
        model.addAttribute("cdto", studentClassResultList);

        // HTML 콘텐츠 생성
        String htmlContent = generateHtmlContent(model);

        // iText를 이용해 HTML을 PDF로 변환
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, pdfStream);

        // PDF 파일을 리소스로 만들어서 응답으로 반환
        ByteArrayResource resource = new ByteArrayResource(pdfStream.toByteArray());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_record_" + student_no + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private String generateHtmlContent(Model model) {
        // 모델 데이터를 바탕으로 HTML을 생성
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; }");
        html.append(".title { color: blue; }");
        html.append(".student-info { margin: 20px 0; }");
        html.append(".student-info p { margin: 0; padding: 5px; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        
        html.append("<h1 class='title'>학생 기록</h1>");
        
        // 학생 정보 추가 (예시)
        html.append("<div class='student-info'>");
        html.append("<p>이름: 홍길동</p>");
        html.append("<p>생년월일: 1990-01-01</p>");
        html.append("<p>주소: 서울시 종로구</p>");
        html.append("<p>전화번호: 010-1234-5678</p>");
        html.append("</div>");
        
        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}
