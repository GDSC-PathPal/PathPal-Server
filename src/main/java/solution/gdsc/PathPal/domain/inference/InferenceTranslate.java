package solution.gdsc.PathPal.domain.inference;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InferenceTranslate {
    private final boolean alert;
    private final Label name;
    private final Direction direction;

    public String toKorean() {
        return direction.toKorean() + "에 " + name.toKorean() + " 감지";
    }

    public String toEnglish() {
        return "Detected " + name.toEnglish() + " on the " + direction.toEnglish();
    }

    public boolean isAlert() {
        return alert;
    }
}
