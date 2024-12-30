package antifraud.dto.request;

import antifraud.model.SuspiciousIp;
import antifraud.validation.annotation.ValidIp;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SuspiciousIpRequestDTO {

    @NotBlank
    @ValidIp
    private String ip;

    public SuspiciousIp toSuspiciousIp() {
        return new SuspiciousIp(ip);
    }
}
