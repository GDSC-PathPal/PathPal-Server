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

    private final double confidenceThreshold = 0.5;

    public String convertInference(List<Inference> inferences) {
        StringBuilder left = new StringBuilder();
        StringBuilder center = new StringBuilder();
        StringBuilder right = new StringBuilder();
        Set<LabelAndDirection> labelAndDirections = new HashSet<>();
        boolean isAlert = false;

        for (Inference inference : inferences) {
            if (isUnderConfidenceThreshold(inference) ||
                    isUpsideObject(inference.up_y(), inference.down_y()) ||
                    isIgnoreLabel(inference.name())) {
                continue;
            }

            LabelAndDirection labelAndDirection = convertLabelAndDirection(inference.name(), inference.left_x(), inference.right_x());
            if (isAlreadyContain(labelAndDirections, labelAndDirection)) {
                continue;
            }

            isAlert |= inference.alert();
            labelAndDirections.add(labelAndDirection);
            appendToStringBuilder(labelAndDirection, left, center, right);
        }

        return convertToJson(left, center, right, isAlert);
    }

    private void appendToStringBuilder(LabelAndDirection labelAndDirection, StringBuilder left, StringBuilder center, StringBuilder right) {
        if (labelAndDirection.direction == Direction.LEFT) {
            appendToStringBuilder(left, labelAndDirection.name, labelAndDirection.direction);
        }
        else if (labelAndDirection.direction == Direction.CENTER) {
            appendToStringBuilder(center, labelAndDirection.name, labelAndDirection.direction);
        }
        else {
            appendToStringBuilder(right, labelAndDirection.name, labelAndDirection.direction);
        }
    }

    private String convertToJson(StringBuilder left, StringBuilder center, StringBuilder right, boolean isAlert) {
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

    private boolean isIgnoreLabel(String name) {
        return name.equals("brailleblock_dot") ||
                name.equals("brailleblock_line") ||
                name.equals("flatness_D") ||
                name.equals("flatness_E") ||
                name.equals("walkway_paved") ||
                name.equals("walkway_block") ||
                name.equals("paved_state_broken") ||
                name.equals("paved_state_normal") ||
                name.equals("block_state_normal") ||
                name.equals("block_kind_bad");
    }

    private LabelAndDirection convertLabelAndDirection(String name, double left_x, double right_x) {
        Label label = Label.fromName(name);
        Direction direction = Direction.fromCenterPoint((left_x + right_x) / 2);
        return new LabelAndDirection(label, direction);
    }

    private boolean isAlreadyContain(Set<LabelAndDirection> labelAndDirections, LabelAndDirection labelAndDirection) {
        return labelAndDirections.contains(labelAndDirection);
    }

    private boolean isUnderConfidenceThreshold(Inference inference) {
        return inference.confidence() < confidenceThreshold;
    }

    private boolean isUpsideObject(double up_y, double down_y) {
        return (up_y + down_y) / 2 < 0.333;
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
