package vip.yazilim.p2g.web.model;

import lombok.Data;
import vip.yazilim.p2g.web.constant.SearchTypes;

import java.util.List;

/**
 * @author mustafaarifsisman - 29.11.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Data
public class SearchModel {
    private SearchTypes type;
    private String name;
    private List<String> artists;
    private String id;
    private String uri;
    private String imageUrl;
}
