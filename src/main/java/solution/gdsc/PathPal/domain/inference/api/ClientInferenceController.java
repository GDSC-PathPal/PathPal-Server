package solution.gdsc.PathPal.domain.inference.api;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import solution.gdsc.PathPal.domain.client.domain.Client;
import solution.gdsc.PathPal.domain.client.domain.ImageInference;
import solution.gdsc.PathPal.domain.client.repository.ClientRepository;
import solution.gdsc.PathPal.domain.client.repository.ImageInferenceRepository;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.service.InferenceService;
import solution.gdsc.PathPal.domain.inference.service.SocketClient;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class ClientInferenceController extends BinaryWebSocketHandler {

    private final Map<WebSocketSession, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final int savePeriodMilliSeconds = 5000;
    private final InferenceService inferenceService;
    private final ClientRepository clientRepository;
    private final ImageInferenceRepository imageInferenceRepository;
    private final SocketClient socketClient = new SocketClient("127.0.0.1", 9999, 2000);

    @Value("${image.path}")
    private String path;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer totalExpectedSeconds = parseTimeHeader(session);
        if (totalExpectedSeconds == null) {
            return;
        }

        Client client = new Client(totalExpectedSeconds);
        clientRepository.save(client);

        session.setBinaryMessageSizeLimit(1024 * 1024 * 10);
        long currentTimeMillis = System.currentTimeMillis();
        sessions.put(session, new SessionInfo(client, currentTimeMillis, totalExpectedSeconds));
    }

    private Integer parseTimeHeader(WebSocketSession session) {
        List<String> time = session.getHandshakeHeaders().get("time");
        if (time == null) {
            System.err.println("not found: time header");
            try {
                session.sendMessage(new TextMessage("not found: time header"));
                session.close();
            } catch (IOException e) {
            }
            return null;
        }
        System.out.println("time header: " + time.get(0));

        int totalExpectedSeconds;
        try {
            totalExpectedSeconds = Integer.parseInt(time.get(0));
        } catch (NumberFormatException e) {
            System.err.println("time header is not number");
            try {
                session.sendMessage(new TextMessage("time header is not number"));
                session.close();
            } catch (IOException e1) {
            }
            return null;
        }
        return totalExpectedSeconds;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        byte[] bytes = message.getPayload().array();
        String responseMessage = "";
        try {
            List<Inference> inferences = socketClient.inferenceImage(bytes);
            responseMessage = inferenceService.convertInference(inferences);
        } catch (Exception e) {
            responseMessage = "";
        }

        saveImageAndInferenceResult(session, bytes, responseMessage);

        try {
            session.sendMessage(new TextMessage(responseMessage));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveImageAndInferenceResult(WebSocketSession session, byte[] bytes, String responseMessage) {
        long currentTimeMillis = System.currentTimeMillis();

        SessionInfo sessionInfo = sessions.get(session);
        if (currentTimeMillis - sessionInfo.recentlySaveTime > savePeriodMilliSeconds) {

            String fileFullName = path + sessionInfo.getClient().getId() + "T" + currentTimeMillis + ".jpeg";
            try (FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileFullName))) {
                imageOutput.write(bytes, 0, bytes.length);

                ImageInference imageInference = new ImageInference(fileFullName, sessionInfo.getClient(), responseMessage);
                imageInferenceRepository.save(imageInference);

                sessionInfo.updateRecentlySaveTime(currentTimeMillis);
            } catch (Exception e) {
                System.err.println("fail save image");
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (status.equalsCode(CloseStatus.NORMAL)) {
            System.out.println("normal disconnect");
            Client client = sessions.get(session).getClient();
            client.updateResult(-1, solution.gdsc.PathPal.domain.client.domain.CloseStatus.NORMAL);
            clientRepository.save(client);
        }
        else {
            System.out.println("disconnect. reason = " + status.getReason() + "\n");
            Client client = sessions.get(session).getClient();
            client.updateResult(-1, solution.gdsc.PathPal.domain.client.domain.CloseStatus.convert(status));
            clientRepository.save(client);
        }
        sessions.remove(session);
    }
}
