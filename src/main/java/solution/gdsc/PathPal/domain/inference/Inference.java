package solution.gdsc.PathPal.domain.inference;

public record Inference(String name, double confidence, double left_x, double right_x, boolean alert) {
}

