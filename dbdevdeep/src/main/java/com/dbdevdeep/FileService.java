package com.dbdevdeep;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.approve.domain.ApproFile;
import com.dbdevdeep.approve.domain.Approve;
import com.dbdevdeep.approve.repository.ApproFileRepository;
import com.dbdevdeep.approve.repository.ApproveRepository;
import com.dbdevdeep.document.domain.FileDto;
import com.dbdevdeep.document.domain.FileEntity;
import com.dbdevdeep.document.domain.Folder;
import com.dbdevdeep.document.repository.FileRepository;
import com.dbdevdeep.document.repository.FolderRepository;
import com.dbdevdeep.document.service.FolderService;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.place.domain.Item;
import com.dbdevdeep.place.domain.Place;
import com.dbdevdeep.place.repository.ItemRepository;
import com.dbdevdeep.place.repository.PlaceRepository;
import com.dbdevdeep.student.domain.Student;
import com.dbdevdeep.student.repository.StudentRepository;

@Service
public class FileService {

	// fileDir 변경 X
	private String fileDir = "C:\\dbdevdeep\\";

	private final EmployeeRepository employeeRepository;
	private final ApproFileRepository approFileRepository;
	private final ApproveRepository approveRepository;
	private final PlaceRepository placeRepository;
	private final FolderService folderService;
	private final ItemRepository itemRepository;
	private final StudentRepository studentRepository;
	private final FolderRepository folderRepository;
	private final FileRepository fileRepository;
	
	@Autowired
	public FileService(EmployeeRepository employeeRepository, ApproFileRepository approFileRepository,
			ApproveRepository approveRepository, PlaceRepository placeRepository, 
			FolderRepository folderRepository, FileRepository fileRepository, @Lazy FolderService folderService,
			ItemRepository itemRepository, StudentRepository studentRepository) {
		this.employeeRepository = employeeRepository;
		this.approFileRepository = approFileRepository;
		this.approveRepository = approveRepository;
		this.placeRepository = placeRepository;

		this.itemRepository = itemRepository;

		this.studentRepository = studentRepository;

		this.folderRepository = folderRepository;
		this.fileRepository = fileRepository;
		this.folderService = folderService;
	}
	
