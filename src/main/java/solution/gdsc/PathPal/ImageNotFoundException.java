package solution.gdsc.PathPal;

import solution.gdsc.PathPal.global.error.ErrorCode;
import solution.gdsc.PathPal.global.error.baseExcaption.NotFoundException;

public class ImageNotFoundException extends NotFoundException {
    public ImageNotFoundException() {
        super(ErrorCode.IMAGE_NOT_FOUND);
    }
}
