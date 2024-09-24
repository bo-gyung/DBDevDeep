package com.dbdevdeep.document.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.FileService;
import com.dbdevdeep.document.domain.FileDto;
import com.dbdevdeep.document.domain.MoveRequestDto;
import com.dbdevdeep.document.service.FolderService;

@Controller
public class FileApiController {
	
	private final FileService fileService;
	private final FolderService folderService;
	
	public FileApiController(FileService fileService, FolderService folderService) {
		this.fileService = fileService;
		this.folderService = folderService;
	}
	
	@ResponseBody
	@PostMapping("/file")
	public Map<String, String> uploadFile(FileDto dto, @RequestParam("folder_no") Long folderNo,
                              @RequestParam("files") List<MultipartFile> files){
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("res_code", "404");
        resultMap.put("res_msg", "파일 업로드 중 오류가 발생했습니다.");
        
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = (User)authentication.getPrincipal();
		String empId = user.getUsername();
		
		dto.setEmp_id(empId);

        List<String> savedFileNames = fileService.documentUploadFiles(files, folderNo, dto);

        if (savedFileNames != null) {
            resultMap.put("res_code", "200");
            resultMap.put("res_msg", "파일 업로드에 성공했습니다.");
        }

        return resultMap;
	}
	
	@ResponseBody
	@DeleteMapping("/file")
	public Map<String, String> deleteFolderAndFile(@RequestBody Map<String, List<Long>> requestBody) {
	    List<Long> fileNos = requestBody.get("fileNos");
	    List<Long> folderNos = requestBody.get("folderNos");
	    
		Map<String, String> resultMap = new HashMap<>();
		
        // 파일이 있을 경우 파일 삭제 로직 처리
        if (fileNos != null && !fileNos.isEmpty()) {
            for (Long fileNo : fileNos) {
                if (fileService.deleteFile(fileNo) > 0) {
                    resultMap.put("file_res_code", "200");
                    resultMap.put("file_res_msg", "파일 삭제를 성공했습니다.");
                }
            }
        }

        // 폴더가 있을 경우 폴더 삭제 로직 처리
        if (folderNos != null && !folderNos.isEmpty()) {
            for (Long folderNo : folderNos) {
                if (fileService.deleteFolder(folderNo) > 0) {
                    resultMap.put("folder_res_code", "200");
                    resultMap.put("folder_res_msg", "폴더 삭제를 성공했습니다.");
                }
            }
        }

        return resultMap;
	}
	
	@GetMapping("/download/{file_no}")
	public ResponseEntity<Object> downloadFile(@PathVariable("file_no") Long file_no){
		return fileService.downloadFile(file_no);
	}
	
	@ResponseBody
	@PutMapping("/file")
    public Map<String, String> moveFilesAndFolders(@RequestBody MoveRequestDto moveRequest) {
        Map<String, String> resultMap = new HashMap<>();
        
        // 이동될 폴더 번호를 가져옴
        Long targetFolderNo = moveRequest.getTargetFolderNo();

        // 파일 이동 로직 처리
        List<Long> fileNos = moveRequest.getFileNos();
        if (fileNos != null && !fileNos.isEmpty()) {
            for (Long fileNo : fileNos) {
                if (fileService.moveFile(fileNo, targetFolderNo) > 0) {
                    resultMap.put("file_res_code", "200");
                    resultMap.put("file_res_msg", "파일 이동을 성공했습니다.");
                }
            }
        }

        // 폴더 이동 로직 처리
        List<Long> folderNos = moveRequest.getFolderNos();
        if (folderNos != null && !folderNos.isEmpty()) {
            for (Long folderNo : folderNos) {
                if (folderService.moveFolder(folderNo, targetFolderNo) > 0) {
                    resultMap.put("folder_res_code", "200");
                    resultMap.put("folder_res_msg", "폴더 이동을 성공했습니다.");
                }
            }
        }

        return resultMap;
    }
}
