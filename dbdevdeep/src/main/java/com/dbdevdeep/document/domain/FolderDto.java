package com.dbdevdeep.document.domain;

import java.time.LocalDateTime;

import com.dbdevdeep.document.repository.FolderRepository;
import com.dbdevdeep.employee.domain.Employee;
import com.dbdevdeep.employee.repository.EmployeeRepository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class FolderDto {
	private Long folder_no;
	private int folder_type;
	private String folder_name;
	private LocalDateTime reg_time;
	private LocalDateTime mod_time;
	
	private String emp_id;
	private String emp_name;
	private Long parent_folder_no;
	
	public Folder toEntity(EmployeeRepository employeeRepository, FolderRepository folderRepository) {
	    Employee employee = employeeRepository.findByempId(emp_id);  // emp_id로 Employee 엔티티를 조회
	    // findById는 항상 Optional 타입으로 반환
	    // .orElse(null): Optional 에서 값이 존재하면 해당 값을 반환, 값이 없으면 null 을 반환
	    Folder parentFolder = parent_folder_no != null ? folderRepository.findById(parent_folder_no).orElse(null) : null;  
	    
	    return Folder.builder()
	        .folderNo(folder_no) 
	        .folderType(folder_type)
	        .folderName(folder_name)
	        .regTime(reg_time)
	        .modTime(mod_time)
	        .employee(employee)
	        .parentFolder(parentFolder)
	        .build();
	}
	
	public FolderDto toDto(Folder folder) {
		return FolderDto.builder()
				.folder_no(folder.getFolderNo())
				.folder_type(folder.getFolderType())
				.folder_name(folder.getFolderName())
				.reg_time(folder.getRegTime())
				.mod_time(folder.getModTime())
				.emp_id(folder.getEmployee().getEmpId())
				.emp_name(folder.getEmployee().getEmpName())
				.parent_folder_no(folder.getParentFolder().getFolderNo())
				.build();
	}

}
