package asia.igsaas.repository;

import asia.igsaas.domain.Currency;
import asia.igsaas.domain.IncomeBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<IncomeBalance, Long> {

    List<IncomeBalance> findByChatIdAndIncomeDate(long chatId, LocalDate incomeDate);

    Optional<IncomeBalance> findByChatIdAndIncomeDateAndCurrency(long chatId, LocalDate incomeDate, Currency currency);
}
