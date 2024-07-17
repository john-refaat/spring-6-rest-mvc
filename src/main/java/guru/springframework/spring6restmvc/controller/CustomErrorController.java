package guru.springframework.spring6restmvc.controller;

import org.springframework.http.HttpStatus;
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
