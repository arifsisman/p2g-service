package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.spring.core.exception.web.NotFoundException;

import static org.springframework.http.HttpStatus.*;

/**
 * @author mustafaarifsisman - 09.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@ControllerAdvice
public class ExceptionHandlerController {

    private Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRunTimeException(RuntimeException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return error(NOT_FOUND, e);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        return error(FORBIDDEN, e);
    }

    @ExceptionHandler({SpotifyWebApiException.class})
    public ResponseEntity<String> handleSpotifyWebApiException(SpotifyWebApiException e) {
        return error(FORBIDDEN, e);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        LOGGER.error("Exception : " + e.getMessage());
        return ResponseEntity.status(status).body(e.getMessage());
    }

}
