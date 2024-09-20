package com.dbdevdeep;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dbdevdeep.approve.domain.ApproFile;
import com.dbdevdeep.approve.domain.Approve;
import com.dbdevdeep.approve.repository.ApproFileRepository;
import com.dbdevdeep.approve.repository.ApproveRepository;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;
import com.dbdevdeep.place.domain.Place;
import com.dbdevdeep.place.repository.PlaceRepository;

@Service
public class FileService {

	// fileDir 변경 X
	private String fileDir = "C:\\dbdevdeep\\";

	private final EmployeeRepository employeeRepository;
	private final ApproFileRepository approFileRepository;
	private final ApproveRepository approveRepository;
	private final PlaceRepository placeRepository;
	
	@Autowired
	public FileService(EmployeeRepository employeeRepository, ApproFileRepository approFileRepository,
			ApproveRepository approveRepository, PlaceRepository placeRepository) {
		this.employeeRepository = employeeRepository;
		this.approFileRepository = approFileRepository;
		this.approveRepository = approveRepository;
		this.placeRepository = placeRepository;
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

	
	
	
	
	
	
	
	
	
	
	
	
}
