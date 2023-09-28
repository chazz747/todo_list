package com.example.demo.config;

import com.example.demo.exceptions.NoDataFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorHandlerConfig extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(ErrorHandlerConfig.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> all(Exception e, WebRequest request){
        logger.error(e.getMessage(),e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
/*
    @ExceptionHandler(ValidateServiceException.class)
    public ResponseEntity<?> validateService(Exception e, WebRequest request){
        logger.error(e.getMessage(),e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
*/
    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<?> noData(Exception e, WebRequest request){
        logger.error(e.getMessage(),e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
