package solution.gdsc.PathPal.domain.inference.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionInfo {

    int totalExpectedMinutes;
    long recentlySaveTime;

    public void updateRecentlySaveTime(long updateValue) {
        this.recentlySaveTime = updateValue;
    }
}
