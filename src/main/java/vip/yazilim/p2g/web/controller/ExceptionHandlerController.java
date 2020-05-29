package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.exceptions.detailed.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import vip.yazilim.libs.springcore.exception.*;
import vip.yazilim.p2g.web.exception.AccountException;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.exception.SpotifyException;
import vip.yazilim.p2g.web.util.SecurityHelper;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

/**
 * @author mustafaarifsisman - 09.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerController {

    private Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler({Exception.class})
    public ResponseEntity<String> handleException(Exception e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRunTimeException(RuntimeException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({NestedRuntimeException.class})
    public ResponseEntity<String> handleNestedRuntimeException(NestedRuntimeException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<String> handleForbiddenException(ForbiddenException e) {
        return noLog(FORBIDDEN, e);
    }

    @ExceptionHandler({AccountException.class})
    public ResponseEntity<String> handleAccountException(AccountException e) {
        return error(CONFLICT, e);
    }

    @ExceptionHandler({SpotifyException.class})
    public ResponseEntity<String> handleSpotifyAccountException(SpotifyException e) {
        return warn(NOT_ACCEPTABLE, e);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return error(CONFLICT, e);
    }

    @ExceptionHandler({MessageDeliveryException.class})
    public ResponseEntity<String> handleMessageDeliveryException(MessageDeliveryException e) {
        return noLog(FORBIDDEN, e);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return error(FORBIDDEN, e);
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException e) {
        return noLog(UNAUTHORIZED, e);
    }

    @ExceptionHandler({IOException.class})
    public ResponseEntity<String> handleIOException(IOException e) {
        return error(BAD_REQUEST, e);
    }

    /////////////////////////////
    // Spring Core Lib Exceptions
    /////////////////////////////

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleInvalidArgumentException(IllegalArgumentException e) {
        return error(BAD_REQUEST, e);
    }

    @ExceptionHandler({DatabaseException.class})
    public ResponseEntity<String> handleDatabaseException(DatabaseException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({DatabaseCreateException.class})
    public ResponseEntity<String> handleDatabaseCreateException(DatabaseCreateException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({DatabaseDeleteException.class})
    public ResponseEntity<String> handleDatabaseDeleteException(DatabaseDeleteException e) {
        return error(NOT_FOUND, e);
    }

    @ExceptionHandler({DatabaseReadException.class})
    public ResponseEntity<String> handleDatabaseReadException(DatabaseReadException e) {
        return error(NOT_FOUND, e);
    }


    @ExceptionHandler({DatabaseUpdateException.class})
    public ResponseEntity<String> handleDatabaseUpdateException(DatabaseUpdateException e) {
        return error(NOT_FOUND, e);
    }

    @ExceptionHandler({NoSuchMethodException.class})
    public ResponseEntity<String> handleNoSuchMethodException(NoSuchMethodException e) {
        return error(NOT_FOUND, e);
    }

    /////////////////////////////
    // Spotify Web Api Exceptions
    /////////////////////////////

    @ExceptionHandler({BadGatewayException.class})
    public ResponseEntity<String> handleBadGatewayException(BadGatewayException e) {
        return spotifyWarn(BAD_GATEWAY, e);
    }

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<String> handleBadRequestException(BadRequestException e) {
        return spotifyWarn(BAD_REQUEST, e);
    }

    @ExceptionHandler({SpotifyWebApiException.class})
    public ResponseEntity<String> handleSpotifyWebApiException(SpotifyWebApiException e) {
        return spotifyWarn(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({com.wrapper.spotify.exceptions.detailed.ForbiddenException.class})
    public ResponseEntity<String> handleForbiddenException(com.wrapper.spotify.exceptions.detailed.ForbiddenException e) {
        return spotifyWarn(FORBIDDEN, e);
    }

    @ExceptionHandler({InternalServerErrorException.class})
    public ResponseEntity<String> handleInternalServerErrorException(InternalServerErrorException e) {
        return spotifyWarn(SERVICE_UNAVAILABLE, e);
    }

    @ExceptionHandler({com.wrapper.spotify.exceptions.detailed.NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(com.wrapper.spotify.exceptions.detailed.NotFoundException e) {
        return spotifyWarn(NOT_FOUND, e);
    }

    @ExceptionHandler({ServiceUnavailableException.class})
    public ResponseEntity<String> handleServiceUnavailableException(ServiceUnavailableException e) {
        return spotifyWarn(SERVICE_UNAVAILABLE, e);
    }

    @ExceptionHandler({TooManyRequestsException.class})
    public ResponseEntity<String> handleTooManyRequestsException(TooManyRequestsException e) {
        return spotifyWarn(TOO_MANY_REQUESTS, e);
    }

    @ExceptionHandler({UnauthorizedException.class})
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return spotifyWarn(UNAUTHORIZED, e);
    }

    private ResponseEntity<String> warn(HttpStatus status, Exception e) {
        logger.warn("[{}] :: Exception :: {}", SecurityHelper.getUserId(), e.getMessage());
        return ResponseEntity.status(status).body(e.getMessage());
    }

    private ResponseEntity<String> spotifyWarn(HttpStatus status, Exception e) {
        logger.warn("[{}] :: Spotify Exception :: {}", SecurityHelper.getUserId(), e.getMessage());
        return ResponseEntity.status(status).body(e.getMessage());
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        logger.error("[{}] :: Exception :: {}", SecurityHelper.getUserId(), e.getMessage());
        return ResponseEntity.status(status).body(e.getMessage());
    }

    private ResponseEntity<String> noLog(HttpStatus status, Exception e) {
        return ResponseEntity.status(status).body(e.getMessage());
    }
}