package com.dbdevdeep.document.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.dbdevdeep.FileService;
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
	private final FileService fileService;
	
	@Autowired
	public FolderService(FolderRepository folderRepository, FileRepository fileRepository, EmployeeRepository employeeRepository
			, @Lazy FileService fileService) {
		this.folderRepository = folderRepository;
		this.fileRepository = fileRepository;
		this.employeeRepository = employeeRepository;
		this.fileService = fileService;
	}

    // 공용 폴더 목록 가져오기
	public List<Map<String, Object>> selectPublicFolderTree() {
		List<Folder> folderList = folderRepository.findByFolderType(0);
		return convertToTree(folderList);
	}

    // 개인 폴더 목록 가져오기
	public List<Map<String, Object>> selectPrivateFolderTree(String empId) {
	    // 1. 개인업무폴더(루트 폴더)를 먼저 가져오기 (empId가 null인 경우)
	    Folder rootFolder = folderRepository.findByFolderTypeAndEmployee_EmpIdIsNull(1);
	    
	    // 2. 해당 사용자의 하위 폴더 목록 가져오기
	    List<Folder> folderList = folderRepository.findByFolderTypeAndEmployee_EmpId(1, empId);
	    
	    folderList.add(0, rootFolder);  // 루트 폴더를 맨 앞에 추가
	    
	    // 3. 폴더 목록을 트리 형태로 변환
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

    public List<FolderDto> getChildFolders(Long folderNo, String empId) {
        Folder folder = folderRepository.findById(folderNo)
            .orElseThrow(() -> new RuntimeException("Folder not found"));

        List<Folder> childFolders;
        
        // folder_type에 따라 공용 폴더인지 개인 폴더인지 구분
        if (folder.getFolderType() == 0) {
            // 공용 폴더의 경우 모든 하위 폴더 조회
            childFolders = folderRepository.findByParentFolder(folder);
        } else if (folder.getFolderType() == 1) {
            // 개인 폴더의 경우 empId로 필터링하여 하위 폴더 조회
            childFolders = folderRepository.findByParentFolderAndEmployee_EmpId(folder, empId);
        } else {
            throw new RuntimeException("Unknown folder type");
        }
        
        List<FolderDto> folderDtoList = new ArrayList<>();
        for(Folder f : childFolders) {
        	FolderDto dto = new FolderDto().toDto(f);
        	folderDtoList.add(dto);
        }
        
        return folderDtoList;
    }

    public List<FileDto> getFilesInFolder(Long folderNo, String empId) {
        Folder folder = folderRepository.findById(folderNo)
            .orElseThrow(() -> new RuntimeException("Folder not found"));

        List<FileEntity> files;

        // folder_type에 따라 공용 폴더인지 개인 폴더인지 구분
        if (folder.getFolderType() == 0) {
            // 공용 폴더의 경우 모든 파일 조회
            files = fileRepository.findByFolder(folder);
        } else if (folder.getFolderType() == 1) {
            // 개인 폴더의 경우 empId로 필터링하여 파일 조회
            files = fileRepository.findByFolderAndEmployee_EmpId(folder, empId);
        } else {
            throw new RuntimeException("Unknown folder type");
        }

        List<FileDto> fileDtoList = new ArrayList<>();
        for(FileEntity f : files) {
        	FileDto dto = new FileDto().toDto(f);
        	fileDtoList.add(dto);
        }
        
        return fileDtoList;
    }
    
    // 폴더 번호로 폴더의 총 용량을 계산하는 메서드
    public Long calculateFolderTotalSize(Long folderNo, String empId) {
        Long totalSize = 0L;

        // 폴더를 조회
        Folder folder = folderRepository.findById(folderNo)
            .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 폴더 ID"));

        // 공용문서함 처리 (folderType == 0)
        if (folder.getFolderType() == 0) {
        	totalSize = fileRepository.getTotalFileSizeWithSubFolders(folderNo);
        } 
        // 개인문서함 처리 (folderType == 1)
        else if (folder.getFolderType() == 1) {
        	totalSize = fileRepository.getTotalFileSizeWithSubFoldersByEmpId(folderNo, empId);
        }

        return totalSize == null ? 0L : totalSize;
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

	        // 데이터베이스에서 상위 폴더 업데이트
	        folderToMove.setParentFolder(targetFolder);
	        folderToMove.setModTime(LocalDateTime.now());
	        folderRepository.save(folderToMove);  // DB에서 상위 폴더 업데이트

	        // 현재 폴더 내 하위 폴더들도 재귀적으로 이동
	        List<Folder> subFolders = folderRepository.findByParentFolder(folderToMove);
	        for (Folder subFolder : subFolders) {
	            moveFolder(subFolder.getFolderNo(), folderToMove.getFolderNo());  // 재귀적으로 하위 폴더 이동
	        }

	        result = 1;  // 성공

	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;  // 실패
	    }

	    return result;
	}

	public int copyFolder(Long folderNo, Long targetFolderNo, String empId) {
	    int result = -1;

	    try {
	        Employee employee = employeeRepository.findByempId(empId);

	        // 1. 복사할 폴더 정보 가져오기
	        Folder folderToCopy = folderRepository.findByFolderNo(folderNo);
	        Folder targetFolder = folderRepository.findByFolderNo(targetFolderNo);

	        if (folderToCopy == null || targetFolder == null) {
	            throw new RuntimeException("복사할 폴더 또는 대상 폴더를 찾을 수 없습니다.");
	        }

	        // 2. 대상 폴더의 현재 사용 용량 계산
	        Long currentUsedSize = calculateFolderTotalSize(targetFolderNo, empId);

	        // 3. 복사할 폴더 내 모든 파일들의 용량 합산
	        Long totalSizeToCopy = 0L;
	        List<FileEntity> filesInFolder = fileRepository.findByFolder(folderToCopy);
	        for (FileEntity file : filesInFolder) {
	            totalSizeToCopy += file.getFileSize();
	        }

	        // 4. 개인문서함의 총 용량 가져오기 (예: 100GB)
	        Long totalCapacity = fileService.getFolderTotalCapacity();

	        // 5. 용량 비교 (현재 사용 용량 + 복사할 용량 > 총 용량)
	        if (currentUsedSize + totalSizeToCopy > totalCapacity) {
	            throw new RuntimeException("개인문서함의 용량을 초과하여 폴더를 복사할 수 없습니다.");
	        }

	        // 6. 새로운 폴더 정보 생성 및 데이터베이스에 저장 (폴더 복사)
	        Folder copiedFolder = Folder.builder()
	        		.folderName(folderToCopy.getFolderName())
	        		.folderType(1)
	        		.parentFolder(targetFolder)
	        		.employee(employee)
	        		.regTime(LocalDateTime.now())
	        		.modTime(LocalDateTime.now())
	        		.build();
	        folderRepository.save(copiedFolder);
	        
	        // 실제 경로에 폴더 생성
	        String targetFolderPath = getFolderPath(targetFolderNo);
	        String newFolderPath = fileDir + "document\\" + targetFolderPath + "\\" + copiedFolder.getFolderName();
	        Path newFolderPathAsPath = Paths.get(newFolderPath);

	        // 폴더가 존재하지 않으면 생성
	        if (!Files.exists(newFolderPathAsPath)) {
	            Files.createDirectories(newFolderPathAsPath);  // 대상 폴더가 없으면 생성
	        }

	        // 7. 폴더 내 파일 복사 처리
	        for (FileEntity file : filesInFolder) {
	            // 파일 복사 (복사된 폴더로)
	            fileService.copyFile(file.getFileNo(), copiedFolder.getFolderNo(), empId);
	        }

	        // 8. 폴더 내 하위 폴더들도 재귀적으로 복사
	        List<Folder> subFolders = folderRepository.findByParentFolder(folderToCopy);
	        for (Folder subFolder : subFolders) {
	            // 하위 폴더 복사 (재귀적으로 처리)
	            copyFolder(subFolder.getFolderNo(), copiedFolder.getFolderNo(), empId);
	        }

	        result = 1;  // 성공

	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;  // 실패 시
	    }

	    return result;
	}

	public Long getTotalSizeOfFolders(List<Long> folderNos) {
	    Long totalSize = 0L;

	    for (Long folderNo : folderNos) {
	        // 폴더에 속한 모든 파일들의 크기 합산
	        totalSize += fileRepository.getTotalFileSizeWithSubFolders(folderNo);  // 폴더 내 파일들의 총 크기 계산
	    }

	    return totalSize;
	}

}
