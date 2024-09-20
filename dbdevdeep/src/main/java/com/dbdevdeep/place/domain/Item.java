package com.dbdevdeep.place.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="item")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "item_no")
	private Long itemNo;
	
	@ManyToOne
	@JoinColumn(name = "place_no")
	private Place place;
	
	@Column(name ="item_name")
	private String itemName;
	
	@Column(name ="item_quantity")
	private int itemQuantity;
	
	@Column(name ="item_status")
	private String itemStatus;
	
	@Column(name ="item_serial_no")
	private String itemSerialNo;
	
	@Column(name ="item_content")
	private String itemContent;
	
	@Column(name ="unuseable_quantity")
	private int unuseableQuantity;
	
	@Column(name ="unuseable_reason")
	private String unuseableReason;
	
	@Column(name ="unuseable_start_date")
	private String unuseableStartDate;
	
	@Column(name ="unuseable_end_date")
	private String unuseableEndDate;
	
	@Column(name ="ori_pic_name")
	private String oriPicName;
	
	@Column(name ="new_pic_name")
	private String newPicName;
	
	@Column(name ="reg_date")
	@CreationTimestamp
	private LocalDateTime regDate;
	
	@Column(name ="mod_date")
	@UpdateTimestamp
	private LocalDateTime modDate;
	
	
	
	
	
	
	
	
	
	
	
}
