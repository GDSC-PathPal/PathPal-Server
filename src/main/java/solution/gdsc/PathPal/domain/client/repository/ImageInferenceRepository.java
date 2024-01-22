package solution.gdsc.PathPal.domain.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solution.gdsc.PathPal.domain.client.domain.ImageInference;

public interface ImageInferenceRepository extends JpaRepository<ImageInference, Long> {
}
