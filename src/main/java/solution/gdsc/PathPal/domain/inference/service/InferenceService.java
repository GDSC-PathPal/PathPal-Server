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

    public String convertInference2(List<Inference> inferences) {
        StringBuilder left = new StringBuilder();
        StringBuilder center = new StringBuilder();
        StringBuilder right = new StringBuilder();

        final double confidenceThreshold = 0.3;
        boolean isAlert = false;
        for (Inference inference : inferences) {
            if (inference.confidence() < confidenceThreshold) {
                continue;
            }
            if (inference.alert()) {
                isAlert = true;
            }
            Direction direction = Direction.fromCenterPoint((inference.left_x() + inference.right_x()) / 2);
            if (direction == Direction.LEFT) {
                if (left.isEmpty()) {
                    left.append(direction.toKorean()).append("에 ");
                }
                else {
                    left.append(", ");
                }
                String korean = Label.fromName(inference.name()).toKorean();
                left.append(korean);
            }
            else if (direction == Direction.CENTER) {
                if (center.isEmpty()) {
                    center.append(direction.toKorean()).append("에 ");
                }
                else {
                    center.append(", ");
                }
                String korean = Label.fromName(inference.name()).toKorean();
                center.append(korean);
            }
            else {
                if (right.isEmpty()) {
                    right.append(direction.toKorean()).append("에 ");
                }
                else {
                    right.append(", ");
                }
                String korean = Label.fromName(inference.name()).toKorean();
                right.append(korean);
            }
        }

        if (!left.isEmpty() && !center.isEmpty() && right.isEmpty()) {
            left.append(", ");
        }
        else if (!left.isEmpty() && center.isEmpty() && !right.isEmpty()) {
            left.append(", ");
        }
        else if (left.isEmpty() && !center.isEmpty() && !right.isEmpty()) {
            center.append(", ");
        }
        else if (!left.isEmpty() && !center.isEmpty() && !right.isEmpty()) {
            left.append(", ");
            center.append(", ");
        }

        /*
         sb.append("[");
        for (int i = 0; i < inferenceTranslates.size(); i++) {
            sb.append("{\"koreanTTSString\": \"");
            sb.append(inferenceTranslates.get(i).toKorean());
            sb.append("\", \"englishTTSString\": \"");
            sb.append(inferenceTranslates.get(i).toEnglish());
            sb.append("\", \"needAlert\": \"");
            sb.append(inferenceTranslates.get(i).isAlert());

            if (i != inferenceTranslates.size() - 1) {
                sb.append("\"},");
            }
            else {
                sb.append("\"}");
            }
        }
        sb.append("]");
         */

        if (!left.isEmpty() || !center.isEmpty() || !right.isEmpty()) {
            return "[{\"koreanTTSString\"" +
                    left.append(center).append(right) +
                    ", \"needAlert\": \"" + isAlert + "\"}]";
        }
        else {
            return "[]";
        }
    }

}
