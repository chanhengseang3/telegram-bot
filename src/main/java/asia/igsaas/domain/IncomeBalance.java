package asia.igsaas.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class IncomeBalance implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "chat_id")
    private Long chatId;

    private LocalDate incomeDate;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    private BigDecimal amount = BigDecimal.ZERO;

    public void addAmount(BigDecimal amount) {
        if (this.amount == null) {
            this.amount = amount;
        }
        this.amount = this.amount.add(amount);
    }
}
