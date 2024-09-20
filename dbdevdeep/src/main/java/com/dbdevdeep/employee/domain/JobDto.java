package com.dbdevdeep.employee.domain;

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
public class JobDto {

	private String job_code;
	private String job_name;
	
	public Job toEntity() {
		return Job.builder()
				.jobCode(job_code).jobName(job_name).build();
	}
	
	public JobDto toDto(Job job) {
		return JobDto.builder()
				.job_code(job.getJobCode()).job_name(job.getJobName()).build();
	}
}
