package solution.gdsc.PathPal.domain.inference;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class InferenceTranslate implements Comparable<InferenceTranslate> {
    @Getter
    private final boolean alert;
    private final Label name;
    private final Direction direction;

    public String toKorean() {
        return direction.toKorean() + "에 " + name.toKorean() + " 감지";
    }

    public String toEnglish() {
        return "Detected " + name.toEnglish() + " on the " + direction.toEnglish();
    }


    @Override
    public int compareTo(InferenceTranslate inferenceTranslate) {
        int thisScore = 0;
        if (direction == Direction.LEFT) thisScore += 100;
        else if (direction == Direction.CENTER) thisScore += 50;
        if (alert) thisScore += 10;

        int thereScore = 0;
        if (inferenceTranslate.direction == Direction.LEFT) thereScore += 100;
        else if (inferenceTranslate.direction == Direction.CENTER) thereScore += 50;
        if (inferenceTranslate.alert) thereScore += 10;

        return thereScore - thisScore;
    }
}
