package study.goorm.domain.folder.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.goorm.domain.folder.domain.entity.ClothFolder;

public interface ClothFolderRepository extends JpaRepository<ClothFolder, Long>{
}
