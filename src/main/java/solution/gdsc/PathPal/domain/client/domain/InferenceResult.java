package solution.gdsc.PathPal.domain.client.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solution.gdsc.PathPal.domain.inference.domain.Direction;
import solution.gdsc.PathPal.domain.inference.domain.Label;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InferenceResult {

    private boolean alert;
    private double confidence;

    @Enumerated(value = EnumType.STRING)
    private Label name;

    @Enumerated(value = EnumType.STRING)
    private Direction direction;

    public InferenceResult(Label name, Direction direction, double confidence, boolean alert) {
        this.name = name;
        this.direction = direction;
        this.confidence = confidence;
        this.alert = alert;
    }
}
