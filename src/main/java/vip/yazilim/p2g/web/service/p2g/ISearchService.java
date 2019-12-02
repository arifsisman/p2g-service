package vip.yazilim.p2g.web.service.p2g;

import com.wrapper.spotify.enums.ModelObjectType;
import vip.yazilim.p2g.web.model.SearchModel;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
public interface ISearchService {

    List<SearchModel> search(String q, ModelObjectType... searchTypes);

}
