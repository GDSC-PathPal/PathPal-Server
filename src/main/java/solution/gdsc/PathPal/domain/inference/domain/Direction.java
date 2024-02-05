package solution.gdsc.PathPal.domain.inference.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Direction {
    LEFT("왼쪽", "left"),
    CENTER("중앙", "center"),
    RIGHT("오른쪽", "right");

    private final String korean;
    private final String english;

    public static Direction fromCenterPoint(double centerPointX) {
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

    public String toKorean() {
        return korean;
    }

    public String toEnglish() {
        return english;
    }
}
