package solution.gdsc.PathPal.domain.inference.api;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.service.InferenceService;
import solution.gdsc.PathPal.domain.inference.service.InferenceTranslate;
import solution.gdsc.PathPal.domain.inference.service.SocketClient;
import solution.gdsc.PathPal.global.util.JsonUtil;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ClientInferenceController extends WebSocketClientController {

    private final String hostName = "127.0.0.1";
    private final int port = 9999;
    private String path = "/home/hsk4991149/static/image/";
    private final AtomicInteger imageNumber = new AtomicInteger(1);

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
            int imageId = imageNumber.get();
            String fileFullName = path + imageId + ".jpeg";
            System.out.println(fileFullName);

            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileFullName));
            imageOutput.write(bytes, 0, bytes.length);
            imageOutput.close();
            imageNumber.incrementAndGet();
            System.out.println("이미지 저장 성공. id = " + imageId);
        } catch (Exception e) {
            System.out.println("이미지 저장 실패");
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
