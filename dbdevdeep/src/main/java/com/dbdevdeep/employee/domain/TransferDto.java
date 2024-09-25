package com.dbdevdeep.employee.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
	
	private String admin_id; // 기록자
	private String admin_name; //
	
	private LocalDateTime log_time; // 기록일시
	
	public Transfer toEntity() {
		return Transfer.builder()
				.transNo(trans_no)
				.transDate(trans_date)
				.transSchoolId(trans_school_id)
				.transType(trans_type)
				.logTime(log_time)
				.build();
	}
	
	public Transfer toEntityWithJoin(Employee employee, Employee admin) {
		return Transfer.builder()
				.employee(employee)
				.transNo(trans_no)
				.transDate(trans_date)
				.transSchoolId(trans_school_id)
				.transType(trans_type)
				.admin(admin)
				.logTime(log_time)
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
				.admin_id(t.getAdmin().getEmpId())
				.admin_name(t.getAdmin().getEmpName())
				.log_time(t.getLogTime())
				.build();
	}

}
