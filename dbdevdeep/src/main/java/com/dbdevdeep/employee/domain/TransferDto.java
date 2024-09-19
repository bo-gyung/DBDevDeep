package com.dbdevdeep.employee.domain;

import java.time.LocalDate;

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
public class TransferDto {
	
	public Long trans_no;
	
	private String emp_id;
	private String emp_name; 
	
	private LocalDate trans_date; // 전근일
	private String trans_school_id; // 이전/이후 학교 아이디
	private String trans_type; // 전입: F, 전출: T
	
	public Transfer toEntity() {
		return Transfer.builder()
				.transNo(trans_no)
				.transDate(trans_date)
				.transSchoolId(trans_school_id)
				.transType(trans_type)
				.build();
	}
	
	public Transfer toEntityWithJoin(Employee employee) {
		return Transfer.builder()
				.employee(employee)
				.transNo(trans_no)
				.transDate(trans_date)
				.transSchoolId(trans_school_id)
				.transType(trans_type)
				.build();
	}
	
	public TransferDto toDto(Transfer t) {
		return TransferDto.builder()
				.trans_no(t.getTransNo())
				.emp_id(t.getEmployee().getEmpId())
				.emp_name(t.getEmployee().getEmpId())
				.trans_date(t.getTransDate())
				.trans_school_id(t.getTransSchoolId())
				.trans_type(t.getTransType())
				.build();
	}

}
