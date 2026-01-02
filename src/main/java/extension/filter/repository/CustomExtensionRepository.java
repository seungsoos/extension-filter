package extension.filter.repository;

import extension.filter.entity.CustomExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomExtensionRepository extends JpaRepository<CustomExtension, Long> {

    boolean existsByExtension(String extension);

    long countBy();
}
