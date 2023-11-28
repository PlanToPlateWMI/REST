package pl.plantoplate.REST.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.plantoplate.REST.controller.dto.response.SimpleResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global Exception Handler. Contains methods that handle exception and return
 * ResponseEntity parametrized with {@link SimpleResponse} with message from
 * handled exception
 */
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


    @ExceptionHandler(DuplicateObject.class)
    public ResponseEntity<SimpleResponse> addTheSameProduct(DuplicateObject e) {
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

    @ExceptionHandler(DeleteNotSelected.class)
    public ResponseEntity<SimpleResponse> deleteNotSelectedByGroupRecipe(DeleteNotSelected e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidGroup.class)
    public ResponseEntity<SimpleResponse> getMealNotExistsInGroup(NotValidGroup e) {
        return buildResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new SimpleResponse(errors.toString()), HttpStatus.BAD_REQUEST);
    }

    private static ResponseEntity<SimpleResponse> buildResponseEntity(Exception e, HttpStatus status) {
        return new ResponseEntity<>(new SimpleResponse(e.getMessage()),status );
    }
}
