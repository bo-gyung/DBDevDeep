package com.dbdevdeep.document.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
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

import com.dbdevdeep.document.domain.Folder;
import com.dbdevdeep.document.service.FolderService;

@RestController
@RequestMapping("/api") // JSON 응답을 위한 API
public class FolderViewController {
	
	private final FolderService folderService;
	
	public FolderViewController(FolderService folderService) {
		this.folderService = folderService;
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
    public ResponseEntity<Long> getFolderTotalSize(@RequestParam("folder_no") Long folderNo) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User user = (User) authentication.getPrincipal();
	    String empId = user.getUsername();
	    
        Long totalSize = folderService.calculateFolderTotalSize(folderNo, empId);  // 폴더의 전체 용량 계산
        return ResponseEntity.ok(totalSize);  // 계산된 용량을 클라이언트로 반환
    }
}
