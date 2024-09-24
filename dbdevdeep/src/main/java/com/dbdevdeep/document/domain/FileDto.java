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
public class FileDto {
	private Long file_no;
	private String ori_file_name;
	private String new_file_name;
	private String file_extension;
	private Long file_size;
	private LocalDateTime reg_time;
	private LocalDateTime mod_time;
	
	private String emp_id;
	private String emp_name;
	private Long folder_no;
	
	public FileEntity toEntity(EmployeeRepository employeeRepository, FolderRepository folderRepository) {
		Employee employee = employeeRepository.findByempId(emp_id);
		Folder folder = folderRepository.findByFolderNo(folder_no);
		
		return FileEntity.builder()
				.fileNo(file_no)
				.oriFileName(ori_file_name)
				.newFileName(new_file_name)
				.fileExtension(file_extension)
				.fileSize(file_size)
				.regTime(reg_time)
				.modTime(mod_time)
				.employee(employee)
				.folder(folder)
				.build();
				
	}
	
	public FileDto toDto(FileEntity file) {
		return FileDto.builder()
				.file_no(file.getFileNo())
				.ori_file_name(file.getOriFileName())
				.new_file_name(file.getNewFileName())
				.file_extension(file.getFileExtension())
				.file_size(file.getFileSize())
				.reg_time(file.getRegTime())
				.mod_time(file.getModTime())
				.emp_id(file.getEmployee().getEmpId())
				.emp_name(file.getEmployee().getEmpName())
				.folder_no(file.getFolder().getFolderNo())
				.build();
	}
}
