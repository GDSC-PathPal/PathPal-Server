package solution.gdsc.PathPal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping
    public String index() {
        return "hello world!";
    }

    @GetMapping
    public String test(@RequestBody Dto dto) {
        return "받은 데이터 = " + dto.getName();
    }
}
