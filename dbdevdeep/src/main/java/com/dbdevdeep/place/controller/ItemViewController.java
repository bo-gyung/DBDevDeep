package com.dbdevdeep.place.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.dbdevdeep.employee.domain.EmployeeDto;
import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.place.domain.ItemDto;
import com.dbdevdeep.place.domain.Place;
import com.dbdevdeep.place.service.ItemService;
import com.dbdevdeep.place.service.PlaceService;

@Controller
public class ItemViewController {

	private final ItemService itemService;
	private final PlaceService placeService;
	private final EmployeeService employeeService;
	
	@Autowired
	public ItemViewController(ItemService itemService, PlaceService placeService, EmployeeService employeeService) {
		this.itemService = itemService;
		this.placeService = placeService;
		this.employeeService = employeeService;
	}
	
	//수정하기
	@GetMapping("/item/update/{item_no}")
	public String updateItem(Model model, @PathVariable("item_no") Long item_no) {
		
		// 1. 로그인한 사람 정보
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String emp_id = user.getUsername();
	    EmployeeDto e_dto = employeeService.selectEmployeeOne(emp_id);
	    
	    model.addAttribute("e_dto", e_dto);
	    
	    ItemDto dto = itemService.selectItemOne(item_no);
	    
	    model.addAttribute("dto" ,dto);
	    
	    return "place/item_update";
	}
	
	// 상세 조회
	@GetMapping("/item/{item_no}")
	public String detailItem(Model model, @PathVariable("item_no") Long item_no) {
		
		// 1. 로그인한 사람 정보
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String emp_id = user.getUsername();
	    EmployeeDto e_dto = employeeService.selectEmployeeOne(emp_id);
	    
	    model.addAttribute("e_dto", e_dto);
	    ItemDto dto = itemService.selectItemOne(item_no);
	    
	    model.addAttribute("dto" ,dto);
	    
	    if(dto.getNew_pic_name() != null && !dto.getNew_pic_name().isEmpty()) {
	    	String imageUrl = "/UploadImg/place/item/" + dto.getNew_pic_name();
	    	model.addAttribute("imageUrl", imageUrl);
	    } else {
	    	model.addAttribute("imageUrl", null);
	    }
	    
	    return "place/item_detail";
	}
	
	
	// 등록
	@GetMapping("/item/create")
	public String createItem(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String emp_id = user.getUsername();
	    EmployeeDto e_dto = employeeService.selectEmployeeOne(emp_id); 
	    
	    // Place 목록 가져오기(select용)
	    List<Place> placeList = placeService.getAllPlaces();
	    model.addAttribute("placeList",placeList);
	    
	    model.addAttribute("writer",e_dto);
	    
	    return "place/item_create";
	}
	
	// 장소별 항목 조회
		@GetMapping("/item/place")
		public String selectItemsByPlace(@RequestParam("placeNo") Long placeNo, Model model) {
			model.addAttribute("itemList", itemService.selectItemsByPlace(placeNo));
			return "place/item_list";
		}
	
	
	// 목록조회
	@GetMapping("/item")
	public String selectItemList(Model model,ItemDto itemDto) {
		List<ItemDto> resultList = itemService.selectItemList(itemDto);
		model.addAttribute("resultList",resultList);
		
		// 다른 동적으로 계산된 값도 전달 가능
		for (ItemDto dto : resultList) {
			int availableQuantity = (dto.getItem_quantity() - dto.getUnuseable_quantity());
			model.addAttribute("availableQuantity_" + dto.getItem_no(), availableQuantity);
		}
		
		return "place/item_list";
	}
}
