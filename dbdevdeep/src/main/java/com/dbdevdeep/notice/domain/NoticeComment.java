package com.dbdevdeep.notice.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.dbdevdeep.employee.domain.Employee;

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

@Entity
@Table(name="notice_comment")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class NoticeComment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cmt_no")
	private Long cmtNo;
	
	@ManyToOne
	@JoinColumn(name="notice_no")
	private Notice notice;
	
	@ManyToOne
	@JoinColumn(name="writer_id")
	private Employee employee;
	
	@ManyToOne
	@JoinColumn(name="parent_cmt_no", nullable=true)
	private NoticeComment parentComment;
	
	// 대댓글 관련
    @OneToMany(mappedBy = "parentComment")
    private List<NoticeComment> childComments;
	
    @Column(name="cmt_content")
    private String cmtContent;
    
	@Column(name="reg_time")
	private LocalDateTime regTime;
	
	@Column(name="mod_time")
	private LocalDateTime modTime;
	
	@Column(name="is_delete")
	private int isDelete;
	
	
}
