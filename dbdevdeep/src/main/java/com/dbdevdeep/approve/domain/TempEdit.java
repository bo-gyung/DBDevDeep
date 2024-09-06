package com.dbdevdeep.approve.domain;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="temp_edit")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
@Getter
@Builder
public class TempEdit {
	
	@Id
    @Column(name = "temp_no")
    private Long tempNo;

    @Column(name = "temp_name")
    private String tempName;

    @Lob
    @Column(name = "temp_content")
    private String tempContent;

    @Column(name = "temp_type")
    private int tempType;
	
    @OneToMany(mappedBy = "tempEdit")
    private List<Approve> approves;

    @OneToMany(mappedBy = "tempEdit")
    private List<ApproDraft> approveDrafts;
}
