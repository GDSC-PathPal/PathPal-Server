package solution.gdsc.PathPal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TestRequest {

    @Schema(description = "테스트 데이터", example = "this is name field")
    private String name;
}
