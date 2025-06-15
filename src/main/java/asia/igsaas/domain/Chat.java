package asia.igsaas.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Entity
public class Chat implements Serializable {

    @Id
    private Long id;

    private Long fromSender;

}
