package solution.gdsc.PathPal.domain.client.domain;

public enum CloseStatus {

    NORMAL, GOING_AWAY, PROTOCOL_ERROR, NOT_ACCEPTABLE,
    NO_STATUS_CODE, NO_CLOSE_FRAME, BAD_DATA, POLICY_VIOLATION,
    TOO_BIG_TO_PROCESS, REQUIRED_EXTENSION, SERVER_ERROR,
    SERVICE_RESTARTED, SERVICE_OVERLOAD, TLS_HANDSHAKE_FAILURE, SESSION_NOT_RELIABLE,
    OPEN, UNKNOWN;

    public static CloseStatus convert(org.springframework.web.socket.CloseStatus closeStatus) {
        if (closeStatus == null) {
            return UNKNOWN;
        }
        return switch (closeStatus.getCode()) {
            case 1000 -> NORMAL;
            case 1001 -> GOING_AWAY;
            case 1002 -> PROTOCOL_ERROR;
            case 1003 -> NOT_ACCEPTABLE;
            case 1005 -> NO_STATUS_CODE;
            case 1006 -> NO_CLOSE_FRAME;
            case 1007 -> BAD_DATA;
            case 1008 -> POLICY_VIOLATION;
            case 1009 -> TOO_BIG_TO_PROCESS;
            case 1010 -> REQUIRED_EXTENSION;
            case 1011 -> SERVER_ERROR;
            case 1012 -> SERVICE_RESTARTED;
            case 1013 -> SERVICE_OVERLOAD;
            case 1015 -> TLS_HANDSHAKE_FAILURE;
            case 4500 -> SESSION_NOT_RELIABLE;
            default -> UNKNOWN;
        };
    }

}
