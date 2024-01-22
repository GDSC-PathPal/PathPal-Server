package solution.gdsc.PathPal.domain.inference.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solution.gdsc.PathPal.domain.client.domain.Client;

@Getter
@AllArgsConstructor
public class SessionInfo {

    Client client;
    long recentlySaveTime;
    int totalExpectedMinutes;

    public void updateRecentlySaveTime(long updateValue) {
        this.recentlySaveTime = updateValue;
    }
}
