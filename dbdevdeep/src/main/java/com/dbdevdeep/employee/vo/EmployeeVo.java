package com.dbdevdeep.employee.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.dbdevdeep.employee.domain.Employee;

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
public class EmployeeVo {

	private String emp_id;
	private String emp_pw;
	private String gov_id;
	private String emp_name;
	private String emp_rrn;
	private String emp_phone;
	private String ori_pic_name;
	private String new_pic_name;
	private String emp_post_code;
	private String emp_addr;
	private String emp_detail_addr;

	private String dept_code;
	private String job_code;

	private String emp_internal_phone;
	private int vacation_hour;
	private LocalDate hire_date;
	private LocalDate end_date;
	private String ent_status;
	private String login_yn;
	private int account_status;
	private String chat_status_msg;
	
	private LocalDateTime account_time;

	public EmployeeVo EntityToVo(Employee employee) {
		return EmployeeVo.builder().emp_id(employee.getEmpId()).emp_pw(employee.getEmpPw()).gov_id(employee.getGovId())
				.emp_name(employee.getEmpName()).emp_rrn(employee.getEmpRrn()).emp_phone(employee.getEmpPhone())
				.ori_pic_name(employee.getOriPicName()).new_pic_name(employee.getNewPicName())
				.emp_post_code(employee.getEmpPostCode()).emp_addr(employee.getEmpAddr())
				.emp_detail_addr(employee.getEmpDetailAddr()).emp_internal_phone(employee.getEmpInternalPhone())
				.vacation_hour(employee.getVacationHour()).hire_date(employee.getHireDate()).end_date(employee.getEndDate())
				.ent_status(employee.getEntStatus()).login_yn(employee.getLoginYn()).account_status(employee.getAccountStatus())
				.chat_status_msg(employee.getChatStatusMsg()).job_code(employee.getJob().getJobCode()).account_time(employee.getAccountTime())
				.dept_code(employee.getDepartment().getDeptCode()).build();
	}
}
