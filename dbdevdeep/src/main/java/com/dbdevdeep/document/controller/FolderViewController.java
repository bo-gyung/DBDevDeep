package com.dbdevdeep.document.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dbdevdeep.FileService;
import com.dbdevdeep.document.service.FolderService;

@RestController
@RequestMapping("/api") // JSON 응답을 위한 API
public class FolderViewController {
	
	private final FolderService folderService;
	private final FileService fileService;
	
	@Value("${folder.total-capacity}")
	private String totalCapacityConfig;
	
	public FolderViewController(FolderService folderService, FileService fileService) {
		this.folderService = folderService;
		this.fileService = fileService;
	}
	
	@GetMapping("/folder-tree")
	public Map<String, Object> selectFolderList(Model model) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String empId = user.getUsername();
	    
	    Map<String, Object> folderTreeData = new HashMap<>();
	    folderTreeData.put("publicFolderList", folderService.selectPublicFolderTree());
	    folderTreeData.put("privateFolderList", folderService.selectPrivateFolderTree(empId));
	    
	    return folderTreeData;
	}
	
    @GetMapping("/totalSize")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getFolderTotalSize(@RequestParam("folder_no") Long folderNo) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String empId = user.getUsername();
	    
	    Long usedSize = folderService.calculateFolderTotalSize(folderNo, empId);  // 폴더의 전체 용량 계산
	    
	    Long totalCapacity = fileService.getFolderTotalCapacity();
        
        Map<String, Long> response = new HashMap<>();
        response.put("usedSize", usedSize);         // 현재 사용 중인 용량
        response.put("totalCapacity", totalCapacity); // 폴더의 총 용량
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/privateTotalSize")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getPrivateFolderTotalSize() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String empId = user.getUsername();
	    
	    Long usedSize = folderService.calculateFolderTotalSize(6L, empId);  // 폴더의 전체 용량 계산
	    
	    Long totalCapacity = fileService.getFolderTotalCapacity();
        
        Map<String, Long> response = new HashMap<>();
        response.put("usedSize", usedSize);         // 현재 사용 중인 용량
        response.put("totalCapacity", totalCapacity); // 폴더의 총 용량
        
        return ResponseEntity.ok(response);
    }
}
