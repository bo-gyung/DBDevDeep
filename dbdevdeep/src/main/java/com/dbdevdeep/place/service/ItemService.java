package com.dbdevdeep.place.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.place.domain.Item;
import com.dbdevdeep.place.domain.ItemDto;
import com.dbdevdeep.place.domain.Place;
import com.dbdevdeep.place.repository.ItemRepository;
import com.dbdevdeep.place.repository.PlaceRepository;

@Service
public class ItemService {

	private final ItemRepository itemRepository;
	private final PlaceRepository placeRepository;
	private final FileService fileService;
	
	@Autowired
	public ItemService(ItemRepository itemRepository, PlaceRepository placeRepository,
			FileService fileService) {
		this.itemRepository = itemRepository;
		this.placeRepository = placeRepository;
		this.fileService = fileService;
	}
	// 일련번호 중복방지
	
	
	
	// 삭제하기
	public int deleteItem(Long item_no) {
		int result = -1;
		
		try {
			  Item item = itemRepository.findByitemNo(item_no);
	            if (item.getNewPicName() != null) {
	                fileService.placeDelete(item_no);  // 파일 삭제
	            }
	            itemRepository.deleteById(item_no);  // 기자재 삭제
	            result = 1;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return result;	
		
	}
	
	// 수정하기
	public int updateItem(ItemDto dto, MultipartFile file) {
	    int result = -1;

	    try {
	        Place p = placeRepository.findByplaceNo(dto.getPlace_no());
	        if (dto.getItem_content() == null || dto.getItem_content().isEmpty()) {
	            throw new IllegalArgumentException("item_content 값이 null이거나 비어있습니다.");
	        }
	        // item_serial_no null 체크 추가
	        if (dto.getItem_serial_no() == null || dto.getItem_serial_no().isEmpty()) {
	            throw new IllegalArgumentException("item_serial_no 값이 null이거나 비어있습니다.");
	        }


	        // 새 파일이 업로드되면 기존 파일 삭제
	        if (file != null && !file.isEmpty()) {
	            if (p.getNewPicName() != null) {
	                fileService.placeDelete(p.getPlaceNo());  // 기존 파일 삭제
	            }
	            String newPicName = fileService.placeUpload(file);  // 새 파일 업로드
	            dto.setNew_pic_name(newPicName);
	            dto.setOri_pic_name(file.getOriginalFilename());
	        }

	        // 만약 상태가 "사용가능"이라면 사용 불가 날짜를 null로 설정
	        if ("Y".equals(dto.getItem_status())) {
	            dto.setUnuseable_start_date(null);
	            dto.setUnuseable_end_date(null);
	            dto.setUnuseable_reason(null);
	            dto.setUnuseable_quantity(0);
	        } else {
	            // 날짜 형식 변환 (예외 처리 추가)
	            try {
	                DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 서버에서의 기본 형식
	                if (dto.getUnuseable_start_date() != null && !dto.getUnuseable_start_date().isEmpty()) {
	                    String formattedStartDate = LocalDate.parse(dto.getUnuseable_start_date(), inputFormatter)
	                                                         .toString();
	                    dto.setUnuseable_start_date(formattedStartDate);
	                }

	                if (dto.getUnuseable_end_date() != null && !dto.getUnuseable_end_date().isEmpty()) {
	                    String formattedEndDate = LocalDate.parse(dto.getUnuseable_end_date(), inputFormatter)
	                                                         .toString();
	                    dto.setUnuseable_end_date(formattedEndDate);
	                }
	            } catch (DateTimeParseException e) {
	                // 날짜 변환 실패 처리
	                e.printStackTrace();
	                return -1;  // 실패 시 -1 반환
	            }
	        }

	        Item i = Item.builder()
	                .itemNo(dto.getItem_no())
	                .place(p)
	                .itemName(dto.getItem_name())
	                .itemSerialNo(dto.getItem_serial_no())
	                .itemQuantity(dto.getItem_quantity())
	                .itemContent(dto.getItem_content())
	                .itemStatus(dto.getItem_status())
	                .unuseableQuantity(dto.getUnuseable_quantity())
	                .unuseableReason(dto.getUnuseable_reason())
	                .unuseableStartDate(dto.getUnuseable_start_date())
	                .unuseableEndDate(dto.getUnuseable_end_date())
	                .oriPicName(dto.getOri_pic_name() != null ? dto.getOri_pic_name() : "Default oriPicName")
	                .newPicName(dto.getNew_pic_name() != null ? dto.getNew_pic_name() : "Default newPicName")
	                .regDate(dto.getReg_date())
	                .modDate(LocalDateTime.now())
	                .build();

	        itemRepository.save(i);
	        result = 1;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return result;
	}
	
	
	// 상세 조회
	public ItemDto selectItemOne(Long item_no) {
		
		Item i = itemRepository.findByitemNo(item_no);
		
		if (i == null || i.getPlace() == null) {
	        // 예외 처리: 만약 Item 또는 Place가 null이면, 적절한 오류 처리
	        throw new IllegalArgumentException("해당 아이템이나 장소 정보를 찾을 수 없습니다.");
	    }

		
		 // 사용 불가 기간
	    String unuseableStartDate = i.getUnuseableStartDate();
	    String unuseableEndDate = i.getUnuseableEndDate();

	    // 날짜 포맷 설정
	    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

	    // 시작 날짜 포맷팅, 값이 없으면 "N/A" 반환
	    String formattedStartDate = (unuseableStartDate != null && !unuseableStartDate.isEmpty()) 
	        ? LocalDate.parse(unuseableStartDate, inputFormatter).format(outputFormatter)
	        : "N/A";

	    // 종료 날짜 포맷팅, 값이 없으면 "N/A" 반환
	    String formattedEndDate = (unuseableEndDate != null && !unuseableEndDate.isEmpty())
	        ? LocalDate.parse(unuseableEndDate, inputFormatter).format(outputFormatter)
	        : "N/A";
	    
	    ItemDto dto = ItemDto.builder()
	            .item_name(i.getItemName())
	            .place_no(i.getPlace().getPlaceNo())
	            .place_name(i.getPlace().getPlaceName())  // Place의 place_name 가져오기
	            .place_location(i.getPlace().getPlaceLocation())
	            .item_no(i.getItemNo())
	            .item_serial_no(i.getItemSerialNo())
	            .item_status(i.getItemStatus())
	            .item_content(i.getItemContent())
	            .item_quantity(i.getItemQuantity())
	            .unuseable_reason(i.getUnuseableReason())
	            .unuseable_start_date(formattedStartDate)  // 변환된 날짜
	            .unuseable_end_date(formattedEndDate)  // 변환된 날짜
	            .ori_pic_name(i.getOriPicName()!= null ? i.getOriPicName() : "Default oriPicName")
	            .new_pic_name(i.getNewPicName()!= null ? i.getNewPicName() : "Default newPicName")
	            .reg_date(i.getRegDate())
	            .mod_date(i.getModDate())
	            .build();
	    
	    return dto;
	}
	
	
	// 정규식 중복여부
	
    public boolean checkSerialExists(Long placeNo, String serialNo) {
        return itemRepository.existsByPlacePlaceNoAndItemSerialNo(placeNo, serialNo);
    }
	
	// 등록
	
	public int createItem(ItemDto dto) {
			int result = -1;
		try {
			
			Place p = placeRepository.findByplaceNo(dto.getPlace_no());
			
			Item i = Item.builder()
					
					.place(p)
					.itemSerialNo(dto.getItem_serial_no())
					.itemName(dto.getItem_name())
					.itemContent(dto.getItem_content())
					.itemQuantity(dto.getItem_quantity())
					.itemStatus(dto.getItem_status())
					.unuseableReason(dto.getUnuseable_reason())
					.unuseableQuantity(dto.getUnuseable_quantity())
					.unuseableStartDate(dto.getUnuseable_start_date())
					.unuseableEndDate(dto.getUnuseable_end_date())
					.oriPicName(dto.getOri_pic_name() != null ? dto.getOri_pic_name() : "Default oriPicname")
	                .newPicName(dto.getNew_pic_name() != null ? dto.getNew_pic_name() : "Default newPicname")
					.regDate(dto.getReg_date())
					.modDate(dto.getMod_date())
					.build();
			
			itemRepository.save(i);
			result = 1;
			
		}catch(Exception e) {
			e.printStackTrace();;
		}
		return result;
	}
	
	
	// 장소별 항목 조회 메서드 추가
		public List<ItemDto> selectItemsByPlace(Long placeNo) {
			List<Item> itemList = itemRepository.findByPlacePlaceNo(placeNo);  // 장소 번호로 항목 조회
			List<ItemDto> itemDtoList = new ArrayList<>();

			for(Item i : itemList) {
				ItemDto dto = ItemDto.builder()
		    			.item_no(i.getItemNo())
		    			.place_no(i.getPlace().getPlaceNo())
		    			.place_name(i.getPlace().getPlaceName())
		    			.item_name(i.getItemName())
		    			.place_location(i.getPlace().getPlaceLocation())
		    			.item_status(i.getItemStatus())
		    			.item_quantity(i.getItemQuantity())
		    			.unuseable_quantity(i.getUnuseableQuantity())
		    			.build();
		    	itemDtoList.add(dto);
			}
			return itemDtoList;
		}
	
	
	
	//목록 조회
	public List<ItemDto> selectItemList(ItemDto itemDto){
		List<Item> itemList = itemRepository.findAll();
		List<ItemDto> itemDtoList = new ArrayList<ItemDto>();
		// 사용자 정보
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    
	    for(Item i : itemList) {
	    	// 사용 가능 갯수 계산: 총 갯수 - 사용 불가 갯수
            int availableQuantity = i.getItemQuantity() - i.getUnuseableQuantity();
            
	    	ItemDto dto = ItemDto.builder()
	    			.item_no(i.getItemNo())
	    			.place_no(i.getPlace().getPlaceNo())
	    			.place_name(i.getPlace().getPlaceName())
	    			.item_name(i.getItemName())
	    			.place_location(i.getPlace().getPlaceLocation())
	    			.item_status(i.getItemStatus())
	    			.item_quantity(i.getItemQuantity())
	    			
	    			.unuseable_quantity(i.getUnuseableQuantity())
	    			.build();
	    	
	    	
	    	
	    	itemDtoList.add(dto);
	    			
	    	
	    }
	    return itemDtoList;
	}
	
	
	
	
	
	
}
