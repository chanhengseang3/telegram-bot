package asia.igsaas.service;

import asia.igsaas.data.Result;
import asia.igsaas.domain.IncomingBalance;
import asia.igsaas.repository.ChatRepository;
import asia.igsaas.repository.IncomingRepository;
import asia.igsaas.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomingService {

    private final ChatRepository chatRepository;
    private final IncomingRepository incomingRepository;

    public void saveIncome(Long chatId, Result result) {
        final var incoming = new IncomingBalance()
                .setIncomeDate(DateUtils.today())
                .setAmount(result.amount())
                .setCurrency(result.currency())
                .setChatId(chatId);
        incomingRepository.save(incoming);
    }

    public List<IncomingBalance> getSummary(Long chatId, LocalDate incomeDate) {

        return incomingRepository.findByChatIdAndIncomeDate(chatId, incomeDate);
    }
}
