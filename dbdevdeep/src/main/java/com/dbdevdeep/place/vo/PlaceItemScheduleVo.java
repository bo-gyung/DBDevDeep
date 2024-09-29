package com.dbdevdeep.place.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
public class PlaceItemScheduleVo {

private Long place_schedule_no;
	
	// join
	private Long place_no;
	private String place_name;
	private Long item_no;  
	
	List<Long> itemNoList;
	private String item_name;
	private int item_quantity;
	private String item_serial_no;
	private Long teacher_no;
	private int grade;
	private int gradeClass;
	private String t_year;
	private String emp_id;
	private String emp_name;
	
	// join아닌것
	private String place_schedule_title;
	private String place_schedule_content;
	private LocalDate start_date;
	private LocalDate end_date;
	private String management_no;
	private LocalTime start_time;
	private LocalTime end_time;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	private String formattedRegDate; // 날짜 포맷팅 결과를 담을 필드
	
}
