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
public class ScoreDto {
	private Long score_no;
	private Long curriculum_no;
	private Long student_no;
	
	private String score;
	
	public Score toEntity() {
		return Score.builder()
				.scoreNo(score_no)
				.score(score)
				.build();
	}
	
	public ScoreDto toDto(Score score) {
		return ScoreDto.builder()
				.score_no(score.getScoreNo())
				.score(score.getScore())
				.student_no(score.getStudent().getStudentNo())
				.curriculum_no(score.getCurriculum().getCurriculumNo())
				.build();
	}
	
}
