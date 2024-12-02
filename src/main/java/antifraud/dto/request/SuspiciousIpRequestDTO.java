package antifraud.dto.request;

import antifraud.model.SuspiciousIp;
import antifraud.validation.annotation.ValidIp;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuspiciousIpRequestDTO {
    @NotBlank
    @ValidIp
    private String ip;

    public SuspiciousIp toSuspiciousIp() {
        return new SuspiciousIp(ip);
    }
}
