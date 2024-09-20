package com.dbdevdeep.approve.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.dbdevdeep.employee.domain.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="appro_draft")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class ApproDraft {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "draft_no")
    private Long draftNo;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "temp_no")
    private TempEdit tempEdit;
    
    @Column(name = "draft_name")
    private String draftName;

    @Column(name = "appro_time")
    private LocalDateTime approTime;

    @Column(name = "appro_type")
    private int approType;

    @Column(name = "appro_title")
    private String approTitle;

    @Lob
    @Column(name = "appro_content")
    private String approContent;

    @Column(name = "ori_file")
    private String oriFile;

    @Column(name = "new_file")
    private String newFile;

    @Column(name = "file_root")
    private String fileRoot;

    @Column(name = "vac_type")
    private int vacType;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "consult_draft_root")
    private String consultDraftRoot;
    
    @Column(name = "approval_draft_root")
    private String approvalDraftRoot;
    
    @Column(name = "ref_draft_root")
    private String refDraftRoot;
    
}
