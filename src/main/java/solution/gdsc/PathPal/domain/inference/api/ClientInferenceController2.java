package solution.gdsc.PathPal.domain.inference.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import solution.gdsc.PathPal.domain.client.domain.Client;
import solution.gdsc.PathPal.domain.client.domain.ImageInference;
import solution.gdsc.PathPal.domain.client.repository.ClientRepository;
import solution.gdsc.PathPal.domain.client.repository.ImageInferenceRepository;
import solution.gdsc.PathPal.domain.inference.domain.Direction;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.domain.Label;
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
public class ClientInferenceController2 extends WebSocketClientController {

    private final Map<WebSocketSession, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final int savePeriodMilliSeconds = 5000;
    private final byte[] scoreByte = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35};
    private final InferenceService inferenceService;
    private final ClientRepository clientRepository;
    private final ImageInferenceRepository imageInferenceRepository;
    private final SocketClient socketClient = new SocketClient("127.0.0.1", 9999, 2000);

    //@Value("${image.path}")
    private String path = "/home/hsk4991149/static/image/";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Integer totalExpectedSeconds = parseTimeHeader(session);
        if (totalExpectedSeconds == null) return;

        Client client = new Client(totalExpectedSeconds);
        clientRepository.save(client);

        session.setBinaryMessageSizeLimit(1024 * 1024 * 10);
        long currentTimeMillis = System.currentTimeMillis();
        sessions.put(session, new SessionInfo(client, currentTimeMillis, totalExpectedSeconds));
    }

    private Integer parseTimeHeader(WebSocketSession session) {
        List<String> time = session.getHandshakeHeaders().get("time");
        if (time == null) {
            System.err.println("time 헤더가 없습니다.");
            try {
                session.sendMessage(new TextMessage("time 헤더가 없습니다."));
                session.close();
            } catch (IOException e) {
            }
            return null;
        }
        System.out.println("time 헤더: " + time.get(0));

        int totalExpectedSeconds;
        try {
            totalExpectedSeconds = Integer.parseInt(time.get(0));
        } catch (NumberFormatException e) {
            System.err.println("time 헤더가 숫자가 아닙니다.");
            try {
                session.sendMessage(new TextMessage("time 헤더가 숫자가 아닙니다."));
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
//        if (bytes.length == 1) {
//            for (int i = 0; i <= 5; i++) {
//                if (bytes[0] == scoreByte[i]) {
//                    System.out.println("점수 : " + i);
//                    Client client = sessions.get(session).getClient();
//                    client.updateResult(i, solution.gdsc.PathPal.domain.client.domain.CloseStatus.NORMAL);
//                    clientRepository.save(client);
//                }
//            }
//
//            try {
//                session.close();
//            } catch (IOException e) {
//            }
//            return;
//        }

        String responseMessage = "";
        try {
            List<Inference> inferences = socketClient.inferenceImage(bytes);
            System.out.println("Inference 변환 성공");
//            List<InferenceTranslate> inferenceTranslates = inferenceService.convertInference(inferences);
//            System.out.println("Translate 성공");
//            responseMessage = JsonUtil.toJsonFormat(inferenceTranslates);
//            System.out.println("json 변환 성공");

            StringBuilder left = new StringBuilder();
            StringBuilder center = new StringBuilder();
            StringBuilder right = new StringBuilder();

            final double confidenceThreshold = 0.3;

            for (Inference inference : inferences) {
                if (inference.confidence() < confidenceThreshold) {
                    continue;
                }
                Direction direction = Direction.fromCenterPoint((inference.left_x() + inference.right_x()) / 2);
                if (direction == Direction.LEFT) {
                    if (left.isEmpty()) {
                        left.append(direction.toKorean()).append("에 ");
                    }
                    else {
                        left.append(", ");
                    }
                    String korean = Label.fromName(inference.name()).toKorean();
                    left.append(korean);
                }
                else if (direction == Direction.CENTER) {
                    if (center.isEmpty()) {
                        center.append(direction.toKorean()).append("에 ");
                    }
                    else {
                        center.append(", ");
                    }
                    String korean = Label.fromName(inference.name()).toKorean();
                    center.append(korean);
                }
                else {
                    if (right.isEmpty()) {
                        right.append(direction.toKorean()).append("에 ");
                    }
                    else {
                        right.append(", ");
                    }
                    String korean = Label.fromName(inference.name()).toKorean();
                    right.append(korean);
                }
            }

            if (!left.isEmpty() && !center.isEmpty() && right.isEmpty()) {
                left.append(", ");
            }
            else if (!left.isEmpty() && center.isEmpty() && !right.isEmpty()) {
                left.append(", ");
            }
            else if (left.isEmpty() && !center.isEmpty() && !right.isEmpty()) {
                center.append(", ");
            }
            else if (!left.isEmpty() && !center.isEmpty() && !right.isEmpty()) {
                left.append(", ");
                center.append(", ");
            }

            if (!left.isEmpty() || !center.isEmpty() || !right.isEmpty()) {
                responseMessage = left.append(center).append(right).toString();
            }
            else {
                responseMessage = "";
            }

        } catch (Exception e) {
            System.err.println("추론 실패");
            responseMessage = "";
        }

        long currentTimeMillis = System.currentTimeMillis();

        SessionInfo sessionInfo = sessions.get(session);
        if (currentTimeMillis - sessionInfo.recentlySaveTime > savePeriodMilliSeconds) {

            String fileFullName = path + sessionInfo.getClient().getId() + "T" + currentTimeMillis + ".jpeg";
            try (FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileFullName))) {
                imageOutput.write(bytes, 0, bytes.length);

                ImageInference imageInference = new ImageInference(fileFullName, sessionInfo.getClient(), responseMessage);
                imageInferenceRepository.save(imageInference);

                sessionInfo.updateRecentlySaveTime(currentTimeMillis);
                System.out.println("데이터 저장 (currentTimeMillis: " + currentTimeMillis + "). imageId = " + fileFullName);
            } catch (Exception e) {
                System.err.println("이미지 저장 실패");
            }
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
        if (status.equalsCode(CloseStatus.NORMAL)) {
            System.out.println("정상 종료");
            Client client = sessions.get(session).getClient();
            client.updateResult(-1, solution.gdsc.PathPal.domain.client.domain.CloseStatus.NORMAL);
            clientRepository.save(client);
        }
        else {
            System.out.println("비정상 종료. reason = " + status.getReason() + "\n");
            Client client = sessions.get(session).getClient();
            client.updateResult(-1, solution.gdsc.PathPal.domain.client.domain.CloseStatus.convert(status));
            clientRepository.save(client);
        }
        sessions.remove(session);
    }
}
