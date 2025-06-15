package asia.igsaas.repository;

import asia.igsaas.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {


}
