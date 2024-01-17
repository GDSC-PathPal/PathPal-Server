package solution.gdsc.PathPal.domain.inference.api;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TestClientController extends WebSocketClientController {

    private final Map<WebSocketSession, SessionInfo> sessions = new ConcurrentHashMap<>();
    private final int savePeriodMilliSeconds = 5000;
    private final byte[] scoreByte = {0x30, 0x31, 0x32, 0x33, 0x34, 0x35};

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        List<String> time = session.getHandshakeHeaders().get("time");
        if (time == null) {
            System.err.println("time 헤더가 없습니다.");
            try {
                session.close();
            } catch (IOException e) {
            }
            return;
        }

        int totalExpectedMinutes;
        try {
            totalExpectedMinutes = Integer.parseInt(time.get(0));
        } catch (NumberFormatException e) {
            System.err.println("time 헤더가 숫자가 아닙니다.");
            try {
                session.close();
            } catch (IOException e1) {
            }
            return;
        }

        long currentTimeMillis = System.currentTimeMillis();
        session.setBinaryMessageSizeLimit(1024 * 1024 * 10);
        sessions.put(session, new SessionInfo(totalExpectedMinutes, currentTimeMillis));
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        byte[] bytes = message.getPayload().array();
        if (bytes.length == 1) {
            for (int i = 0; i <= 5; i++) {
                if (bytes[0] == scoreByte[i]) {
                    System.out.println("점수 : " + i);
                }
            }

            try {
                session.close();
                return;
            } catch (IOException e) {
            }
        }

        String responseMessage;
        long currentTimeMillis = System.currentTimeMillis();
        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTimeMillis);

        SessionInfo sessionInfo = sessions.get(session);
        if (currentTimeMillis - sessionInfo.recentlySaveTime > savePeriodMilliSeconds) {
            sessionInfo.updateRecentlySaveTime(currentTimeMillis);
            responseMessage = "데이터 저장 : " + format;
            System.out.println("데이터 저장: " + format);
        }
        else {
            responseMessage = "데이터 저장 안함 : " + format;
        }

        try {
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
        }
        else {
            System.out.println("비정상 종료: " + status.getReason() + "\n");
        }
        sessions.remove(session);
    }
}
