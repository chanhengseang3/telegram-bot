package asia.igsaas.service;

import asia.igsaas.data.BotMessageData;
import asia.igsaas.domain.IncomeBalance;
import asia.igsaas.repository.IncomeRepository;
import asia.igsaas.utils.PaywayParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotMessageService {

    private final IncomeRepository incomeRepository;
    private final IncomeService incomeService;

    public IncomeBalance save(BotMessageData data) {

        var parseResult = PaywayParser.parseAmountAndCurrency(data.getMessage());
        return incomeService.saveIncome(data.getChatId(), parseResult);

    }
}
