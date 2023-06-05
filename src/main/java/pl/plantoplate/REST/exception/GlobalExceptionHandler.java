package pl.plantoplate.REST.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.plantoplate.REST.dto.Response.SimpleResponse;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(UserChangeHisRole.class)
    public ResponseEntity<SimpleResponse> userChangeHisRole(UserChangeHisRole e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(EmailAlreadyTaken.class)
    public ResponseEntity<SimpleResponse> emailAlreadyTaken(EmailAlreadyTaken e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFromGroup.class)
    public ResponseEntity<SimpleResponse> userNotFromGroup(UserNotFromGroup e) {
        return buildResponseEntity(e, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(AddTheSameProduct.class)
    public ResponseEntity<SimpleResponse> addTheSameProduct(AddTheSameProduct e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFound.class)
    public ResponseEntity<SimpleResponse> entityNotFound(EntityNotFound e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ModifyGeneralProduct.class)
    public ResponseEntity<SimpleResponse> modifyGeneralProduct(ModifyGeneralProduct e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongInviteCode.class)
    public ResponseEntity<SimpleResponse> wrongInviteCode(WrongInviteCode e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoValidProductWithAmount.class)
    public ResponseEntity<SimpleResponse> wrongProductInShoppingList(NoValidProductWithAmount e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongRequestData.class)
    public ResponseEntity<SimpleResponse> wrongQueryParama(WrongRequestData e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<SimpleResponse> buildResponseEntity(Exception e, HttpStatus status) {
        return new ResponseEntity<>(new SimpleResponse(e.getMessage()),status );
    }
}
