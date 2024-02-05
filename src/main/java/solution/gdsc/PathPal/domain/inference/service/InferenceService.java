package solution.gdsc.PathPal.domain.inference.service;

import org.springframework.stereotype.Service;
import solution.gdsc.PathPal.domain.inference.domain.Direction;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.domain.Label;

import java.util.List;

@Service
public class InferenceService {

    private final double confidenceThreshold = 0.3;

    public List<InferenceTranslate> convertInference(List<Inference> inferences) {
        return inferences.stream()
                .filter(inference -> inference.confidence() >= confidenceThreshold)
                .map(inference -> {
                    Label label = Label.fromName(inference.name());
                    Direction direction = Direction.fromCenterPoint((inference.left_x() + inference.right_x()) / 2);
                    return new InferenceTranslate(inference.alert(), label, direction);
                })
                .sorted()
                .toList();
    }

}
