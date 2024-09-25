package com.dbdevdeep.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.document.domain.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long>{

	List<Folder> findByFolderType(int folderType);

	List<Folder> findByFolderTypeAndEmployee_EmpId(int folderType, String empId);

	Folder findByFolderNo(Long folderNo);

	List<Folder> findByParentFolder(Folder folder);

	Folder findByFolderTypeAndEmployee_EmpIdIsNull(int folderType);

	List<Folder> findByParentFolderAndEmployee_EmpId(Folder rootFolder, String empId);

	Folder findByFolderNoAndEmployee_EmpId(Long folderNo, String empId);

}
