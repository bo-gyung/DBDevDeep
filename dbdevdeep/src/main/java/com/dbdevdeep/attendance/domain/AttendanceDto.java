package com.dbdevdeep.attendance.domain;

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
public class AttendanceDto {

	private Long attend_no;
	
	private String emp_id;
	private String emp_name;
	private int vacation_hour;
	
	private LocalDate attend_date;
	private LocalDateTime check_in_time;
	private LocalDateTime check_out_time;
	private int work_status;
	private String late_status;
	
	public Attendance toEntity() {
		return Attendance.builder()
				.attendNo(attend_no)
				.attendDate(attend_date)
				.checkInTime(check_in_time)
				.checkOutTime(check_out_time)
				.workStatus(work_status)
				.lateStatus(late_status)
				.build();
	}
	
	public AttendanceDto toDto(Attendance attend) {
		return AttendanceDto.builder()
				.attend_no(attend.getAttendNo())
				.emp_id(attend.getEmployee().getEmpId())
				.emp_name(attend.getEmployee().getEmpName())
				.vacation_hour(attend.getEmployee().getVacationHour())
				.attend_date(attend.getAttendDate())
				.check_in_time(attend.getCheckInTime())
				.check_out_time(attend.getCheckOutTime())
				.work_status(attend.getWorkStatus())
				.late_status(attend.getLateStatus())
				.build();
	}
}
