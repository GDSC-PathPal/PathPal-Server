package solution.gdsc.PathPal.domain.inference;

import org.junit.jupiter.api.Test;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.service.InferenceService;
import solution.gdsc.PathPal.domain.inference.service.InferenceTranslate;
import solution.gdsc.PathPal.global.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

class InferenceServiceTest {

    @Test
    void testInferenceService() {
        InferenceService inferenceService = new InferenceService();

        List<Inference> inferences = new ArrayList<>();
        inferences.add(new Inference("steepramp", 0.9, 0.1, 0.2, false));
        inferences.add(new Inference("trash_can", 0.1, 0.4, 0.9, false));
        inferences.add(new Inference("stair_broken", 0.4, 0.2, 0.4, true));
        inferences.add(new Inference("restsapce", 0.6, 0.5, 0.6, true));

        List<InferenceTranslate> inferenceTranslates = inferenceService.convertInference(inferences);
        String jsonFormat = JsonUtil.toJsonFormat(inferenceTranslates);
        System.out.println(jsonFormat);
    }

}