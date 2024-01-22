package solution.gdsc.PathPal.domain.client.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ImageInference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imagePath;

//    @Embedded
//    private InferenceResult inferenceResult;

    private String sendMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    public ImageInference(String imagePath, Client client, String sendMessage) {
        this.client = client;
        this.imagePath = imagePath;
        this.sendMessage = sendMessage;
    }

}