	// 장소이미지 삭제
		public int placeDelete(Long place_no) {
			int result = -1;
			
			try {
				Place p = placeRepository.findByplaceNo(place_no);
				
				String newPicName = p.getNewPicName();
				String resultDir = fileDir + "place\\" + URLDecoder.decode(newPicName,"UTF-8");
				
				
				if(newPicName == null || newPicName.isEmpty()) {
					// 파일명이 비어있거나, null일때
					return 1;
				}
				
				
				if(resultDir != null && resultDir.isEmpty() == false) {
					File file = new File(resultDir);
					
					if(file.exists()) {
						file.delete();
						result = 1;
					}
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
		
		// 장소 이미지등록
		public String placeUpload(MultipartFile file) {
			
			String newPicName = null;
			
			try {
				// 1. 파일 원래 이름
				String oriPicName = file.getOriginalFilename();
				// 2. 파일 자르기 (자료형 떼서 UUID로 바꾸기 위해)
				String fileExt = oriPicName.substring(oriPicName.lastIndexOf("."),oriPicName.length());
				// 3. 파일 명칭 바꾸기
				UUID uuid = UUID.randomUUID();
				// 4. 8자리마다 포함되는 하이픈 제거
				String uniqueName = uuid.toString().replaceAll("-", "");
				// 5. 새로운 파일명
				newPicName = uniqueName + fileExt;
				// 7. 파일 껍데기 생성
				File saveFile = new File(fileDir  +"place\\" + newPicName);
				// 8. 경로 존재 여부 확인
				if (!saveFile.exists()) {
					saveFile.mkdirs();
				}
				// 9. 껍데기에 파일 정보 복제
				file.transferTo(saveFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return newPicName;
			}

			// 기자재 이미지등록
			public String itemUpload(MultipartFile file) {
			
			String newPicName = null;
			
			try {
				// 1. 파일 원래 이름
				String oriPicName = file.getOriginalFilename();
				// 2. 파일 자르기 (자료형 떼서 UUID로 바꾸기 위해)
				String fileExt = oriPicName.substring(oriPicName.lastIndexOf("."),oriPicName.length());
				// 3. 파일 명칭 바꾸기
				UUID uuid = UUID.randomUUID();
				// 4. 8자리마다 포함되는 하이픈 제거
				String uniqueName = uuid.toString().replaceAll("-", "");
				// 5. 새로운 파일명
				newPicName = uniqueName + fileExt;
				// 7. 파일 껍데기 생성
				File saveFile = new File(fileDir  +"place\\item\\" + newPicName);
				// 8. 경로 존재 여부 확인
				if (!saveFile.exists()) {
					saveFile.mkdirs();
				}
				// 9. 껍데기에 파일 정보 복제
				file.transferTo(saveFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return newPicName;
			}
		
			// 기자재 이미지 삭제
			public int itemDelete(Long item_no) {
				int result = -1;
				
				try {
					Item i = itemRepository.findByitemNo(item_no);
					
					String newPicName = i.getNewPicName();
					String resultDir = fileDir + "place\\item\\" + URLDecoder.decode(newPicName,"UTF-8");
					
					
					if(newPicName == null || newPicName.isEmpty()) {
						// 파일명이 비어있거나, null일때
						return 1;
					}
					
					
					if(resultDir != null && resultDir.isEmpty() == false) {
						File file = new File(resultDir);
						
						if(file.exists()) {
							file.delete();
							result = 1;
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
				return result;
			}

	public int employeePicDelete(String emp_id) {
		int result = -1;
		
		try {
			Employee e = employeeRepository.findByempId(emp_id);
			String newFileName = e.getNewPicName(); // UUID
			String resultDir = fileDir + "employee\\" + URLDecoder.decode(newFileName, "UTF-8"); // 본인 폴더 지정
			if (resultDir != null && resultDir.isEmpty() == false) {
				File file = new File(resultDir);
				if (file.exists()) {
					file.delete();
					result = 1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String employeePicUpload(MultipartFile file) {

		String newFileName = null;

		try {
			// 1. 파일 원래 이름
			String oriFileName = file.getOriginalFilename();
			// 2. 파일 자르기
			String fileExt = oriFileName.substring(oriFileName.lastIndexOf("."), oriFileName.length());
			// 3. 파일 명칭 바꾸기
			UUID uuid = UUID.randomUUID();
			// 4. 8자리마다 포함되는 하이픈 제거
			String uniqueName = uuid.toString().replaceAll("-", "");
			// 5. 새로운 파일명
			newFileName = uniqueName + fileExt;
			// 6. 파일 저장 경로 설정
			// 7. 파일 껍데기 생성
			File saveFile = new File(fileDir + "employee\\" + newFileName);
			// 8. 경로 존재 여부 확인
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			}
			// 9. 껍데기에 파일 정보 복제
			file.transferTo(saveFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFileName;
	}

	public String employeeSignPicUpload(MultipartFile file) {
		String newFileName = null;

		try {
			// 1. 파일 원래 이름
			String oriFileName = file.getOriginalFilename();
			// 2. 파일 자르기
			String fileExt = oriFileName.substring(oriFileName.lastIndexOf("."), oriFileName.length());
			// 3. 파일 명칭 바꾸기
			UUID uuid = UUID.randomUUID();
			// 4. 8자리마다 포함되는 하이픈 제거
			String uniqueName = uuid.toString().replaceAll("-", "");
			// 5. 새로운 파일명
			newFileName = uniqueName + fileExt;
			// 6. 파일 저장 경로 설정
			// 7. 파일 껍데기 생성
			File saveFile = new File(fileDir + "empSign\\" + newFileName);
			// 8. 경로 존재 여부 확인
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			}
			// 9. 껍데기에 파일 정보 복제
			file.transferTo(saveFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFileName;
	}

	public String approveUpload(MultipartFile file) {

		String newFileName = null;

		try {
			// 1. 파일 원래 이름
			String oriFileName = file.getOriginalFilename();
			// 2. 파일 자르기
			String fileExt = oriFileName.substring(oriFileName.lastIndexOf("."), oriFileName.length());
			// 3. 파일 명칭 바꾸기
			UUID uuid = UUID.randomUUID();
			// 4. 8자리마다 포함되는 하이픈 제거
			String uniqueName = uuid.toString().replaceAll("-", "");
			// 5. 새로운 파일명
			newFileName = uniqueName + fileExt;
			// 6. 파일 저장 경로 설정
			// 7. 파일 껍데기 생성
			File saveFile = new File(fileDir + "approve\\" + newFileName);
			// 8. 경로 존재 여부 확인
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			}
			// 9. 껍데기에 파일 정보 복제
			file.transferTo(saveFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFileName;
	}

	public int approFileDelete(Long appro_no) {
		int result = -1;

		try {
			Approve approve = approveRepository.findById(appro_no).orElse(null);
			if (approve == null) {
				return result; // Approve 객체가 없는 경우
			}

			List<ApproFile> files = approFileRepository.findAllByApprove(approve);
			if (files == null || files.isEmpty()) {
				return 0; // 삭제할 파일이 없는 경우
			}

			for (ApproFile file : files) {
				String newFileName = file.getNewFile();
				String resultDir = fileDir + "approve\\" + URLDecoder.decode(newFileName, "UTF-8");

				if (resultDir != null && !resultDir.isEmpty()) {
					File actualFile = new File(resultDir);
					if (actualFile.exists()) {
						actualFile.delete();
					}
				}
				approFileRepository.delete(file); // 데이터베이스에서 파일 정보 삭제
			}
			result = 1; // 모든 파일이 성공적으로 삭제됨
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public String studentPicUpload(MultipartFile file) {
			
			String newFileName = null;
			
			try {
				// 1. 파일 원래 이름
				String oriFileName = file.getOriginalFilename();
				// 2. 파일 자르기 (자료형 떼서 UUID로 바꾸기 위해)
				String fileExt = oriFileName.substring(oriFileName.lastIndexOf("."),oriFileName.length());
				// 3. 파일 명칭 바꾸기
				UUID uuid = UUID.randomUUID();
				// 4. 8자리마다 포함되는 하이픈 제거
				String uniqueName = uuid.toString().replaceAll("-", "");
				// 5. 새로운 파일명
				newFileName = uniqueName+fileExt;
				// 7. 파일 껍데기 생성
				File saveFile = new File(fileDir + "student\\" + newFileName);
				// 8. 경로 존재 여부 확인
				if(!saveFile.exists()) {
					saveFile.mkdirs();
				}
				// 9. 껍데기에 파일 정보 복제
				file.transferTo(saveFile);
			}catch(Exception e) {
				e.printStackTrace();
			}
			return newFileName; 
		}
	
	
	public int studentPicDelete(Long student_no) {
		int result = -1;
		try {
			Student student = studentRepository.findBystudentNo(student_no);
			
			String newFileName = student.getStudentNewPic();	// UUID
			String oriFileName = student.getStudentOriPic();	// 사용자가 아는 파일명
			
			if (newFileName == null || newFileName.isEmpty()) {
	            // 파일명이 null 또는 빈 문자열일 경우 바로 성공 처리
	            return 1;
	        }
			
			String resultDir = fileDir + "student\\" + URLDecoder.decode(newFileName, "UTF-8");
			
			if(resultDir != null && resultDir.isEmpty() == false) {
				File file = new File(resultDir);
				
				if(file.exists()) {
					file.delete();
					result = 1;
				}
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
    @Value("${folder.total-capacity}")
    private String totalCapacityConfig;

    // 폴더의 총 용량을 바이트로 변환해서 반환하는 메서드
    public Long getFolderTotalCapacity() {
        // 설정된 총 용량을 바이트로 변환 (예: "100GB")
        Long totalCapacityInBytes = convertCapacityToBytes(totalCapacityConfig);
        return totalCapacityInBytes;
    }

    // "100GB"와 같은 문자열을 바이트(Long)로 변환하는 메서드
    private Long convertCapacityToBytes(String capacity) {
        if (capacity.toLowerCase().endsWith("gb")) {
            Long value = Long.parseLong(capacity.replaceAll("[^0-9]", ""));
            return value * 1024 * 1024 * 1024; // GB -> Bytes
        } else if (capacity.toLowerCase().endsWith("mb")) {
            Long value = Long.parseLong(capacity.replaceAll("[^0-9]", ""));
            return value * 1024 * 1024; // MB -> Bytes
        } else {
            throw new IllegalArgumentException("지원되지 않는 용량 단위입니다.");
        }
    }

	public List<String> documentUploadFiles(List<MultipartFile> files, Long folderNo, FileDto dto) {
	    List<String> savedFileNames = new ArrayList<>();
	    
	    long totalFileSize = files.stream().mapToLong(MultipartFile::getSize).sum(); // 업로드할 모든 파일의 총 크기 계산

	    // 1. 폴더의 현재 총 용량 계산
	    Long currentFolderSize = folderService.calculateFolderTotalSize(folderNo, dto.getEmp_id());
	    // 2. 폴더의 최대 허용 용량 설정
	    // long maxFolderCapacity = 100L * 1024 * 1024;
	    long maxFolderCapacity = getFolderTotalCapacity();

	    // 3. 파일 업로드가 가능한지 확인
	    if (currentFolderSize + totalFileSize > maxFolderCapacity) {
	        throw new IllegalStateException("문서함의 총 용량을 초과하였습니다.");
	    }

	    for (MultipartFile file : files) {
	        // 각 파일마다 새로운 DTO를 생성해서 전송
	        FileDto fileDto = new FileDto();
	        fileDto.setEmp_id(dto.getEmp_id());
	        fileDto.setReg_time(dto.getReg_time());
	        fileDto.setMod_time(dto.getMod_time());

	        String savedFileName = documentUploadFile(file, folderNo, fileDto);
	        if (savedFileName != null) {
	            savedFileNames.add(savedFileName);
	        }
	    }
	    return savedFileNames;
	}


	private String documentUploadFile(MultipartFile file, Long folderNo, FileDto dto) {
		String newFileName = null;

		try {
			// 1. 파일 원래 이름
			String oriFileName = file.getOriginalFilename();
			// 2. 파일 자르기
			String fileExt = oriFileName.substring(oriFileName.lastIndexOf("."), oriFileName.length());
			// 3. 파일 명칭 바꾸기
			UUID uuid = UUID.randomUUID();
			// 4. 8자리마다 포함되는 하이픈 제거
			String uniqueName = uuid.toString().replaceAll("-", "");
			// 5. 새로운 파일명
			newFileName = uniqueName + fileExt;
			// 6. 파일 저장 경로 설정
			String folderPath = getFolderPath(folderNo);
			// 7. 파일 껍데기 생성
	        File saveFile = new File(fileDir + "document\\" + folderPath + "\\" + newFileName);
			// 8. 경로 존재 여부 확인
			if (!saveFile.exists()) {
				saveFile.mkdirs();
			}
			// 9. 껍데기에 파일 정보 복제
			file.transferTo(saveFile);
			
			Employee employee = employeeRepository.findByempId(dto.getEmp_id());
			Folder folder = folderRepository.findByFolderNo(folderNo);
			
	        // 10. 파일 정보 데이터베이스에 저장
	        FileEntity fileEntity = FileEntity.builder()
	        		.oriFileName(oriFileName)
	        		.newFileName(newFileName)
	        		.fileExtension(fileExt)
	        		.fileSize(file.getSize())
	        		.employee(employee)
	        		.folder(folder)
	        		.regTime(dto.getReg_time())
	        		.modTime(dto.getMod_time())
	        		.build();

	        fileRepository.save(fileEntity);  // 파일 정보 저장
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newFileName;
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

	public int deleteFile(Long fileNo) {
	    int result = -1;  // 기본적으로 실패를 나타내는 값

	    try {
	        // 파일 엔티티 조회
	        FileEntity fileEntity = fileRepository.findById(fileNo).orElse(null);
	        if (fileEntity == null) {
	            return result; // FileEntity 객체가 없는 경우
	        }

	        // 파일 경로 설정 (폴더 경로 포함)
	        Long folderNo = fileEntity.getFolder().getFolderNo();
	        String folderPath = getFolderPath(folderNo);  // 경로 계산
	        String newFileName = fileEntity.getNewFileName();
	        String resultDir = fileDir + "document\\" + folderPath + "\\" + URLDecoder.decode(newFileName, "UTF-8");

	        // 실제 파일 삭제 로직
	        if (resultDir != null && !resultDir.isEmpty()) {
	            File actualFile = new File(resultDir);
	            if (actualFile.exists()) {
	                if (!actualFile.delete()) {
	                    throw new IOException("Failed to delete the file: " + resultDir);
	                }
	            }
	        }

	        // 데이터베이스에서 파일 정보 삭제
	        fileRepository.delete(fileEntity);

	        result = 1;  // 모든 작업이 성공적으로 완료되었을 경우

	    } catch (Exception e) {
	        e.printStackTrace();  // 예외 발생 시 스택 트레이스 출력
	    }

	    return result;
	}

	public int deleteFolder(Long folderNo) {
		int result = -1;  // 기본적으로 실패를 나타내는 값

	    try {
	        // 1. 폴더 조회
	        Folder folder = folderRepository.findById(folderNo).orElse(null);
	        if (folder == null) {
	            return result;  // 폴더가 존재하지 않으면 실패로 반환
	        }

	        // 2. 폴더 경로 계산
	        String folderPath = getFolderPath(folderNo);

	        // 3. 폴더 내 파일 삭제
	        List<FileEntity> files = fileRepository.findByFolder(folder);
	        for (FileEntity file : files) {
	            deleteFile(file.getFileNo());  // 파일 삭제 (이미 구현된 파일 삭제 로직 사용)
	        }

	        // 4. 하위 폴더 삭제 (재귀적으로)
	        List<Folder> subFolders = folderRepository.findByParentFolder(folder);
	        for (Folder subFolder : subFolders) {
	            deleteFolder(subFolder.getFolderNo());  // 하위 폴더 재귀 삭제
	        }

	        // 5. 실제 폴더 삭제 (파일 시스템 상에서)
	        File folderToDelete = new File(fileDir + "document\\" + folderPath);
	        if (folderToDelete.exists() && folderToDelete.isDirectory()) {
	            // 폴더 내부가 비었는지 확인한 후 삭제
	            if (!folderToDelete.delete()) {
	                throw new IOException("폴더 삭제 실패: " + folderPath);
	            }
	        }

	        // 6. 데이터베이스에서 폴더 정보 삭제
	        folderRepository.delete(folder);

	        result = 1;  // 성공적으로 삭제된 경우

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return result;
	}

	public ResponseEntity<Object> downloadFile(Long file_no) {
		
		try {
			FileEntity f = fileRepository.findByFileNo(file_no);
			
			// 파일 정보에서 폴더 번호를 통해 경로 조회
	        Long folderNo = f.getFolder().getFolderNo();
	        String folderPath = getFolderPath(folderNo); // 폴더 경로 계산
	        
	        // 새 파일 이름 및 경로 설정
	        String newFileName = f.getNewFileName();
	        String oriFileName = URLEncoder.encode(f.getOriFileName(), "UTF-8"); 
	        String downDir = fileDir + "document\\" + folderPath + "\\" + URLDecoder.decode(newFileName, "UTF-8");
			
			// 파일 정보 찾기
			Path filePath = Paths.get(downDir);
			Resource resource = new InputStreamResource(Files.newInputStream(filePath));
			
			// 파일 정보 꺼내와서 담기
			File file = new File(downDir);
			HttpHeaders headers = new HttpHeaders();
			
			// 파일 다운로드 화면 표현(이전 파일 이름 보여주기)
			headers.setContentDisposition(ContentDisposition.builder("attachment").filename(oriFileName).build());
			
			// 파일 정보 / 사용자가 보는 파일 정보 / 다운로드 성공
			return new ResponseEntity<Object>(resource,headers,HttpStatus.OK);
			
		} catch (Exception e) {
			e.printStackTrace();
			// 다운로드 충돌
			return new ResponseEntity<Object>(null,HttpStatus.CONFLICT);
		}
	}

	public int moveFile(FileEntity file, Folder currentFolder, Long targetFolderNo) {
	    int result = -1;

	    try {
	        // 1. 현재 파일 경로 계산
	        String currentFolderPath = getFolderPath(currentFolder.getFolderNo());  // 현재 폴더 경로
	        String fileName = URLDecoder.decode(file.getNewFileName(), "UTF-8");

	        // 2. 이동될 폴더 경로 계산 (폴더 경로 유지)
	        Folder targetFolder = folderRepository.findById(targetFolderNo)
	                .orElseThrow(() -> new RuntimeException("대상 폴더를 찾을 수 없습니다."));
	        String targetFolderPath = getFolderPath(targetFolder.getFolderNo());

	        // 폴더 내부 구조를 유지하기 위해 파일 경로를 새롭게 계산
	        String newFilePath = fileDir + "document\\" + targetFolderPath + "\\" + fileName;

	        Path currentPath = Paths.get(fileDir + "document\\" + currentFolderPath + "\\" + fileName);
	        Path newPath = Paths.get(newFilePath);

	        // 3. 실제 파일 이동
	        if (Files.exists(currentPath)) {
	            Files.move(currentPath, newPath, StandardCopyOption.REPLACE_EXISTING);  // 파일을 새로운 경로로 이동
	        } else {
	            throw new RuntimeException("이동할 파일의 경로를 찾을 수 없습니다: " + currentPath);
	        }

	        // 4. 데이터베이스에서 파일의 폴더 정보 업데이트 (이동될 폴더)
	        file.setFolder(targetFolder);  // 새로운 폴더 정보 설정
	        file.setModTime(LocalDateTime.now());
	        fileRepository.save(file);

	        result = 1;  // 성공

	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;  // 실패
	    }

	    return result;
	}


	public int copyFile(Long fileNo, Long targetFolderNo, String empId) {
	    int result = -1;  // 기본적으로 실패를 나타내는 값

	    try {
	    	Employee employee = employeeRepository.findByempId(empId);
	    	
	        // 1. 파일 정보 가져오기
	        FileEntity originalFile = fileRepository.findById(fileNo)
	            .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
	        Folder targetFolder = folderRepository.findById(targetFolderNo)
	            .orElseThrow(() -> new RuntimeException("대상 폴더를 찾을 수 없습니다."));
	        
	        // 2. 개인문서함(대상 폴더)의 현재 사용 용량 가져오기
	        Long currentUsedSize = folderService.calculateFolderTotalSize(targetFolderNo, empId);
	        
	        // 3. 복사하려는 파일의 크기 가져오기
	        Long fileSize = originalFile.getFileSize();

	        // 4. 개인문서함의 총 용량 가져오기 (100GB = 100 * 1024 * 1024 * 1024 바이트)
	        Long totalCapacity = getFolderTotalCapacity();

	        // 5. 현재 용량 + 복사하려는 파일 크기 비교
	        if (currentUsedSize + fileSize > totalCapacity) {
	            throw new RuntimeException("개인문서함의 용량을 초과하여 파일을 복사할 수 없습니다.");
	        }

	        // 6. 원본 파일 경로 계산
	        Long currentFolderNo = originalFile.getFolder().getFolderNo();
	        String currentFolderPath = getFolderPath(currentFolderNo);
	        String originalFileName = URLDecoder.decode(originalFile.getNewFileName(), "UTF-8");

	        // 7. 새로운 파일 이름 생성 (복사본이므로 다른 파일 이름을 사용)
	        String fileExt = originalFile.getFileExtension();
	        UUID uuid = UUID.randomUUID();
	        String newFileName = uuid.toString().replaceAll("-", "") + fileExt;

	        // 8. 복사할 경로 계산
	        String targetFolderPath = getFolderPath(targetFolderNo);
	        Path originalPath = Paths.get(fileDir + "document\\" + currentFolderPath + "\\" + originalFileName);
	        Path copyPath = Paths.get(fileDir + "document\\" + targetFolderPath + "\\" + newFileName);

	        // 9. 파일 복사
	        if (Files.exists(originalPath)) {
	            Files.copy(originalPath, copyPath, StandardCopyOption.REPLACE_EXISTING);
	        } else {
	            throw new RuntimeException("복사할 파일의 경로를 찾을 수 없습니다.");
	        }

	        // 10. 새로운 파일 정보를 데이터베이스에 저장 (복사본)
	        FileEntity copiedFile = FileEntity.builder()
	            .oriFileName(originalFile.getOriFileName())  // 원본 파일 이름 그대로 사용
	            .newFileName(newFileName)  // 새로운 파일 이름
	            .fileExtension(fileExt)  // 확장자는 동일
	            .fileSize(originalFile.getFileSize())  // 파일 크기는 동일
	            .employee(employee)  // 작성자 정보 그대로
	            .folder(targetFolder)  // 대상 폴더로 이동
	            .regTime(LocalDateTime.now())  // 복사된 파일의 등록 시간 갱신
	            .modTime(LocalDateTime.now())  // 수정 시간도 갱신
	            .build();

	        // 11. 데이터베이스에 파일 정보 저장
	        fileRepository.save(copiedFile);

	        result = 1;  // 모든 작업이 성공적으로 완료된 경우

	    } catch (Exception e) {
	        e.printStackTrace();
	        result = 0;  // 실패 시
	    }

	    return result;
	}

	public List<Long> getFileSize(List<Long> fileNos) {
        List<Long> fileSizes = new ArrayList<>();
        
        for (Long fileNo : fileNos) {
            FileEntity file = fileRepository.findById(fileNo)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다. 파일 번호: " + fileNo));

            fileSizes.add(file.getFileSize());  // 파일 크기를 리스트에 추가
        }

        return fileSizes;
	}

	public List<FileDto> findPublicFilesOrderByModTimeDesc() {
	    Pageable limit = PageRequest.of(0, 6); // 첫 번째 페이지, 6개 항목
	    List<FileEntity> fileEntities = fileRepository.findByFolder_FolderTypeOrderByRegTimeDesc(0, limit);
	    
	    return fileEntities.stream()
	            .map(file -> new FileDto().toDto(file))
	            .collect(Collectors.toList());
	}
	
}

