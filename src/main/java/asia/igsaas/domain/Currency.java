package asia.igsaas.domain;

import lombok.Getter;

@Getter
public enum Currency {
    USD("$"),
    KHR("៛");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }
}
