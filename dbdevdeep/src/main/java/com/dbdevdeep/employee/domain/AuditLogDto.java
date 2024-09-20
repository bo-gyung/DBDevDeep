package com.dbdevdeep.employee.domain;

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
public class AuditLogDto {

	private Long audit_no;
	
	private String emp_id;
	private String emp_name;
	
	private String admin_id;
	private String admin_name;
	
	private String audit_type;
	private LocalDateTime reg_time;
	private String changed_item;
	private String ori_data;
	private String new_data;
	
	public AuditLog toEntity() {
		return AuditLog.builder()
				.auditNo(audit_no)
				.regTime(reg_time)
				.audit_type(audit_type)
				.changedItem(changed_item)
				.oriData(ori_data)
				.newData(new_data)
				.build();
	}
	
	public AuditLog toEntityWithJoin(Employee employee, Employee admin) {
		return AuditLog.builder()
				.employee(employee)
				.admin(admin)
				.auditNo(audit_no)
				.regTime(reg_time)
				.audit_type(audit_type)
				.changedItem(changed_item)
				.oriData(ori_data)
				.newData(new_data)
				.build();
	}
	
	public AuditLogDto toDto(AuditLog al) {
		return AuditLogDto.builder()
				.audit_no(al.getAuditNo())
				.emp_id(al.getEmployee().getEmpId())
				.emp_name(al.getEmployee().getEmpName())
				.admin_id(al.getAdmin().getEmpId())
				.admin_name(al.getAdmin().getEmpName())
				.audit_type(al.getAudit_type())
				.reg_time(al.getRegTime())
				.changed_item(al.getChangedItem())
				.ori_data(al.getOriData())
				.new_data(al.getNewData())
				.build();
	}
}
