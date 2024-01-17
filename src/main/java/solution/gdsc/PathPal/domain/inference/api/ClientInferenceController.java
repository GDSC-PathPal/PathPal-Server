package solution.gdsc.PathPal.domain.inference.api;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.service.InferenceService;
import solution.gdsc.PathPal.domain.inference.service.InferenceTranslate;
import solution.gdsc.PathPal.domain.inference.service.SocketClient;
import solution.gdsc.PathPal.global.util.JsonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

//@Component
public class ClientInferenceController extends WebSocketClientController {

    private final String hostName = "127.0.0.1";
    //private final String hostName = "34.22.89.175";
    private final int port = 9999;

    private final InferenceService inferenceService;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final SocketClient socketClient;

    public ClientInferenceController(InferenceService inferenceService) {
        this.inferenceService = inferenceService;
        this.socketClient = new SocketClient(hostName, port, 2000);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        session.setBinaryMessageSizeLimit(1024 * 1024 * 10);
        sessions.add(session);


        List<String> time = session.getHandshakeHeaders().get("time");
        if (time == null) {
            System.err.println("time 헤더가 없습니다.");
        }
        System.out.println("time.size(): " + time.size());
        System.out.println("time: " + time.get(0));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        byte[] bytes = message.getPayload().array();

        String responseMessage = "";
        try {
            List<Inference> inferences = socketClient.inferenceImage(bytes);
            System.out.println("Inference 변환 성공");
            List<InferenceTranslate> inferenceTranslates = inferenceService.convertInference(inferences);
            System.out.println("Translate 성공");
            responseMessage = JsonUtil.toJsonFormat(inferenceTranslates);
            System.out.println("json 변환 성공");
        } catch (Exception e) {
            System.err.println("추론 실패");
            responseMessage = "[]";
        }

        try {
            System.out.println("전송 메시지: " + responseMessage);
            session.sendMessage(new TextMessage(responseMessage));
        } catch (IOException e) {
            System.err.println("전송실패");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}
