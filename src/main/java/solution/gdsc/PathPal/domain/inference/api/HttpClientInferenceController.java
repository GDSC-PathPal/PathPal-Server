package solution.gdsc.PathPal.domain.inference.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import solution.gdsc.PathPal.domain.client.domain.Client;
import solution.gdsc.PathPal.domain.client.domain.ImageInference;
import solution.gdsc.PathPal.domain.client.repository.ClientRepository;
import solution.gdsc.PathPal.domain.client.repository.ImageInferenceRepository;
import solution.gdsc.PathPal.domain.inference.domain.Inference;
import solution.gdsc.PathPal.domain.inference.service.InferenceService;
import solution.gdsc.PathPal.domain.inference.service.SocketClient;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HttpClientInferenceController {

    private final SocketClient socketClient = new SocketClient("127.0.0.1", 9999, 2000);
    private final InferenceService inferenceService;
    private final ImageInferenceRepository imageInferenceRepository;
    private final ClientRepository clientRepository;
    private final int savePeriodMilliSeconds = 5000;
    private String path = "/home/hsk4991149/static/image/";
    //private String path = "/Users/hongseungtaeg/Desktop/project/GDSC-PathPal/PathPal/src/main/resources/static/";

    private final Map<HttpSession, SessionInfo> sessionMap = new HashMap<>();

    @PostMapping("/inference")
    public String inference(@RequestParam(name = "image") MultipartFile file,
                            @RequestParam(name = "time") Integer time,
                            HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();

        SessionInfo sessionInfo = sessionMap.get(session);
        if (sessionInfo == null) {
            int expectedSeconds = time == null ? 500 : time;
            Client client = new Client(expectedSeconds);
            clientRepository.save(client);

            long currentTimeMillis = System.currentTimeMillis();
            sessionInfo = new SessionInfo(client, currentTimeMillis, expectedSeconds);
            sessionMap.put(session, sessionInfo);
        }

        byte[] bytes = file.getBytes();
        List<Inference> inferences = socketClient.inferenceImage(bytes);
        String responseMessage = inferenceService.convertInference2(inferences);

        long currentTimeMillis = System.currentTimeMillis();
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
        return responseMessage;
    }
}
