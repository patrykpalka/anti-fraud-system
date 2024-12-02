package antifraud.model;

import antifraud.validation.annotation.ValidCardNumber;
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
public class StolenCard implements RemovableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @ValidCardNumber
    private String number;

    public StolenCard(String number) {
        this.number = number;
    }

    @Override
    public String getStatusMessage() {
        return "Card " + number + " successfully removed!";
    }
}
