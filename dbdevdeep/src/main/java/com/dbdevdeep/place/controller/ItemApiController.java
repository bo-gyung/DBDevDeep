package com.dbdevdeep.place.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.place.domain.ItemDto;
import com.dbdevdeep.place.service.ItemService;

@Controller
public class ItemApiController {

	private final ItemService itemService;
	private final FileService fileService;
	
	@Autowired
	public ItemApiController(ItemService itemService, FileService fileService) {
		this.itemService = itemService;
		this.fileService = fileService;
	}
	
	
	// 삭제하기
	@ResponseBody
	@DeleteMapping("/item/{item_no}")
	public Map<String, String> deleteItem(@PathVariable("item_no") Long item_no){
		Map<String, String> resultMap = new HashMap<String, String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "기가재 삭제중 오류가 발생하였습니다.");
		
		if(itemService.deleteItem(item_no) > 0) {
			resultMap.put("res_code", "200");
			resultMap.put("res_msg", "기자재 삭제에 성공했습니다.");
		}
		return resultMap;
	}
	
	// 수정하기
	@ResponseBody
	@PostMapping("/item/update/{item_no}")
	public Map<String, String> updateItem(@ModelAttribute ItemDto dto, @RequestParam(name = "file", required=false) MultipartFile file){
		Map<String, String> resultMap = new HashMap<String,String>();
		
		resultMap.put("res_code", "404");
		resultMap.put("res_msg", "기자재 수정중 오류가 발생하였습니다.");
		
		try {
	        // 파일이 존재하는 경우 처리
	        if (file != null && !file.isEmpty()) {
	            String originalFilename = file.getOriginalFilename();

	            if (originalFilename != null && !originalFilename.isEmpty()) {
	                // 새로운 파일 업로드
	                String savedFileName = fileService.placeUpload(file);

	                if (savedFileName != null) {
	                    // 기존 파일 삭제 (기존 파일이 있을 경우)
	                    if (dto.getNew_pic_name() != null && !dto.getNew_pic_name().isEmpty()) {
	                        fileService.placeDelete(dto.getPlace_no());
	                    }

	                    // 새로운 파일 정보 DTO에 설정
	                    dto.setOri_pic_name(originalFilename);
	                    dto.setNew_pic_name(savedFileName);
	                } else {
	                    resultMap.put("res_msg", "파일 업로드 중 오류가 발생했습니다.");
	                    return resultMap;
	                }
	            }
	        }

	        // 기자재 정보 수정
	        if (itemService.updateItem(dto, file) > 0) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "게시글이 성공적으로 수정되었습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        resultMap.put("res_msg", "수정 중 오류가 발생했습니다.");

	    }
		
	    return resultMap;
	}
	
	
	
	
	
	// 일련번호 정규식 	
	 @PostMapping("/item/check-serial")
	    public ResponseEntity<Map<String, Boolean>> checkSerial(@RequestBody Map<String, Object> payload) {
		    String placeNoStr = payload.get("place_no").toString();
		    String serialNo = payload.get("item_serial_no").toString();

		    Map<String, Boolean> response = new HashMap<>();
		    
		    // placeNo가 빈 문자열이거나 null인 경우 처리
		    if (placeNoStr == null || placeNoStr.isEmpty()) {
		        response.put("exists", false);  // 기본적으로 false를 반환
		        return ResponseEntity.ok(response);
		    }

		    // Long으로 변환
		    Long placeNo;
		    try {
		        placeNo = Long.parseLong(placeNoStr);
		    } catch (NumberFormatException e) {
		        response.put("exists", false);  // 변환에 실패하면 false를 반환
		        return ResponseEntity.ok(response);
		    }

		    boolean exists = itemService.checkSerialExists(placeNo, serialNo);
		    response.put("exists", exists);
		    return ResponseEntity.ok(response);
	    }
	
	
	//등록 
	@ResponseBody
	@PostMapping("/item")
	public Map<String, String> createPlace(@ModelAttribute ItemDto dto, @RequestParam("file") MultipartFile file) {
	    Map<String, String> resultMap = new HashMap<String, String>();
	    
	    resultMap.put("res_code", "404");
	    resultMap.put("res_msg", "기자재 등록 중 오류가 발생하였습니다.");
	    
	    try {
	        // 일련번호 중복 체크
	        boolean exists = itemService.checkSerialExists(dto.getPlace_no(), dto.getItem_serial_no());
	        if (exists) {
	            resultMap.put("res_code", "409");
	            resultMap.put("res_msg", "이미 존재하는 일련번호입니다.");
	            return resultMap;
	        }

	        // 기자재 등록 (파일 처리는 서비스에서 수행)
	        if (itemService.createItem(dto, file) > 0) {
	            resultMap.put("res_code", "200");
	            resultMap.put("res_msg", "기자재 등록에 성공하였습니다.");
	        } else {
	            resultMap.put("res_msg", "기자재 등록에 실패하였습니다.");
	        }

	    } catch (Exception e) {
	        // 예외 처리
	        e.printStackTrace();
	        resultMap.put("res_msg", "서버 오류로 인해 기자재 등록에 실패하였습니다.");
	    }
	    
	    return resultMap;	}
	
	
}
