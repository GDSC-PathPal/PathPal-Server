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

    @GetMapping("/test")
    public String test(@RequestBody Dto dto) {
        if (dto.getName() == null) {
            return "받은 데이터가 없습니다.";
        }
        else {
            return "받은 데이터 = " + dto.getName();
        }
    }
}
