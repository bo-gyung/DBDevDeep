package com.dbdevdeep.student.service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

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
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PdfService {

    public byte[] createPdf(StudentDto studentDto, 
                            List<StudentClassDto> studentClassList, 
                            List<ParentDto> parentList,
                            List<SubjectDto> subjectList,
                            List<CurriculumDto> curriList,
                            List<ScoreDto> scoreList,
                            Map<Long,String> totalScoreMap) throws Exception {
        
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
        
        if (studentClassList.isEmpty()) {
            Cell classmergedCell = new Cell(1, 5)  // 1행 5열 병합
                .add(new Paragraph("학급이력 정보가 없습니다.").setFont(koreanFont));
            classTable.addCell(classmergedCell); // 병합된 셀 추가
        } else { 
            for (StudentClassDto studentClass : studentClassList) {
                classTable.addCell(studentClass.getTeacher_history().getTYear() + "년").setFont(koreanFont);
                classTable.addCell(studentClass.getTeacher_history().getGrade() + "학년").setFont(koreanFont);
                classTable.addCell(studentClass.getTeacher_history().getGradeClass() + "반").setFont(koreanFont);
                classTable.addCell(studentClass.getStudent_id() + "번").setFont(koreanFont);
                classTable.addCell(studentClass.getTeacher_history().getEmployee().getEmpName()).setFont(koreanFont);
            }
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

        if (parentList.isEmpty()) {
            // 부모 정보가 없을 때, 셀을 병합하여 메시지 출력
            Cell mergedCell = new Cell(1, 4)  // 1행 4열 병합
                .add(new Paragraph("부모 정보가 없습니다.").setFont(koreanFont))
                .setTextAlignment(TextAlignment.CENTER);  // 수정된 부분
            familyTable.addCell(mergedCell); // 병합된 셀 추가
        } else {
            for (ParentDto parent : parentList) {
                familyTable.addCell(parent.getParent_relation()).setFont(koreanFont);
                familyTable.addCell(parent.getParent_name()).setFont(koreanFont);
                familyTable.addCell(parent.getParent_phone()).setFont(koreanFont);
                familyTable.addCell(parent.getParent_birth()).setFont(koreanFont);
            }
        }

        document.add(familyTable);

        // 4. 과목 및 성적 정보 출력
        document.add(new Paragraph("\n과목 및 성적").setFont(koreanFont));

        for (SubjectDto subject : subjectList) {
            // 해당 과목의 교육과정들을 필터링
            List<CurriculumDto> matchingCurris = curriList.stream()
                .filter(curri -> curri.getSubject().getSubjectNo().equals(subject.getSubject_no()))
                .toList();

            // 과목명을 제목으로 추가
            document.add(new Paragraph(subject.getTeacher_history().getTYear() + "년도 " + subject.getSubject_semester() + "학기").setFont(koreanFont));

            document.add(new Paragraph("\n과목: " + subject.getSubject_name()).setFont(koreanFont));

         // 테이블 생성
            float[] subjectColumnWidths;
            Table subjectTable;

            // 만약 매칭되는 교육과정이 없는 경우 "등록된 교육과정이 없습니다" 메시지를 테이블에 추가
            if (matchingCurris.isEmpty()) {
                subjectColumnWidths = new float[]{1}; // 하나의 셀만 필요하므로 너비 배열은 1개 요소
                subjectTable = new Table(UnitValue.createPercentArray(subjectColumnWidths));

                subjectTable.addCell(new Cell()
                    .add(new Paragraph("등록된 교육과정이 없습니다.").setFont(koreanFont)) // 수정된 부분
                );
            } else {
                // 매칭된 교육과정이 있을 때 테이블 생성
                subjectColumnWidths = new float[matchingCurris.size() + 1];  // 교육과정 수 + 1 (총 점수)
                for (int i = 0; i < matchingCurris.size(); i++) {
                    subjectColumnWidths[i] = 1;  // 각 컬럼 너비를 동일하게 설정
                }
                subjectColumnWidths[matchingCurris.size()] = 1;  // 마지막 칸도 동일한 너비

                subjectTable = new Table(UnitValue.createPercentArray(subjectColumnWidths));

                // 교육과정 이름들을 테이블 헤더로 추가
                for (CurriculumDto curri : matchingCurris) {
                    subjectTable.addHeaderCell(new Cell().add(new Paragraph(curri.getCurriculum_content()).setFont(koreanFont)));
                }
                subjectTable.addHeaderCell(new Cell().add(new Paragraph("총 점수").setFont(koreanFont)));  // "총 점수" 헤더 추가

                // 각 교육과정에 대해 점수 데이터를 테이블에 추가
                for (CurriculumDto curri : matchingCurris) {
                    // 학생 성적 추가 (해당 curriculum_no가 있는지 확인 후 성적 출력)
                    String score = scoreList.stream()
                        .filter(s -> s.getCurriculum_no().equals(curri.getCurriculum_no()))
                        .map(ScoreDto::getScore)
                        .findFirst()
                        .orElse("-");  // 점수가 없으면 "-"

                    subjectTable.addCell(new Cell().add(new Paragraph(score).setFont(koreanFont)));
                }

                // 해당 과목의 총 점수를 totalScoreMap에서 가져와 마지막 셀에 추가
                String totalScore = totalScoreMap.get(subject.getSubject_no());
                subjectTable.addCell(new Cell().add(new Paragraph(totalScore != null ? totalScore : "-").setFont(koreanFont)));  // 총점이 없으면 "-"
            }

            // 테이블 추가
            document.add(subjectTable);
        }

        // PDF 완료
        document.close();

        // 바이트 배열 반환
        return byteArrayOutputStream.toByteArray();
    }
}