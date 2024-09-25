package com.dbdevdeep.document.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.dbdevdeep.employee.domain.Employee;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "folder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Builder
public class Folder {
	
	@Id
	@Column(name = "folder_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long folderNo;
	
	@Column(name = "folder_type")
	private int folderType;
	
	@Column(name = "folder_name")
	private String folderName;
	
	@Column(name = "reg_time")
    @CreationTimestamp
	private LocalDateTime regTime;
	
    @Column(name = "mod_time")
    @UpdateTimestamp
    private LocalDateTime modTime;
    
    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;
    
    // 부모 폴더를 참조하는 필드
    @ManyToOne
    @JoinColumn(name = "parent_folder_no")
    private Folder parentFolder;
    
    // 자식 폴더들을 참조하는 필드 (양방향 관계를 위해 추가 가능)
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private List<Folder> childFolders = new ArrayList<>();
}
