package solution.gdsc.PathPal.domain.inference;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientInferenceController extends BinaryWebSocketHandler {

    @Value("${image.path}")
    private String path;

    private final InferenceService inferenceService;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public ClientInferenceController(InferenceService inferenceService) {
        this.inferenceService = inferenceService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        session.setBinaryMessageSizeLimit(1024 * 1024 * 10);
        sessions.add(session);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        byte[] bytes = message.getPayload().array();

        String responseMessage = "";
        try {
            // TODO. ML 모델 추론
            List<Inference> inferences = new ArrayList<>();
            List<InferenceTranslate> inferenceTranslates = inferenceService.convertInference(inferences);

            responseMessage = JsonUtil.toJsonFormat(inferenceTranslates);
        } catch (Exception e) {
            responseMessage = "추론실패";
        }
        try {
            session.sendMessage(new TextMessage(responseMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
    }
}
