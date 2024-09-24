package com.dbdevdeep.document.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.dbdevdeep.employee.domain.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class FileEntity {
	
	@Id
	@Column(name = "file_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long fileNo;
	
	@Column(name = "ori_file_name")
	private String oriFileName;
	
	@Column(name = "new_file_name")
	private String newFileName;
	
	@Column(name = "file_extension")
	private String fileExtension;
	
	@Column(name = "file_size")
	private Long fileSize;
	
	@Column(name = "reg_time")
    @CreationTimestamp
	private LocalDateTime regTime;
	
    @Column(name = "mod_time")
    @UpdateTimestamp
    private LocalDateTime modTime;
    
    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;
    
    @ManyToOne
    @JoinColumn(name = "folder_no")
    private Folder folder;
}
