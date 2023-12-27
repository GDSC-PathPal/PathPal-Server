package solution.gdsc.PathPal.global.error.baseExcaption;

import solution.gdsc.PathPal.global.error.ErrorCode;

public class IllegalException extends BusinessException {
    public IllegalException() {
        super(ErrorCode.BAD_REQUEST);
    }

    public IllegalException(ErrorCode e) {
        super(e);
    }
}
