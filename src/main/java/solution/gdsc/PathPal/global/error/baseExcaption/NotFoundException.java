package solution.gdsc.PathPal.global.error.baseExcaption;


import solution.gdsc.PathPal.global.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(message, ErrorCode.NOT_FOUND);
    }

    public NotFoundException(ErrorCode e) {
        super(e);
    }

}
