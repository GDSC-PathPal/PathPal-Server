package solution.gdsc.PathPal.domain.inference.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import solution.gdsc.PathPal.domain.inference.domain.Inference;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketClient {

    private final Socket socket;
    private final OutputStream os;
    private final InputStream is;


    public SocketClient(String hostName, int port) throws IOException {
        this.socket = new Socket(hostName, port);
        this.os = socket.getOutputStream();
        this.is = socket.getInputStream();
    }

    public List<Inference> inferenceImage(byte[] file) {

        try {
            BufferedOutputStream bos = new BufferedOutputStream(os);
            BufferedInputStream bis = new BufferedInputStream(is);

            // 1. Send File Size
            System.out.println("파일 사이즈 전송:" + file.length);
            DataOutputStream dataOutputStream = new DataOutputStream(bos);
            dataOutputStream.writeInt(file.length);
            bos.flush();

            // 2. receive data
            byte[] tmp = new byte[10000];
            int zz = bis.read(tmp);

            // 3. send image
            System.out.println("이미지 전송 " + file.length);
            bos.write(file);
            bos.flush();

            // 4. receive data
            int read = bis.read(tmp);
            System.out.println("read = " + read);

            String bytesToStr = new String(tmp, 0, read);
            System.out.println(bytesToStr);

            // 5. convert to inference
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(bytesToStr, new TypeReference<>() {
            });

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
