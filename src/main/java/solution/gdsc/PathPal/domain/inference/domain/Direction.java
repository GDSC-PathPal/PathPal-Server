package solution.gdsc.PathPal.domain.inference.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Direction {
    LEFT("왼쪽", "left"),
    CENTER("중앙", "center"),
    RIGHT("오른쪽", "right");

    private final String korean;
    private final String english;

    public String toKorean() {
        return korean;
    }

    public String toEnglish() {
        return english;
    }
}
