package com.dbdevdeep.schedule.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dbdevdeep.schedule.domain.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>{

	List<Schedule> findByCalendarType(int calendarType);

	List<Schedule> findByCalendarTypeAndEmployee_EmpId(int calendarType, String empId);

	Schedule findByScheduleNo(Long scheduleNo);

	List<Schedule> findByAlertTypeIsNot(String alertType);
	
	@Query("SELECT s FROM Schedule s WHERE s.calendarType = :calendarType AND :today BETWEEN s.startDate AND s.endDate")
	List<Schedule> findSchedulesForToday(@Param("calendarType") int calendarType, @Param("today") LocalDate today);

	@Query("SELECT s FROM Schedule s WHERE s.calendarType = :calendarType AND s.employee.empId = :empId AND :today BETWEEN s.startDate AND s.endDate")
	List<Schedule> findSchedulesForToday(@Param("calendarType") int calendarType, @Param("empId") String empId, @Param("today") LocalDate today);


}
