package solution.gdsc.PathPal.domain.client.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solution.gdsc.PathPal.domain.client.domain.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
}
