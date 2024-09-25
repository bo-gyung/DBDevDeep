package com.dbdevdeep.document.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dbdevdeep.document.domain.FileDto;
import com.dbdevdeep.document.domain.FileEntity;
import com.dbdevdeep.document.domain.Folder;
import com.dbdevdeep.document.domain.FolderDto;
import com.dbdevdeep.document.repository.FileRepository;
import com.dbdevdeep.document.repository.FolderRepository;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@Service
public class FolderService {
	
	private String fileDir = "C:\\dbdevdeep\\";
	
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;
	private final EmployeeRepository employeeRepository;
	
	public FolderService(FolderRepository folderRepository, FileRepository fileRepository, EmployeeRepository employeeRepository) {
		this.folderRepository = folderRepository;
		this.fileRepository = fileRepository;
		this.employeeRepository = employeeRepository;
	}

    // 공용 폴더 목록 가져오기
	public List<Map<String, Object>> selectPublicFolderTree() {
		List<Folder> folderList = folderRepository.findByFolderType(0);
		return convertToTree(folderList);
	}

    // 개인 폴더 목록 가져오기
	public List<Map<String, Object>> selectPrivateFolderTree(String empId) {
		List<Folder> folderList = folderRepository.findByFolderTypeAndEmployee_EmpId(1, empId);
		return convertToTree(folderList);
	}

    // 트리 구조로 변환하는 메서드
	private List<Map<String, Object>> convertToTree(List<Folder> folderList) {
		List<Map<String, Object>> treeData = new ArrayList<>();
		// 부모 폴더가 없는 최상위 폴더를 먼저 찾고, 그 아래로 자식 폴더를 재귀적으로 추가
		for (Folder folder : folderList) {
			if (folder.getParentFolder() == null) {
				// 최상위 폴더를 트리에 추가
				Map<String, Object> folderNode = createFolderNode(folder, folderList);
				treeData.add(folderNode);
			}
		}
		return treeData;
	}

    // 폴더 노드 생성 및 자식 폴더 추가
	private Map<String, Object> createFolderNode(Folder folder, List<Folder> folderList) {
		Map<String, Object> node = new HashMap<>();
		node.put("id", folder.getFolderNo());
		node.put("text", folder.getFolderName());

		// 자식 폴더 찾기
		List<Map<String, Object>> children = new ArrayList<>();
		for (Folder childFolder : folderList) {
			if (folder.equals(childFolder.getParentFolder())) { // 부모-자식 관계 확인
				children.add(createFolderNode(childFolder, folderList));
			}
		}
		node.put("children", children); // 자식 폴더가 있으면 children에 추가
		return node;
	}

    public List<FolderDto> getChildFolders(Long folderNo) {
        Folder folder = folderRepository.findById(folderNo)
            .orElseThrow(() -> new RuntimeException("Folder not found"));
        List<Folder> childFolders = folderRepository.findByParentFolder(folder);
        
        List<FolderDto> folderDtoList = new ArrayList<>();
        for(Folder f : childFolders) {
        	FolderDto dto = new FolderDto().toDto(f);
        	folderDtoList.add(dto);
        }
        
        return folderDtoList;
    }

    public List<FileDto> getFilesInFolder(Long folderNo) {
        Folder folder = folderRepository.findById(folderNo)
            .orElseThrow(() -> new RuntimeException("Folder not found"));
        List<FileEntity> files = fileRepository.findByFolder(folder);

        List<FileDto> fileDtoList = new ArrayList<>();
        for(FileEntity f : files) {
        	FileDto dto = new FileDto().toDto(f);
        	fileDtoList.add(dto);
        }
        
        return fileDtoList;
    }
    
