package solution.gdsc.PathPal.domain.inference;

import java.util.List;

public class JsonUtil {

    private JsonUtil() {
    }

    public static String toJsonFormat(List<InferenceTranslate> inferenceTranslates) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < inferenceTranslates.size(); i++) {
            sb.append("{\"koreanTTSString\": \"");
            sb.append(inferenceTranslates.get(i).toKorean());
            sb.append("\", \"englishTTSString\": \"");
            sb.append(inferenceTranslates.get(i).toEnglish());
            sb.append("\", \"needAlert\": \"");
            sb.append(inferenceTranslates.get(i).isAlert());

            if (i != inferenceTranslates.size() - 1) {
                sb.append("\"},");
            }
            else {
                sb.append("\"}");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
