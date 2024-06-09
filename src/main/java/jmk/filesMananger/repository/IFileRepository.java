package jmk.filesMananger.repository;

import jmk.filesMananger.entity.FileInfo;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IFileRepository extends JpaRepository<FileInfo,Long> {
    Optional<FileInfo> findByName(String name);
}
