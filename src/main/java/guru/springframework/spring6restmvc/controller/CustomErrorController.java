package guru.springframework.spring6restmvc.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;

/**
 * @author john
 * @since 15/07/2024
 */
@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(TransactionSystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody List<Map<String,String>> handleTransactionException(TransactionSystemException ex) {
      List<Map<String, String>> errorMap = new ArrayList<>();
      errorMap.add(Collections.singletonMap("error", ex.getMessage()));
      Exception exception = (Exception) ex.getCause();
      while (exception!= null) {
          errorMap.add(Collections.singletonMap("error", exception.getMessage()));
          exception = (Exception) exception.getCause();
      }
      return errorMap;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody List<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        List<Map<String, String>> fieldErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(
                violation -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("field", violation.getPropertyPath().toString());
                    errorMap.put("message", violation.getMessage());
                      fieldErrors.add(errorMap);
                }
        );
        return fieldErrors;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody List<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        //Map<String, Set<String>> message = new HashMap<>();
        List<Map<String, String>> fieldErrors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("field", error.getField());
                    errorMap.put("message", error.getDefaultMessage());
                    fieldErrors.add(errorMap);
                }
        );
        return fieldErrors;
    }
}
