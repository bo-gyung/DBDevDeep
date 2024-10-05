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

	@Query(value = """
		    SELECT *
		    FROM schedule s
		    WHERE s.calendar_type = 0
		    AND (
		        -- 매일 반복 일정
		        (s.repeat_type = 'DAILY' 
		         AND :today >= s.repeat_start_date 
		         AND (:today <= s.repeat_end_date OR s.end_date IS NULL) 
		         AND MOD(DATEDIFF(:today, s.start_date), 1) = 0)

		        -- 매주 반복 일정
		        OR (s.repeat_type = 'WEEKLY' 
		            AND :today >= s.repeat_start_date 
		            AND (:today <= s.repeat_end_date OR s.end_date IS NULL) 
		            AND MOD(DATEDIFF(:today, s.start_date), 7) = 0)

		        -- 매월 반복 일정
		        OR (s.repeat_type = 'MONTHLY' 
		            AND :today >= s.repeat_start_date 
		            AND (:today <= s.repeat_end_date OR s.end_date IS NULL) 
		            AND DAY(s.start_date) = DAY(:today))

		        -- 매년 반복 일정
		        OR (s.repeat_type = 'YEARLY' 
		            AND :today >= s.repeat_start_date 
		            AND (:today <= s.repeat_end_date OR s.end_date IS NULL)
		            AND MONTH(s.start_date) = MONTH(:today) 
		            AND DAY(s.start_date) = DAY(:today))

		        -- 반복 설정이 없는 일정
		        OR (s.repeat_type = 'NONE' 
		            AND :today BETWEEN s.start_date AND s.end_date)
		    )
		""", nativeQuery = true)
		List<Schedule> findPublicSchedulesForTodayWithRepeat(@Param("today") String today);

	@Query(value = """
		    SELECT *
		    FROM schedule s
		    WHERE s.calendar_type = 1
		    AND s.emp_id = :empId
		    AND (
		        -- 매일 반복 일정
		        (s.repeat_type = 'DAILY' 
		         AND :today >= s.repeat_start_date 
		         AND (:today <= s.repeat_end_date OR s.end_date IS NULL) 
		         AND MOD(DATEDIFF(:today, s.start_date), 1) = 0)

		        -- 매주 반복 일정
		        OR (s.repeat_type = 'WEEKLY' 
		            AND :today >= s.repeat_start_date 
		            AND (:today <= s.repeat_end_date OR s.end_date IS NULL) 
		            AND MOD(DATEDIFF(:today, s.start_date), 7) = 0)

		        -- 매월 반복 일정
		        OR (s.repeat_type = 'MONTHLY' 
		            AND :today >= s.repeat_start_date 
		            AND (:today <= s.repeat_end_date OR s.end_date IS NULL) 
		            AND DAY(s.start_date) = DAY(:today))

		        -- 매년 반복 일정
		        OR (s.repeat_type = 'YEARLY' 
		            AND :today >= s.repeat_start_date 
		            AND (:today <= s.repeat_end_date OR s.end_date IS NULL)
		            AND MONTH(s.start_date) = MONTH(:today) 
		            AND DAY(s.start_date) = DAY(:today))

		        -- 반복 설정이 없는 일정
		        OR (s.repeat_type = 'NONE' 
		            AND :today BETWEEN s.start_date AND s.end_date)
		    )
		""", nativeQuery = true)
		List<Schedule> findPrivateSchedulesForTodayWithRepeat(@Param("empId") String empId, @Param("today") String today);	
	
}
