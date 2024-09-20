package com.dbdevdeep.place.domain;

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
public class PlaceDto {
	

	private Long place_no;
	private String emp_id; 
	
	private String place_name;
	private String place_location;
	private String place_content;
	private String place_status;
	private String place_start_time;
	private String place_end_time;
	private String unuseable_reason;
	private String unuseable_start_date;
	private String unuseable_end_date;
	private String ori_pic_name;
	private String new_pic_name;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;

	// 포맷된 시간 범위
	private String formattedTimeRange;
	
	public Place toEntity() {
		return Place.builder()
				.placeNo(0L)
				
				.placeName(place_name)
				.placeLocation(place_location)
				.placeContent(place_content)
				.placeStatus(place_status)
				.placeStarttime(place_start_time)
				.placeEndtime(place_end_time)
				.unuseableReason(unuseable_reason)
				.unuseableStartDate(unuseable_start_date)
				.unuseableEndDate(unuseable_end_date)
				.oriPicName(ori_pic_name)
				.newPicName(new_pic_name)
				.regDate(reg_date)
				.modDate(mod_date)
				.build();
	}
	
	public PlaceDto toDto (Place place) {
		
		
		return PlaceDto.builder()
				.place_no(place.getPlaceNo())
				.emp_id(place.getEmployee().getEmpId())
				
				.place_name(place.getPlaceName())
				.place_location(place.getPlaceLocation())
				.place_content(place.getPlaceContent())
				.place_status(place.getPlaceStatus())
				.place_start_time(place.getPlaceStarttime())
				.place_end_time(place.getPlaceEndtime())

				.unuseable_reason(place.getUnuseableReason())
				.unuseable_start_date(place.getUnuseableStartDate())
				.unuseable_end_date(place.getUnuseableEndDate())
				.ori_pic_name(place.getOriPicName())
				.new_pic_name(place.getNewPicName())
				.reg_date(place.getRegDate())
				.mod_date(place.getModDate())
				.build();
				
	}
	
}
