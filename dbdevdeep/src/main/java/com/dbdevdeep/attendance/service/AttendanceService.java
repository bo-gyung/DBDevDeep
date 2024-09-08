package com.dbdevdeep.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.dbdevdeep.attendance.domain.Attendance;
import com.dbdevdeep.attendance.domain.AttendanceDto;
import com.dbdevdeep.attendance.repository.AttendanceRepository;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@Service
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;
	private final EmployeeRepository employeeRepository;
	
	@Autowired
	public AttendanceService(AttendanceRepository attendanceRepository,
			EmployeeRepository employeeRepository) {
		this.attendanceRepository = attendanceRepository;
		this.employeeRepository = employeeRepository;
	}
	
	public int employeeCheckIn(String empId) {
		
		int result = -1;
		
		try {
			Employee employee = employeeRepository.findByempId(empId);
			
			LocalDate attendDate = LocalDate.now();
			LocalDateTime checkInTime = LocalDateTime.now();
			LocalDateTime checkOutTime = LocalDateTime.now();
			
			LocalTime thresholdTime = LocalTime.of(8, 0); // 8시 기준
      boolean isLate = checkInTime.toLocalTime().isAfter(thresholdTime);
      String lateStatus = isLate ? "Y" : "N";
			
			Attendance attendance = Attendance.builder()
          .employee(employee)
          .attendDate(attendDate)
          .checkInTime(checkInTime)
          .checkOutTime(checkOutTime)
          .workStatus(1)
          .lateStatus(lateStatus)
          .build();
			
			attendanceRepository.save(attendance);
			
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public AttendanceDto findByTodayCheckTime() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		String empId = user.getUsername();
		
		AttendanceDto attendDto = new AttendanceDto();
		
		
		try {
			Employee employee = employeeRepository.findByempId(empId);
			
			try {
				LocalDate ld = LocalDate.now();
				
				Attendance attend = attendanceRepository.findByTodayCheckTime(employee, ld);
				
				if(attend != null) {
					attendDto = attendDto.toDto(attend);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return attendDto;
	}
	
	public int employeeCheckOut(AttendanceDto dto) {
int result = -1;
		
		try {
			Employee employee = employeeRepository.findByempId(dto.getEmp_id());
			
			LocalDate attendDate = dto.getAttend_date();
			LocalDateTime checkInTime = dto.getCheck_in_time();
			LocalDateTime checkOutTime = LocalDateTime.now();
			
			LocalTime thresholdTime = LocalTime.of(8, 0); // 8시 기준
      boolean isLate = checkInTime.toLocalTime().isAfter(thresholdTime);
      String lateStatus = isLate ? "Y" : "N";
			
			Attendance attendance = Attendance.builder()
          .employee(employee)
          .attendNo(dto.getAttend_no())
          .attendDate(attendDate)
          .checkInTime(checkInTime)
          .checkOutTime(checkOutTime)
          .workStatus(2)
          .lateStatus(lateStatus)
          .build();
			
			attendanceRepository.save(attendance);
			
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
