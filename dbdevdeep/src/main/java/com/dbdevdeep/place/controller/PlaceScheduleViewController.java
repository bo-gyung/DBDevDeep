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
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.domain.TeacherHistory;
import com.dbdevdeep.employee.service.EmployeeService;
import com.dbdevdeep.employee.service.TeacherHistoryService;
import com.dbdevdeep.place.domain.Item;
import com.dbdevdeep.place.domain.ItemDto;
import com.dbdevdeep.place.domain.Place;
import com.dbdevdeep.place.service.ItemService;
import com.dbdevdeep.place.service.PlaceScheduleService;
import com.dbdevdeep.place.service.PlaceService;
import com.dbdevdeep.place.vo.PlaceItemScheduleVo;

@Controller
public class PlaceScheduleViewController {

	private final PlaceScheduleService placeScheduleService;
	private final PlaceService placeService;
	private final ItemService itemService;
	private final TeacherHistoryService teacherHistoryService;
	private final EmployeeService employeeService;
	
	@Autowired
	public PlaceScheduleViewController(PlaceService placeService, ItemService itemService,
			TeacherHistoryService teacherHistoryService, EmployeeService employeeService,
			PlaceScheduleService placeScheduleService) {
		this.placeScheduleService = placeScheduleService;
		this.placeService = placeService;
		this.itemService = itemService;
		this.teacherHistoryService = teacherHistoryService;
		this.employeeService = employeeService;
	}
	
	
	// 일정등록 목록 조회
	@GetMapping("schedule_list")
		public String scheduleList(Model model) {
			List<PlaceItemScheduleVo> totalSchedule = placeScheduleService.scheduleList();
			
			model.addAttribute("resultList", totalSchedule);
			
			return "place/schedule_list";
		}
	
	
	
		
	// 일정 상세 조회 (AJAX 요청용)
	@ResponseBody
	@GetMapping("/place_schedule/detail/{placeScheduleNo}")
	public PlaceItemScheduleVo getScheduleDetailForAjax(@PathVariable("placeScheduleNo") Long placeScheduleNo) {
	    // 서비스에서 일정 상세 정보 가져오기
	    PlaceItemScheduleVo scheduleDetail = placeScheduleService.getScheduleDetail(placeScheduleNo);
	    
	    // 가져온 데이터를 JSON 형태로 반환
	    return scheduleDetail;
		
		
	}
	
	
	
	
		@GetMapping("/getItemsForPlace")
		@ResponseBody
		public List<ItemDto> getItemsForPlace(@RequestParam("placeNo") Long placeNo) {
		
			
			return itemService.getItemsByPlaceNo(placeNo);  // ItemDto 리스트를 반환
		}
		
	
		
		
		// 일정 겹침 메소드 ajax
		@GetMapping("/checkScheduleOverlap")
		@ResponseBody
		public boolean checkScheduleOverlap(@RequestParam(name = "placeNo") Long placeNo,
		                                    @RequestParam(name= "startDate") String startDate,
		                                    @RequestParam(name= "startTime") String startTime,
		                                    @RequestParam(name= "endDate") String endDate,
		                                    @RequestParam(name= "endTime") String endTime) {
		    return placeScheduleService.isScheduleOverlapping(placeNo, startDate, startTime, endDate, endTime);
		}
	
	
	
		// 화면딴
		@GetMapping("/place_schedule")
		public String selectScheduleList(Model model) {
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	        User user = (User)authentication.getPrincipal();
	        
	        // 필요한 데이터를 모두 모델에 추가 (장소, 기자재, 신청인 데이터)
	        List<Place> placeList = placeService.getAvaliablePlaces();
	        List<Item> itemList = itemService.getAvailableItems();
	        List<Employee> employeeList = employeeService.findAllEmployees();
	        List<TeacherHistory> thList = teacherHistoryService.findAllTeachers();
	        
	        // 일정 조회 데이터 추가 - VO 사용
	        List<PlaceItemScheduleVo> totalSchedule = placeScheduleService.selectTotalScheduleList();
	        
	        
	        
	        // 모델에 담아서 뷰로 전달
	        model.addAttribute("user", user);
	        model.addAttribute("placeList", placeList);  // 장소 데이터
	        model.addAttribute("itemList", itemList);    // 기자재 데이터
	        model.addAttribute("employeeList", employeeList);  // 신청인 데이터
	        model.addAttribute("thList", thList);        // 학급 및 반 데이터
	        model.addAttribute("totalSchedule", totalSchedule);  // 조회된 일정 데이터 (VO)

	        return "place/place_schedule"; // 화면 템플릿 경로
		}
		
		// 캘린더 전체 조회 (Json) - VO 사용
		@GetMapping("totalSchedule")
		@ResponseBody  // JSON 응답을 위해 추가
		public List<PlaceItemScheduleVo> totalScheduleData(){
	        List<PlaceItemScheduleVo> totalSchedule = placeScheduleService.selectTotalScheduleList();
     
	        return totalSchedule; // 일정 조회 결과를 VO로 반환
		}
	
}
