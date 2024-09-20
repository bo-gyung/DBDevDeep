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
public class EmployeeStatusDto {

	private Long status_no;
	
	private String emp_id;
	private String emp_name;
	
	private String stop_reason;
	private LocalDate stop_date;
	private LocalDate excepted_date;
	private LocalDate return_date;
	private String changed_item;
	private String status_type;
	
	private String admin_id;
	private String admin_name;
	
	public EmployeeStatus toEntityWithJoin(Employee employee, Employee admin) {
		return EmployeeStatus.builder()
				.employee(employee)
				.admin(admin)
				.statusNo(status_no)
				.stopReason(stop_reason)
				.stopDate(stop_date)
				.exceptedDate(excepted_date)
				.returnDate(return_date)
				.statusType(status_type)
				.build();
	}
	
	public EmployeeStatus toEntity() {
		return EmployeeStatus.builder()
				.statusNo(status_no)
				.stopReason(stop_reason)
				.stopDate(stop_date)
				.exceptedDate(excepted_date)
				.returnDate(return_date)
				.statusType(status_type)
				.build();
	}
	
	public EmployeeStatusDto toDto(EmployeeStatus es) {
		return EmployeeStatusDto.builder()
				.status_no(es.getStatusNo())
				.emp_id(es.getEmployee().getEmpId())
				.emp_name(es.getEmployee().getEmpName())
				.admin_id(es.getAdmin().getEmpId())
				.admin_name(es.getAdmin().getEmpName())
				.stop_reason(es.getStopReason())
				.stop_date(es.getStopDate())
				.excepted_date(es.getExceptedDate())
				.return_date(es.getReturnDate())
				.status_type(es.getStatusType())
				.build();
	}
}
