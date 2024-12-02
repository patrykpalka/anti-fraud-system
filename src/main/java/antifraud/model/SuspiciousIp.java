package antifraud.model;

import antifraud.validation.annotation.ValidIp;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SuspiciousIp implements RemovableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @ValidIp
    private String ip;

    public SuspiciousIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String getStatusMessage() {
        return "IP " + ip + " successfully removed!";
    }
}
