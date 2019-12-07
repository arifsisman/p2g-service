package vip.yazilim.p2g.web.controller.rest.p2g;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import vip.yazilim.p2g.web.entity.relation.RoomQueue;
import vip.yazilim.p2g.web.model.RoomQueueModel;
import vip.yazilim.p2g.web.model.SearchModel;
import vip.yazilim.spring.core.rest.model.RestResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author mustafaarifsisman - 07.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IRoomQueueRest {
    RestResponse<List<RoomQueue>> getRoomQueueListByRoomUuid(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid);
    RestResponse<RoomQueueModel> getRoomQueueModelByRoomUuid(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid);
    RestResponse<RoomQueue> addToRoomQueue(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomUuid, @RequestBody SearchModel searchModel);
    RestResponse<Boolean> deleteFromRoomQueue(HttpServletRequest request, HttpServletResponse response, @PathVariable String roomQueueUuid);
}
