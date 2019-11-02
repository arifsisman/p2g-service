package vip.yazilim.p2g.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Queue;
import vip.yazilim.p2g.web.entity.Song;
import vip.yazilim.p2g.web.exception.DatabaseException;
import vip.yazilim.p2g.web.repository.IQueueRepo;
import vip.yazilim.p2g.web.service.IQueueService;
import vip.yazilim.p2g.web.service.ISongService;
import vip.yazilim.spring.utils.service.ACrudServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mustafaarifsisman - 1.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class QueueServiceImpl extends ACrudServiceImpl<Queue, String> implements IQueueService {

    // static fields
    private Logger LOGGER = LoggerFactory.getLogger(QueueServiceImpl.class);

    // injected dependencies
    @Autowired
    private IQueueRepo queueRepo;

    @Autowired
    private ISongService songService;

    @Override
    public List<Song> getSongsByRoomUuid(String roomUuid) throws DatabaseException {
        String songUuid = "unknown-song-uuid";
        List<Song> songList;

        try {
            songList = new ArrayList<>();
            Iterable<Queue> queueIterable;

            queueIterable = queueRepo.findByRoomUuid(roomUuid);

            for (Queue queue : queueIterable) {
                songUuid = queue.getSongUuid();
                songList.add(songService.getById(songUuid));
            }

        } catch (Exception exception) {
            String errorMessage = String.format("An error occurred while getting Song[%s] with roomUuid[%s]", songUuid, roomUuid);
            throw new DatabaseException(errorMessage, exception);
        }

        return null;
    }

    @Override
    protected JpaRepository<Queue, String> getRepository() {
        return queueRepo;
    }

    @Override
    protected String getId(Queue entity) {
        return entity.getUuid();
    }
}
