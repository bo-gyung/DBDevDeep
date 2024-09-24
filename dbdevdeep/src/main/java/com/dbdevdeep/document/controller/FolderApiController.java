package com.dbdevdeep.document.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbdevdeep.document.service.FolderService;

@Controller
public class FolderApiController {
	
	private final FolderService folderService;
	
	public FolderApiController(FolderService folderService) {
		this.folderService = folderService;
	}
	
	@ResponseBody
	@PostMapping("/folder")
	public Map<String, String> createFolder(@RequestBody Map<String, Object> requestData){
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "새폴더 생성 중 오류가 발생했습니다.");
        
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		String empId = user.getUsername();
        
        // folder_no와 folder_name을 Map에서 추출
        Long folderNo = Long.valueOf(requestData.get("folder_no").toString());
        String folderName = requestData.get("folder_name").toString();
        
        if(folderService.createFolder(folderNo, folderName, empId) > 0) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "새폴더 생성에 성공했습니다.");
        }
        
        return resultMap;
	}
}
