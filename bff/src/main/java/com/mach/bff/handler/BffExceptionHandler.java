package com.mach.bff.handler;

import com.mach.core.exception.BaseException;
import com.mach.core.exception.NotFoundException;
import com.mach.core.exception.model.FeignErrorModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class BffExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<FeignErrorModel> handleNotFoundExceptionException(final NotFoundException ex) {
        log.error("Cause not found exception. ", ex);
        return ResponseEntity.status(ex.getStatus()).body(ex.getModel());
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<FeignErrorModel> handleBaseException(final BaseException ex) {
        log.error("Cause base exception. ", ex);
        return ResponseEntity.status(ex.getStatus()).body(ex.getModel());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FeignErrorModel> handleBaseException(final Exception ex) {
        log.error("Cause un expected exception. ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new FeignErrorModel(ex.getMessage(), "server_error"));
    }
}