    // 폴더 번호로 폴더의 총 용량을 계산하는 메서드
    public Long calculateFolderTotalSize(Long folderNo) {
        // 폴더를 조회
        Folder folder = folderRepository.findById(folderNo)
            .orElseThrow(() -> new IllegalArgumentException("Invalid folder ID"));

        // 해당 폴더의 파일 크기 합산
        Long totalSize = fileRepository.getTotalFileSizeByFolderNo(folderNo);
        
        // 파일이 없을 경우 null을 반환하므로 이를 0L로 처리
        if (totalSize == null) {
            totalSize = 0L;
        }

        // 자식 폴더를 조회하여 재귀적으로 용량을 계산
        List<Folder> subFolders = folderRepository.findByParentFolder(folder);
        
        // 자식 폴더가 없으면 더 이상 재귀 호출하지 않음
        if (subFolders != null && !subFolders.isEmpty()) {
            for (Folder subFolder : subFolders) {
                totalSize += calculateFolderTotalSize(subFolder.getFolderNo());  // 재귀적으로 자식 폴더 용량 계산
            }
        }

        return totalSize;
    }
    
	public String getFolderPath(Long folderNo) {
	    List<String> folderNames = new ArrayList<>();
	    Folder currentFolder = folderRepository.findById(folderNo).orElseThrow(() -> new RuntimeException("폴더를 찾을 수 없습니다."));

	    while (currentFolder != null) {
	        folderNames.add(currentFolder.getFolderName());
	        currentFolder = currentFolder.getParentFolder();  // 상위 폴더로 이동
	    }

	    Collections.reverse(folderNames); // 최상위 폴더부터 경로를 만들기 위해 순서를 뒤집음
	    return String.join("\\", folderNames);  // 경로를 '\'로 연결
	}

	public int createFolder(Long folderNo, String folderName, String empId) {
		int result = -1;
		
		try {
	        // 상위 폴더 정보 가져오기
	        Folder parentFolder = folderRepository.findByFolderNo(folderNo);
	        Employee employee = employeeRepository.findByempId(empId);
	        if (parentFolder == null) {
	            throw new RuntimeException("부모 폴더를 찾을 수 없습니다.");
	        }
			
            // 상위 폴더 경로 구하기
            String parentFolderPath = getFolderPath(folderNo);

            // 새 폴더 경로
            String newFolderPath = fileDir + "document\\" + parentFolderPath + "\\" + folderName;

            // 실제 폴더 생성
            Path path = Paths.get(newFolderPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);  // 폴더가 없으면 생성
            }

            // 데이터베이스에 폴더 정보 저장
            Folder folder = Folder.builder()
            		.folderName(folderName)
            		.parentFolder(parentFolder)
            		.folderType(parentFolder.getFolderType())
            		.employee(employee)
            		.build();
            folderRepository.save(folder);
            
            result = 1;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		return result;
	}

	public int moveFolder(Long folderNo, Long targetFolderNo) {
	    int result = -1;

	    try {
	        // 이동할 폴더 정보 가져오기
	        Folder folderToMove = folderRepository.findByFolderNo(folderNo);
	        // 이동될 대상 폴더 정보 가져오기
	        Folder targetFolder = folderRepository.findByFolderNo(targetFolderNo);

	        if (folderToMove == null) {
	            throw new RuntimeException("이동할 폴더를 찾을 수 없습니다.");
	        }
	        if (targetFolder == null) {
	            throw new RuntimeException("이동될 대상 폴더를 찾을 수 없습니다.");
	        }

	        // 현재 폴더 경로 구하기
	        String currentFolderPath = getFolderPath(folderNo);

	        // 대상 폴더 경로 구하기
	        String targetFolderPath = getFolderPath(targetFolderNo);

	        // 새로운 경로로 폴더 이동
	        String newFolderPath = fileDir + "document\\" + targetFolderPath + "\\" + folderToMove.getFolderName();
	        Path currentPath = Paths.get(fileDir + "document\\" + currentFolderPath);
	        Path newPath = Paths.get(newFolderPath);

	        if (Files.exists(currentPath)) {
	            Files.move(currentPath, newPath);  // 폴더 경로 변경
	        } else {
	            throw new RuntimeException("이동할 폴더의 경로를 찾을 수 없습니다.");
	        }
	        
	        folderToMove.setParentFolder(targetFolder);
	        		
	        folderRepository.save(folderToMove);  // DB에서 상위 폴더 업데이트

	        result = 1;  // 성공

	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;  // 실패
	    }

	    return result;
	}


}
