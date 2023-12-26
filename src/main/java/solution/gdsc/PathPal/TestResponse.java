package solution.gdsc.PathPal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TestResponse {

    @Schema(description = "받은 데이터", example = "받은 데이터는 {}입니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String data;

    public static TestResponse from(String name) {
        if (name == null) return new TestResponse("받은 데이터는 null입니다.");
        return new TestResponse("받은 데이터는 [" + name + "]입니다.");
    }
}
