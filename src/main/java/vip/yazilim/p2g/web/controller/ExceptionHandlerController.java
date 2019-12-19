package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.exception.SpotifyAccountException;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
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

    @ExceptionHandler({DatabaseException.class})
    public ResponseEntity<String> handleDatabaseException(DatabaseException e) {
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

    @ExceptionHandler({SpotifyAccountException.class})
    public ResponseEntity<String> handleSpotifyAccountException(SpotifyWebApiException e) {
        return error(NOT_ACCEPTABLE, e);
    }

    /////////////////////////////
    // Spotify Web Api Exceptions
    /////////////////////////////

    @ExceptionHandler({BadGatewayException.class})
    public ResponseEntity<String> handleBadGatewayException(BadGatewayException e) {
        return error(BAD_GATEWAY, e);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return error(BAD_REQUEST, e);
    }

    @ExceptionHandler({com.wrapper.spotify.exceptions.detailed.ForbiddenException.class})
    public ResponseEntity<String> handleForbiddenException(com.wrapper.spotify.exceptions.detailed.ForbiddenException e) {
        return error(FORBIDDEN, e);
    }

    @ExceptionHandler({InternalServerErrorException.class})
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException e) {
        return error(SERVICE_UNAVAILABLE, e);
    }

    @ExceptionHandler({com.wrapper.spotify.exceptions.detailed.NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(com.wrapper.spotify.exceptions.detailed.NotFoundException e) {
        return error(NOT_FOUND, e);
    }

    @ExceptionHandler({ServiceUnavailableException.class})
    public ResponseEntity<String> handleServiceUnavailableException(ServiceUnavailableException e) {
        return error(SERVICE_UNAVAILABLE, e);
    }

    @ExceptionHandler({TooManyRequestsException.class})
    public ResponseEntity<String> handleTooManyRequestsException(TooManyRequestsException e) {
        return error(TOO_MANY_REQUESTS, e);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<String> handleTooManyRequestsException(UnauthorizedException e) {
        return error(UNAUTHORIZED, e);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        LOGGER.error("Exception : " + e.getMessage());
        return ResponseEntity.status(status).body(e.getMessage());
    }

}
