package vip.yazilim.p2g.web.controller;

import com.wrapper.spotify.exceptions.detailed.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vip.yazilim.p2g.web.exception.AccountException;
import vip.yazilim.p2g.web.exception.ConstraintViolationException;
import vip.yazilim.p2g.web.exception.ForbiddenException;
import vip.yazilim.p2g.web.exception.SpotifyAccountException;
import vip.yazilim.spring.core.exception.GeneralException;
import vip.yazilim.spring.core.exception.InvalidArgumentException;
import vip.yazilim.spring.core.exception.MethodNotSupported;
import vip.yazilim.spring.core.exception.database.*;
import vip.yazilim.spring.core.exception.web.NotFoundException;
import vip.yazilim.spring.core.exception.web.ServiceException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

/**
 * @author mustafaarifsisman - 09.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@ControllerAdvice
public class ExceptionHandlerController {

    private Logger LOGGER = LoggerFactory.getLogger(ExceptionHandlerController.class);

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
        return error(FORBIDDEN, e);
    }

    @ExceptionHandler({AccountException.class})
    public ResponseEntity<String> handleAccountException(AccountException e) {
        return error(CONFLICT, e);
    }

    @ExceptionHandler({SpotifyAccountException.class})
    public ResponseEntity<String> handleSpotifyAccountException(SpotifyAccountException e) {
        return error(NOT_ACCEPTABLE, e);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return error(CONFLICT, e);
    }

    @ExceptionHandler({MessageDeliveryException.class})
    public ResponseEntity<String> handleMessageDeliveryException(MessageDeliveryException e) {
        return error(FORBIDDEN, e);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
        return error(FORBIDDEN, e);
    }


    /////////////////////////////
    // Spring Core Lib Exceptions
    /////////////////////////////

    @ExceptionHandler({GeneralException.class})
    public ResponseEntity<String> handleGeneralException(GeneralException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({InvalidArgumentException.class})
    public ResponseEntity<String> handleInvalidArgumentException(InvalidArgumentException e) {
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

    @ExceptionHandler({DatabaseSaveException.class})
    public ResponseEntity<String> handleDatabaseSaveException(DatabaseSaveException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({DatabaseUpdateException.class})
    public ResponseEntity<String> handleDatabaseUpdateException(DatabaseUpdateException e) {
        return error(NOT_FOUND, e);
    }

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<String> handleServiceException(ServiceException e) {
        return error(INTERNAL_SERVER_ERROR, e);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleNotFoundException(NotFoundException e) {
        return error(NOT_FOUND, e);
    }

    @ExceptionHandler({MethodNotSupported.class})
    public ResponseEntity<String> handleMethodNotSupported(MethodNotSupported e) {
        return error(METHOD_NOT_ALLOWED, e);
    }

    /////////////////////////////
    // Spotify Web Api Exceptions
    /////////////////////////////

    @ExceptionHandler({IOException.class})
    public ResponseEntity<String> handleIOException(DatabaseException e) {
        return error(BAD_REQUEST, e);
    }

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
    public ResponseEntity<String> handleUnauthorizedException(UnauthorizedException e) {
        return error(UNAUTHORIZED, e);
    }

    private ResponseEntity<String> error(HttpStatus status, Exception e) {
        LOGGER.error("Exception : " + e.getMessage());
        return ResponseEntity.status(status).body(e.getMessage());
    }

    private ResponseEntity<String> error(HttpStatus status,Exception e, String message) {
        LOGGER.error("Exception : " + e.getMessage());
        return ResponseEntity.status(status).body(message);
    }

}