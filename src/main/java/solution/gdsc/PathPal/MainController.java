package solution.gdsc.PathPal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping
    public String index() {
        return "hello world!";
    }

    @PostMapping("/test")
    public TestResponse test(@RequestBody TestRequest testRequest) {
        return TestResponse.from(testRequest.getName());
    }
}
