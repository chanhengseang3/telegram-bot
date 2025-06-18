package asia.igsaas.service;

import asia.igsaas.data.Result;
import asia.igsaas.domain.IncomeBalance;
import asia.igsaas.repository.IncomeRepository;
import asia.igsaas.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final IncomeRepository incomingRepository;

    public IncomeBalance saveIncome(Long chatId, Result result) {
        final var existing = incomingRepository.findByChatIdAndIncomeDateAndCurrency(chatId, DateUtils.today(), result.currency());
        if (existing.isPresent()) {
            var incoming = existing.get();
            incoming.addAmount(result.amount());
            return incomingRepository.save(incoming);
        } else {
            final var incoming = new IncomeBalance()
                    .setIncomeDate(DateUtils.today())
                    .setAmount(result.amount())
                    .setCurrency(result.currency())
                    .setChatId(chatId);
            return incomingRepository.save(incoming);
        }
    }

    public List<IncomeBalance> getSummary(Long chatId, LocalDate incomeDate) {

        return incomingRepository.findByChatIdAndIncomeDate(chatId, incomeDate);
    }
}
