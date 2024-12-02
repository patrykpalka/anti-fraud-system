package antifraud.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SuspiciousIp implements RemovableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String ip;

    public SuspiciousIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String getStatusMessage() {
        return "IP " + ip + " successfully removed!";
    }
}
