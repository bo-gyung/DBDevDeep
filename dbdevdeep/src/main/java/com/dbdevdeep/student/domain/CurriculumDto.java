package com.dbdevdeep.student.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class CurriculumDto {
	private Long curriculum_no;
	
	private Subject subject;
	
	private String curriculum_content;
	private String curriculum_ratio;
	private String curriculum_max_score;
	
	public Curriculum toEntity() {
		return Curriculum.builder()
				.curriculumNo(curriculum_no)
				.curriculumContent(curriculum_content)
				.curriculumRatio(curriculum_ratio)
				.curriculumMaxScore(curriculum_max_score)
				.build();
	}
	
	public CurriculumDto toDto(Curriculum curri) {
		return CurriculumDto.builder()
				.curriculum_no(curri.getCurriculumNo())
				.curriculum_content(curri.getCurriculumContent())
				.curriculum_ratio(curri.getCurriculumRatio())
				.curriculum_max_score(curri.getCurriculumMaxScore())
				.subject(curri.getSubject())
				.build();
	}
	
}
