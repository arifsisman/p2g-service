package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vip.yazilim.p2g.web.config.annotation.HasSystemRole;
import vip.yazilim.p2g.web.constant.enums.Role;
import vip.yazilim.p2g.web.entity.UserDevice;
import vip.yazilim.p2g.web.service.p2g.IUserDeviceService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import vip.yazilim.spring.core.exception.database.DatabaseException;
import vip.yazilim.spring.core.rest.model.RestResponse;
import vip.yazilim.spring.core.rest.model.RestResponseFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static vip.yazilim.p2g.web.constant.Constants.API_P2G;

/**
 * @author mustafaarifsisman - 29.02.2020
 * @contact mustafaarifsisman@gmail.com
 */
@RestController
@RequestMapping(API_P2G + "/device")
public class DeviceRest {

    @Autowired
    private IUserDeviceService userDeviceService;

    @HasSystemRole(role = Role.P2G_USER)
    @GetMapping({"/"})
    public RestResponse<List<UserDevice>> getUserDeviceList(HttpServletRequest request, HttpServletResponse response) throws DatabaseException {
        return RestResponseFactory.generateResponse(userDeviceService.getUserDevicesByUserId(SecurityHelper.getUserId()), HttpStatus.OK, request, response);
    }

    @HasSystemRole(role = Role.P2G_USER)
    @PutMapping({"/select"})
    public RestResponse<Boolean> selectUsersActiveDevice(HttpServletRequest request, HttpServletResponse response, @RequestBody UserDevice userDevice) {
        return RestResponseFactory.generateResponse(userDeviceService.setUsersActiveDevice(SecurityHelper.getUserId(), userDevice), HttpStatus.OK, request, response);
    }

}
