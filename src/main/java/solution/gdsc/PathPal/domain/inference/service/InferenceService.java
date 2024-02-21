package solution.gdsc.PathPal.domain.inference.service;

import org.springframework.stereotype.Service;
import solution.gdsc.PathPal.domain.inference.domain.Direction;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.domain.Label;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        final double confidenceThreshold = 0.5;
        boolean isAlert = false;
        Set<LabelAndDirection> labelAndDirections = new HashSet<>();

        for (Inference inference : inferences) {
            if (inference.name().equals("planecrosswalk_broken")) {
                if (inference.confidence() < 0.2) {
                    continue;
                }
            }
            else {
                if (inference.confidence() < confidenceThreshold) {
                    continue;
                }
            }


            if (inference.name().equals("flatness_D") ||
                    inference.name().equals("flatness_E") ||
                    inference.name().equals("walkway_paved") ||
                    inference.name().equals("walkway_block") ||
                    inference.name().equals("paved_state_broken") ||
                    inference.name().equals("paved_state_normal") ||
                    inference.name().equals("block_state_normal") ||
                    inference.name().equals("block_kind_bad")
            ) {
                continue;
            }
//            if ((inference.down_y() + inference.up_y()) / 2 < 0.333) {
//                continue;
//            }

            Direction direction = Direction.fromCenterPoint((inference.left_x() + inference.right_x()) / 2);
            Label label = Label.fromName(inference.name());

            LabelAndDirection labelAndDirection = new LabelAndDirection(label, direction);
            if (labelAndDirections.contains(labelAndDirection)) {
                continue;
            }
            labelAndDirections.add(labelAndDirection);

            if (inference.alert()) {
                isAlert = true;
            }

            if (direction == Direction.LEFT) {
                appendToStringBuilder(left, label, direction);
            }
            else if (direction == Direction.CENTER) {
                appendToStringBuilder(center, label, direction);
            }
            else {
                appendToStringBuilder(right, label, direction);
            }
        }

        if (!left.isEmpty() && !(center.isEmpty() && right.isEmpty())) {
            left.append(", ");
        }
        if (!center.isEmpty() && !right.isEmpty()) {
            center.append(", ");
        }

        if (!(left.isEmpty() && center.isEmpty() && right.isEmpty())) {
            return "[{\"koreanTTSString\":\"" +
                    left.append(center).append(right) +
                    " 감지\", \"needAlert\": \"" + isAlert + "\"}]";
        }
        else {
            return "[]";
        }
    }

    private void appendToStringBuilder(StringBuilder sb, Label label, Direction direction) {
        if (sb.isEmpty()) {
            sb.append(direction.toKorean()).append("에 ");
        }
        else {
            sb.append(", ");
        }
        sb.append(label.toKorean());
    }

    private record LabelAndDirection(Label name, Direction direction) {
    }

}
