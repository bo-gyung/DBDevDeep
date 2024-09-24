package com.dbdevdeep.place.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
public class PlaceItemScheduleDto {
	
	private Long place_schedule_no;
	
	// join
	private Long place_no;
	private String place_name;
	private Long item_no;
	private String item_name;
	private int item_quantity;
	private String item_serial_no;
	private Long teacher_no;
	private int grade;
	private int gradeClass;
	private String emp_id;
	private String emp_name;
	
	private String place_schedule_title;
	private String place_schedule_content;
	private LocalDate start_date;
	private LocalDate end_date;
	private String management_no;
	private LocalTime start_time;
	private LocalTime end_time;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;
	
	
	public PlaceItemSchedule toEntity() {
		return PlaceItemSchedule.builder()
				.placeScheduleNo(place_schedule_no)
				.placeScheduleTitle(place_schedule_title)
				.placeScheduleContent(place_schedule_content)
				.startDate(start_date)
				.endDate(end_date)
				.startTime(start_time)
				.endTime(end_time)
				.managementNo(management_no)
				.regDate(reg_date)
				.modDate(mod_date)
				
				.build();
	}
	
	public PlaceItemScheduleDto toDto(PlaceItemSchedule pis) {
	    
	    String managementNo = pis.getPlace().getPlaceNo() + "-" + pis.getItem().getItemSerialNo(); // place_no와 item_serial_no를 조합
	    
	    return PlaceItemScheduleDto.builder()
	            .place_schedule_no(pis.getPlaceScheduleNo())
	            .place_no(pis.getPlace().getPlaceNo())
	            .place_name(pis.getPlace().getPlaceName())
	            .item_no(pis.getItem().getItemNo())
	            .item_name(pis.getItem().getItemName())
	            .item_quantity(pis.getItem().getItemQuantity())
	            .item_serial_no(pis.getItem().getItemSerialNo())  // item_serial_no 포함
	            .teacher_no(pis.getTeacherHistory().getTeacherNo())
	            .grade(pis.getTeacherHistory().getGrade())
	            .gradeClass(pis.getTeacherHistory().getGradeClass())
	            .emp_id(pis.getEmployee().getEmpId())
	            .emp_name(pis.getEmployee().getEmpName())
	            .place_schedule_title(pis.getPlaceScheduleTitle())
	            .place_schedule_content(pis.getPlaceScheduleContent())
	            .start_date(pis.getStartDate())
	            .end_date(pis.getEndDate())
	            .start_time(pis.getStartTime())
	            .end_time(pis.getEndTime())
	            .management_no(managementNo)  // 조합된 management_no 설정
	            .reg_date(pis.getRegDate())
	            .mod_date(pis.getModDate())
	            .build();
	

				
	}
	
	
	
	
	
	

}
