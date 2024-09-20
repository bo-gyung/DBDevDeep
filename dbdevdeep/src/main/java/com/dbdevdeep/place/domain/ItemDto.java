package com.dbdevdeep.place.domain;

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
public class ItemDto {
	
	private Long item_no;
	// place join
	private Long place_no;
	private String place_name;
	private String place_location;
	
	private String item_name;
	private int item_quantity;
	private String item_status;
	private String item_serial_no;
	private String item_content;
	private int unuseable_quantity;
	private String unuseable_reason;
	private String unuseable_start_date;
	private String unuseable_end_date;
	private String ori_pic_name;
	private String new_pic_name;
	private LocalDateTime reg_date;
	private LocalDateTime mod_date;

	
	public Item toEntity() {
		return Item.builder()
				.itemNo(item_no)
				.itemName(item_name)
				.itemQuantity(item_quantity)
				.itemStatus(item_status)
				.itemSerialNo(item_serial_no)
				.itemContent(item_content)
				.unuseableQuantity(unuseable_quantity)
				.unuseableReason(unuseable_reason)
				.unuseableStartDate(unuseable_start_date)
				.unuseableEndDate(unuseable_end_date)
				.oriPicName(ori_pic_name)
				.newPicName(new_pic_name)
				.regDate(reg_date)
				.modDate(mod_date)
				.build();
				
	}
	
	public ItemDto toDto (Item i) {
		
		return ItemDto.builder()
				.item_no(i.getItemNo())
				.place_no(i.getPlace().getPlaceNo())
				.place_name(i.getPlace().getPlaceName())
				.place_location(i.getPlace().getPlaceLocation())
				.item_name(i.getItemName())
				.item_quantity(i.getItemQuantity())
				.item_status(i.getItemStatus())
				.item_serial_no(i.getItemSerialNo())
				.item_content(i.getItemContent())
				.unuseable_quantity(i.getUnuseableQuantity())
				.unuseable_reason(i.getUnuseableReason())
				.unuseable_start_date(i.getUnuseableStartDate())
				.unuseable_end_date(i.getUnuseableEndDate())
				.ori_pic_name(i.getOriPicName())
				.new_pic_name(i.getNewPicName())
				.reg_date(i.getRegDate())
				.mod_date(i.getModDate())
				.build();
	}
	
	
	
	
}
