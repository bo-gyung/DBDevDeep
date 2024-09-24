package com.dbdevdeep.alert.domain;

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
public class AlertDto {

	public Long alarm_no;
	
	public String emp_id;
	public String emp_name;
	
	public String reference_name;
	public Long reference_no;
	public String alarm_content;
	public String read_yn;
	public LocalDateTime alarm_time;
	
	public Alert toEntity() {
		return Alert.builder()
				.alarmNo(alarm_no)
				.referenceName(reference_name)
				.referenceNo(reference_no)
				.alarmContent(alarm_content)
				.readYn(read_yn)
				.alarmTime(alarm_time)
				.build();
	}
	
	public Alert toEntity(Employee e) {
		return Alert.builder()
				.alarmNo(alarm_no)
				.employee(e)
				.referenceName(reference_name)
				.referenceNo(reference_no)
				.alarmContent(alarm_content)
				.readYn(read_yn)
				.alarmTime(alarm_time)
				.build();
	}
	
	public AlertDto toDto(Alert a) {
		return AlertDto.builder()
				.alarm_no(a.getAlarmNo())
				.emp_id(a.getEmployee().getEmpId())
				.emp_name(a.getEmployee().getEmpName())
				.reference_name(a.getReferenceName())
				.reference_no(a.getReferenceNo())
				.alarm_content(a.getAlarmContent())
				.read_yn(a.getReadYn())
				.alarm_time(a.getAlarmTime())
				.build();
	}
}
