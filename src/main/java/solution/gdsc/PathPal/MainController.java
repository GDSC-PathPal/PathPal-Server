package solution.gdsc.PathPal;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class MainController {

    //@Value("${image.path}")
    private String path = "/home/hsk4991149/static/image/";

    @GetMapping
    public String index() {
        return "hello world!";
    }

    @PostMapping("/test")
    public TestResponse test(@RequestBody TestRequest testRequest) {
        return TestResponse.from(testRequest.getName());
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<Resource> viewImg(@PathVariable(name = "imageId") Integer imageId) throws IOException {
        final String fileFullPath = path + imageId + ".jpeg";
        System.out.println(fileFullPath);
        Path path = new File(fileFullPath).toPath();

        FileSystemResource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                .body(resource);
    }
}
