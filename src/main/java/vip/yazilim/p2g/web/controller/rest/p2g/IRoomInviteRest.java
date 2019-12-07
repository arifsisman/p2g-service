package vip.yazilim.p2g.web.controller.rest.p2g;

import vip.yazilim.spring.core.rest.model.RestResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomInviteRest {
    RestResponse<Boolean> reject(HttpServletRequest request, HttpServletResponse response, String roomInviteUuid);
}
