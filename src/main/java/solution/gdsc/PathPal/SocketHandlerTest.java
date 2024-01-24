package solution.gdsc.PathPal;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SocketHandlerTest extends BinaryWebSocketHandler {

    //@Value("${image.path:/home/hsk4991149/static/image/}")
    private String path = "/home/hsk4991149/static/image/";
    //private String path = "/Users/hongseungtaeg/Desktop/project/GDSC-PathPal/PathPal/src/main/resources/static/";

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    private final AtomicInteger imageNumber = new AtomicInteger(1);

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
            int imageId = imageNumber.get();
            String fileFullName = path + imageId + ".jpeg";
            System.out.println(fileFullName);

            FileImageOutputStream imageOutput = new FileImageOutputStream(new File(fileFullName));
            imageOutput.write(bytes, 0, bytes.length);
            imageOutput.close();
            responseMessage = "성공적으로 저장했습니다. 받은 바이트 크기=" + bytes.length + ", savedImageId=" + imageId;
            imageNumber.incrementAndGet();
        } catch (Exception e) {
            responseMessage = "저장에 실패했습니다. 받은 바이트 크기=" + bytes.length;
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
