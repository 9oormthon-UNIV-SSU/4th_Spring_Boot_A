package study.goorm.domain.folder.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.folder.domain.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
}
