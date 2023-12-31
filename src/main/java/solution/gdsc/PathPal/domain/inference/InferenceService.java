package solution.gdsc.PathPal.domain.inference;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class InferenceService {

    private final Double confidenceThreshold = 0.3;

    public List<InferenceTranslate> convertInference(List<Inference> inferences) {
        List<InferenceTranslate> result = new ArrayList<>();
        for (Inference inference : inferences) {
            if (inference.confidence() < confidenceThreshold) {
                continue;
            }

            Label label = getLabel(inference);
            Direction direction = getDirection(inference);

            InferenceTranslate inferenceTranslate = new InferenceTranslate(inference.alert(), label, direction);
            result.add(inferenceTranslate);
        }

        Collections.sort(result);
        return result;
    }

    private static Label getLabel(Inference inference) {
        String name = inference.name();
        return Label.valueOf(name);
    }

    private Direction getDirection(Inference inference) {
        double centerPointX = (inference.left_x() + inference.right_x()) / 2;
        if (centerPointX < 0.333) {
            return Direction.LEFT;
        }
        else if (centerPointX < 0.666) {
            return Direction.CENTER;
        }
        else {
            return Direction.RIGHT;
        }
    }
}
