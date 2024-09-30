package com.dbdevdeep.student.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import com.dbdevdeep.student.domain.CurriculumDto;
import com.dbdevdeep.student.domain.ParentDto;
import com.dbdevdeep.student.domain.ScoreDto;
import com.dbdevdeep.student.domain.StudentClassDto;
import com.dbdevdeep.student.domain.StudentDto;
import com.dbdevdeep.student.domain.SubjectDto;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PdfService {

    public byte[] createPdf(StudentDto studentDto, 
                            List<StudentClassDto> studentClassList, 
                            List<ParentDto> parentList,
                            List<SubjectDto> subjectList,
                            List<CurriculumDto> curriList,
                            List<ScoreDto> scoreList) throws Exception {
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(byteArrayOutputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // 폰트 설정 (맑은 고딕 폰트 경로 설정)
        String fontPath = new ClassPathResource("static/assets/libs/pdffont/MALGUN.TTF").getFile().getPath();
        PdfFont koreanFont = PdfFontFactory.createFont(fontPath, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        // 1. 학생 기본 정보 출력
        document.add(new Paragraph("학생 정보").setFont(koreanFont));
        document.add(new Paragraph("이름: " + studentDto.getStudent_name()).setFont(koreanFont));
        document.add(new Paragraph("생년월일: " + studentDto.getStudent_birth()).setFont(koreanFont));
        document.add(new Paragraph("주소: " + studentDto.getStudent_addr() + " " + studentDto.getStudent_detail_addr() + " (" + studentDto.getStudent_post_code() + ")").setFont(koreanFont));
        document.add(new Paragraph("전화번호: " + studentDto.getStudent_phone()).setFont(koreanFont));

        // 2. 학년 이력 정보 출력
        document.add(new Paragraph("\n학년 이력").setFont(koreanFont));
        float[] classColumnWidths = {1, 1, 1, 1, 1};
        Table classTable = new Table(UnitValue.createPercentArray(classColumnWidths));
        classTable.addHeaderCell("학년도").setFont(koreanFont);
        classTable.addHeaderCell("학년").setFont(koreanFont);
        classTable.addHeaderCell("반").setFont(koreanFont);
        classTable.addHeaderCell("번호").setFont(koreanFont);
        classTable.addHeaderCell("담임 성명").setFont(koreanFont);

        for (StudentClassDto studentClass : studentClassList) {
            classTable.addCell(studentClass.getTeacher_history().getTYear() + "년").setFont(koreanFont);
            classTable.addCell(studentClass.getTeacher_history().getGrade() + "학년").setFont(koreanFont);
            classTable.addCell(studentClass.getTeacher_history().getGradeClass() + "반").setFont(koreanFont);
            classTable.addCell(studentClass.getStudent_id() + "번").setFont(koreanFont);
            classTable.addCell(studentClass.getTeacher_history().getEmployee().getEmpName()).setFont(koreanFont);
        }

        document.add(classTable);

        // 3. 가족 정보 출력
        document.add(new Paragraph("\n가족사항").setFont(koreanFont));
        float[] familyColumnWidths = {2, 2, 4, 3};
        Table familyTable = new Table(UnitValue.createPercentArray(familyColumnWidths));
        familyTable.addHeaderCell("관계").setFont(koreanFont);
        familyTable.addHeaderCell("성명").setFont(koreanFont);
        familyTable.addHeaderCell("연락처").setFont(koreanFont);
        familyTable.addHeaderCell("생년월일").setFont(koreanFont);

        for (ParentDto parent : parentList) {
            familyTable.addCell(parent.getParent_relation()).setFont(koreanFont);
            familyTable.addCell(parent.getParent_name()).setFont(koreanFont);
            familyTable.addCell(parent.getParent_phone()).setFont(koreanFont);
            familyTable.addCell(parent.getParent_birth()).setFont(koreanFont);
        }

        document.add(familyTable);

        // 4. 과목 및 성적 정보 출력
        document.add(new Paragraph("\n과목 및 성적").setFont(koreanFont));
        
        float[] subjectColumnWidths = {1, 3, 1, 1, 1};
        
        Table subjectTable = new Table(UnitValue.createPercentArray(subjectColumnWidths));
        
        subjectTable.addHeaderCell("과목").setFont(koreanFont);
        subjectTable.addHeaderCell("교육과정 내용").setFont(koreanFont);
        subjectTable.addHeaderCell("반영비율(%)").setFont(koreanFont);
        subjectTable.addHeaderCell("과목만점").setFont(koreanFont);
        subjectTable.addHeaderCell("점수").setFont(koreanFont);

        for (SubjectDto subject : subjectList) {
            // 과목 번호로 해당하는 교육과정 필터링
            Optional<CurriculumDto> matchingCurri = curriList.stream()
                .filter(curri -> curri.getSubject().getSubjectNo().equals(subject.getSubject_no()))
                .findFirst();

            // 과목명 출력
            subjectTable.addCell(subject.getSubject_name()).setFont(koreanFont);
            
            // 교육과정 내용 출력 (존재하면 해당 값 출력, 없으면 공백 출력)
            if (matchingCurri.isPresent()) {
                CurriculumDto curri = matchingCurri.get();
                subjectTable.addCell(curri.getCurriculum_content()).setFont(koreanFont);
                subjectTable.addCell(curri.getCurriculum_ratio()).setFont(koreanFont);
                subjectTable.addCell(curri.getCurriculum_max_score()).setFont(koreanFont);

                // 학생 성적 추가 (해당 curriculum_no가 있는지 확인 후 성적 출력)
                String score = scoreList.stream()
                    .filter(s -> s.getCurriculum_no().equals(curri.getCurriculum_no()))
                    .map(ScoreDto::getScore)
                    .findFirst().orElse("-");
                subjectTable.addCell(score).setFont(koreanFont);
            } else {
                // 교육과정 정보가 없는 경우 빈 셀 추가
                subjectTable.addCell("-").setFont(koreanFont);
                subjectTable.addCell("-").setFont(koreanFont);
                subjectTable.addCell("-").setFont(koreanFont);
                subjectTable.addCell("-").setFont(koreanFont); // 성적 빈칸
            }
        }

        document.add(subjectTable);

        // PDF 완료
        document.close();

        // 바이트 배열 반환
        return byteArrayOutputStream.toByteArray();
    }
}
