package solution.gdsc.PathPal.domain.client.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long expectedSeconds;
    private long realSeconds;
    private int rating;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CloseStatus closeStatus;

    public Client(long expectedSeconds) {
        this.expectedSeconds = expectedSeconds;
        this.closeStatus = CloseStatus.OPEN;
        this.realSeconds = -1;
        this.rating = -1;
    }

    public void updateResult(long realSeconds, int rating, CloseStatus closeStatus) {
        this.realSeconds = realSeconds;
        this.rating = rating;
        this.closeStatus = closeStatus;
    }
}
