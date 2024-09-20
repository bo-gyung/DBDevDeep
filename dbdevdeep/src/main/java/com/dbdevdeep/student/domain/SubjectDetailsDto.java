package com.dbdevdeep.student.domain;

import java.util.List;

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
public class SubjectDetailsDto {
	private SubjectDto sdto;
    private List<CurriculumDto> cdtoList;
    private List<TimeTableDto> tdtoList;
}
