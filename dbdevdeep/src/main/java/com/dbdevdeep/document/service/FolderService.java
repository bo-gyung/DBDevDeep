package com.dbdevdeep.document.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.dbdevdeep.document.domain.Folder;
import com.dbdevdeep.document.domain.FolderDto;
import com.dbdevdeep.document.repository.FolderRepository;
import com.dbdevdeep.employee.repository.EmployeeRepository;

@Service
public class FolderService {
	private final FolderRepository folderRepository;
	private final EmployeeRepository employeeRepository;
	
	public FolderService(FolderRepository folderRepository, EmployeeRepository employeeRepository) {
		this.folderRepository = folderRepository;
		this.employeeRepository = employeeRepository;
	}
	
	public List<FolderDto> selectPublicFolderList() {
		List<Folder> folderList = folderRepository.findByFolderType(0);
		
		List<FolderDto> folderDtoList = new ArrayList<FolderDto>();
		for(Folder f : folderList) {
			FolderDto dto = new FolderDto().toDto(f);
			folderDtoList.add(dto);
		}
		
		return folderDtoList;
	}

	public List<FolderDto> selectPrivateFolderList(String empId) {
		List<Folder> folderList = folderRepository.findByFolderTypeAndEmployee_EmpId(1,empId);
		
		List<FolderDto> folderDtoList = new ArrayList<FolderDto>();
		for(Folder f : folderList) {
			FolderDto dto = new FolderDto().toDto(f);
			folderDtoList.add(dto);
		}
		
		return folderDtoList;
	}

}
