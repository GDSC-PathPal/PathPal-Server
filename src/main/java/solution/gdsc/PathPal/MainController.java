package solution.gdsc.PathPal;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class MainController {

    //@Value("${image.path}")
    private String path = "/home/hsk4991149/static/image/";
    //private String path = "/Users/hongseungtaeg/Desktop/project/GDSC-PathPal/PathPal/src/main/resources/static/";

    @GetMapping
    @Operation(summary = "index page", description = "index page Test")
    public String index() {
        return "hello world!";
    }

    @PostMapping("/test")
    @Operation(summary = "Post request body(json)", description = "request body(json) Test")
    public TestResponse test(@RequestBody TestRequest testRequest) {
        return TestResponse.from(testRequest.getName());
    }

    @GetMapping("/image/{imageId}")
    @Operation(summary = "이미지 조회", description = "WebSocket으로 저장된 이미지를 조회합니다.(Content-Type: image/jpeg)")
    public ResponseEntity<Resource> viewImg(@PathVariable(name = "imageId") String imageId) throws IOException {
        final String fileFullPath = path + imageId + ".jpeg";
        File file = new File(fileFullPath);

        if (!file.exists()) {
            throw new ImageNotFoundException();
        }

        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(file.toPath())))
                .body(resource);
    }
}
