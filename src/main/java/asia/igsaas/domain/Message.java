package asia.igsaas.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Message {

    @Id
    private Integer messageId;

    private boolean isAwaitForReply = true;

    private boolean isReplied = false;
}
