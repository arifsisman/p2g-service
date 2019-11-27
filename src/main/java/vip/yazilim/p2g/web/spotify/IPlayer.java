package vip.yazilim.p2g.web.spotify;

/**
 * @author mustafaarifsisman - 28.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface IPlayer {

    boolean play(String roomUuid, String songUuid);
    boolean play(String roomUuid);
    boolean pause(String roomUuid);
    boolean next(String roomUuid);
    boolean previous(String roomUuid);

    boolean seek(String roomUuid, Integer ms);
    boolean shuffle(String roomUuid);
    boolean repeat(String roomUuid);

}
