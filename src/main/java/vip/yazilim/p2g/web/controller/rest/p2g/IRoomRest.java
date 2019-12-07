package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.web.bind.annotation.PathVariable;
import vip.yazilim.p2g.web.model.RoomModel;
import vip.yazilim.spring.core.rest.model.RestResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomRest {
    RestResponse<RoomModel> getRoomModel(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid);
    RestResponse<Boolean> deleteRoom(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid);
}
