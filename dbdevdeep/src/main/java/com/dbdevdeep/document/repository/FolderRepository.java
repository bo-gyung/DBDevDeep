package com.dbdevdeep.document.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dbdevdeep.document.domain.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long>{

	List<Folder> findByFolderTypeOrderByFolderNameAsc(int folderType);

	List<Folder> findByFolderTypeAndEmployee_EmpIdOrderByFolderNameAsc(int folderType, String empId);

	Folder findByFolderNo(Long folderNo);

	Folder findByFolderTypeAndEmployee_EmpIdIsNull(int folderType);

	List<Folder> findByParentFolderAndEmployee_EmpId(Folder rootFolder, String empId);

	Folder findByFolderNoAndEmployee_EmpId(Long folderNo, String empId);

	List<Folder> findByParentFolder(Folder folderToMove);

	Folder findByFolderName(String folderName);

}
