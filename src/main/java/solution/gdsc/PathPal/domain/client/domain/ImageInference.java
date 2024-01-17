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

    @Embedded
    private InferenceResult inferenceResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    public ImageInference(String imagePath, Client client) {
        this.imagePath = imagePath;
        this.client = client;
    }

    public void updateInferenceResult(InferenceResult inferenceResult) {
        this.inferenceResult = inferenceResult;
    }
}
