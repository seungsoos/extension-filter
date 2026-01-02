package extension.filter.repository;

import extension.filter.entity.FixedExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FixedExtensionRepository extends JpaRepository<FixedExtension, Long> {

    Optional<FixedExtension> findByExtension(String extension);

    boolean existsByExtension(String extension);
}
