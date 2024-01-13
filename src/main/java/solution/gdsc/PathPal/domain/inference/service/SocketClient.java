package solution.gdsc.PathPal.domain.inference.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import solution.gdsc.PathPal.domain.inference.domain.Inference;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
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

    public SocketClient(String hostName, int port, int timeout) {
        this.port = port;
        this.timeout = timeout;
        this.hostName = hostName;
        this.objectMapper = new ObjectMapper();
        this.readByteArray = new byte[10000];
        socketConnect();
    }

    private void socketConnect() {
        if (this.socket != null) {
            if (socket.isConnected() && !socket.isClosed()) {
                try {
                    this.socket.close();
                } catch (IOException e) {
                }
            }
        }
        this.socket = new Socket();
        try {
            this.socket.setSoTimeout(timeout);
        } catch (IOException e) {
        }

        try {
            this.socket.connect(new InetSocketAddress(hostName, port), timeout);
            //this.socket.connect(new InetSocketAddress(hostName, port));
            this.bos = new BufferedOutputStream(socket.getOutputStream());
            this.bis = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("소켓 연결 실패. 프로그램을 종료합니다.");
            System.exit(1);
        }
    }

    public List<Inference> inferenceImage(byte[] file) {
        try {
            // 1. Send File Size
            System.out.println("파일 사이즈 전송:" + file.length);
            DataOutputStream dataOutputStream = new DataOutputStream(bos);
            dataOutputStream.writeInt(file.length);
            bos.flush();

            // 2. receive data
            int k = bis.read(readByteArray);

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
            System.out.println("소켓 타임아웃 발생. 소켓 재연결 시도");
            socketConnect();
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
