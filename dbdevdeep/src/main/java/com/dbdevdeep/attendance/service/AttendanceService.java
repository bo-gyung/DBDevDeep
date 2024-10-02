package com.dbdevdeep.attendance.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.dbdevdeep.approve.service.RestHolidayService;
import com.dbdevdeep.attendance.domain.Attendance;
import com.dbdevdeep.attendance.domain.AttendanceDto;
import com.dbdevdeep.attendance.repository.AttendanceRepository;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@EnableScheduling 
@Service
public class AttendanceService {

	private final AttendanceRepository attendanceRepository;
	private final EmployeeRepository employeeRepository;
	private final RestHolidayService restHolidayService;

	@Autowired
	public AttendanceService(AttendanceRepository attendanceRepository, EmployeeRepository employeeRepository,
			RestHolidayService restHolidayService) {
		this.attendanceRepository = attendanceRepository;
		this.employeeRepository = employeeRepository;
		this.restHolidayService = restHolidayService;
	}

	public int employeeCheckIn(String empId) {

		int result = -1;
		int overtime = 0;

		try {
			Employee employee = employeeRepository.findByempId(empId);

			LocalDate attendDate = LocalDate.now();
			LocalDateTime checkInTime = LocalDateTime.now();
			int year = attendDate.getYear();
			int month = attendDate.getMonthValue();
			int date = attendDate.getDayOfMonth();
			if(date == 1) {
				overtime = 0;
			}else {
				overtime = attendanceRepository.findByLastInfo(employee , year , month).orElse(0);
			}
			
			LocalDateTime checkOutTime = LocalDateTime.now();

			LocalTime thresholdTime = LocalTime.of(8, 0); // 8시 기준
      boolean isLate = checkInTime.toLocalTime().isAfter(thresholdTime);
      String lateStatus = isLate ? "Y" : "N";
			
			Attendance attendance = Attendance.builder()
          .employee(employee)
          .attendDate(attendDate)
          .checkInTime(checkInTime)
          .checkOutTime(null)
          .workStatus(1)
          .lateStatus(lateStatus)
          .overtimeSum(overtime)
          .build();

			attendanceRepository.save(attendance);

			result = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public AttendanceDto findByTodayCheckTime() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User) authentication.getPrincipal();
		String empId = user.getUsername();

		AttendanceDto attendDto = new AttendanceDto();

		try {
			Employee employee = employeeRepository.findByempId(empId);

			try {
				LocalDate ld = LocalDate.now();

				Attendance attend = attendanceRepository.findByTodayCheckTime(employee, ld);

				if (attend != null) {
					attendDto = attendDto.toDto(attend);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return attendDto;
	}

	public int employeeCheckOut(AttendanceDto dto) {
		int result = -1;
		int overtime = 0;
		int overtimeSum = 0;
		try {
			Employee employee = employeeRepository.findByempId(dto.getEmp_id());
			
			LocalDate attendDate = dto.getAttend_date();
			int year = attendDate.getYear();
			int month = attendDate.getMonthValue();
			LocalDateTime checkInTime = dto.getCheck_in_time();
			LocalDateTime checkOutTime = LocalDateTime.now();
			
			int overtimeEnd = checkOutTime.getHour()-16;
			
			overtime = attendanceRepository.findByLastInfo(employee , year , month).orElse(0);
			if(overtime > 67) {
				overtimeSum = overtime;
			}else {
				overtimeSum = overtime + overtimeEnd;
			}
			
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
          .overtimeSum(overtimeSum)
          .build();
			
			attendanceRepository.save(attendance);
			
			result = 1;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public List<String> selectByToDayList() {
		List<String> dtoList = new ArrayList<>();
		
		LocalDate now = LocalDate.now(); 
		
		List<Attendance> attendList = attendanceRepository.selectByToDayList(now);
		
		for(Attendance attend : attendList) {
			String list = attend.getEmployee().getEmpId();
			
			dtoList.add(list);
		}
		
		return dtoList;
	}

	// vacationHour 출력
	public EmployeeDto employeeInfo(String empId) {
		Employee employee = employeeRepository.findByempId(empId);
		EmployeeDto dto = EmployeeDto.builder()
				.vacation_hour(employee.getVacationHour())
				.build();
		
		return dto;
	}
	
	// 월별 기본 정보 조회
	public List<AttendanceDto> findByYearAndMonth(String empId, int year, int month){
		List<Attendance> aList = null;
		LocalDate startDate = LocalDate.of(year, month, 1);
		LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
		LocalDate today = LocalDate.now();
		
		Employee employee = employeeRepository.findByempId(empId);
		aList = attendanceRepository.findByYearAndMonth(employee, startDate, endDate, today);
		List<AttendanceDto> aDtoList = new ArrayList<AttendanceDto>();
		for(Attendance a : aList) {
			AttendanceDto dto = new AttendanceDto().toDto(a);
			aDtoList.add(dto);
		}
		return aDtoList;
	}
	
	// 직원 상세 페이지 근태기록 출력
	public List<AttendanceDto> findByempId(String empId) {
		List<AttendanceDto> attendDtoList = new ArrayList<AttendanceDto>();
		Employee employee = employeeRepository.findByempId(empId);
		
		List<Attendance> attendList = attendanceRepository.findByEmpIdList(employee);
		
		for(Attendance a : attendList) {
			AttendanceDto dto = new AttendanceDto().toDto(a);
			
			attendDtoList.add(dto);
		}
		
		return attendDtoList;
	}
	
	 @Scheduled(cron = "0 1 16 * * *")
	    public void noCheckIn() {
	        LocalDate today = LocalDate.now();
	        int overtime = 0;
	        int year = today.getYear();
			int month = today.getMonthValue();
	        int date = today.getDayOfMonth();

	        Set<LocalDate> holidays = new HashSet<>(restHolidayService.getHolidays(today.getYear(), today.getMonthValue()));

	        List<Employee> employees = employeeRepository.findAll();

	        for (Employee employee : employees) {
	            if (isWeekendOrHoliday(today, new ArrayList<>(holidays))) {
	                continue;
	            }

	            Attendance attendance = attendanceRepository.findByEmpAndDate(employee, today);
	            if(date == 1) {
	            	overtime = 0;
	            }else {
	            	overtime = attendanceRepository.findByLastInfo(employee , year , month).orElse(0);
	            }

	            if (attendance == null) {
	                Attendance newAttendance = Attendance.builder()
	                    .employee(employee)
	                    .attendDate(today)
	                    .checkInTime(null)
	                    .checkOutTime(null)
	                    .workStatus(3)
	                    .lateStatus("N")
	                    .overtimeSum(overtime)
	                    .build();

	                attendanceRepository.save(newAttendance);
	            }
	        }
	    }

	    private boolean isWeekendOrHoliday(LocalDate date, List<LocalDate> holidays) {
	        DayOfWeek dayOfWeek = date.getDayOfWeek();
	        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
	            return true;
	        }

	        return holidays.contains(date);
	    }
	
}
