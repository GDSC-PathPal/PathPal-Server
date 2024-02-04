package solution.gdsc.PathPal.domain.inference.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solution.gdsc.PathPal.domain.client.domain.Client;

@Getter
@AllArgsConstructor
public class SessionInfo {

    Client client;
    long recentlySaveTime;
    int totalExpectedSeconds;

    public void updateRecentlySaveTime(long updateValue) {
        this.recentlySaveTime = updateValue;
    }
}
