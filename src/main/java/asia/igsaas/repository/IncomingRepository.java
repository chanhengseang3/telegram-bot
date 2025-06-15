package asia.igsaas.repository;

import asia.igsaas.domain.IncomingBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IncomingRepository extends JpaRepository<IncomingBalance, Long> {

    List<IncomingBalance> findByChatIdAndIncomeDate(long chatId, LocalDate incomeDate);
}
