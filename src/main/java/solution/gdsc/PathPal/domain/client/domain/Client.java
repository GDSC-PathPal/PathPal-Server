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

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private long expectedSeconds;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private CloseStatus closeStatus;

    public Client(long expectedSeconds) {
        this.rating = -1;
        this.closeStatus = CloseStatus.OPEN;
        this.expectedSeconds = expectedSeconds;
    }

    public void updateResult(int rating, CloseStatus closeStatus) {
        this.rating = rating;
        this.closeStatus = closeStatus;
    }
}
