package solution.gdsc.PathPal.domain.inference.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import solution.gdsc.PathPal.domain.inference.domain.Inference;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class SocketClient {
    private final int port;
    private final int timeout;
    private final String hostName;
    private final ObjectMapper objectMapper;
    private final byte[] readByteArray;
    private Socket socket;
    private BufferedInputStream bis;
    private BufferedOutputStream bos;

    public SocketClient(String hostName, int port, int timeout) throws IOException {
        this.port = port;
        this.timeout = timeout;
        this.hostName = hostName;
        this.objectMapper = new ObjectMapper();
        this.readByteArray = new byte[10000];
        socketConnect();
    }

    private void socketConnect() throws IOException {
        if (this.socket.isConnected()) {
            this.socket.close();
        }
        this.socket = new Socket(hostName, port);
        this.socket.setSoTimeout(timeout);
        this.bos = new BufferedOutputStream(socket.getOutputStream());
        this.bis = new BufferedInputStream(socket.getInputStream());
    }

    public List<Inference> inferenceImage(byte[] file) {

        try {
            // 1. Send File Size
            System.out.println("파일 사이즈 전송:" + file.length);
            DataOutputStream dataOutputStream = new DataOutputStream(bos);
            dataOutputStream.writeInt(file.length);
            bos.flush();

            // 2. receive data
            bis.read(readByteArray);

            // 3. send image
            System.out.println("이미지 전송 " + file.length);
            bos.write(file);
            bos.flush();

            // 4. receive data
            int readByte = bis.read(readByteArray);
            System.out.println("read Byte size = " + readByte);

            // 5. convert to inference
            String bytesToStr = new String(readByteArray, 0, readByte);
            System.out.println(bytesToStr);

            return objectMapper.readValue(bytesToStr, new TypeReference<>() {
            });

        } catch (SocketTimeoutException e) {
            System.err.println("소켓 타임아웃 발생");
            System.err.println("소켓 재연결 시도");
            try {
                socketConnect();
                return inferenceImage(file);
            } catch (IOException ioException) {
                System.err.println("소켓 재연결 실패");
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
