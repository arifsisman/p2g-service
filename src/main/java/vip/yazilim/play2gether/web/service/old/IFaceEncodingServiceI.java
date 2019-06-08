package vip.yazilim.play2gether.web.service.old;

import vip.yazilim.play2gether.web.entity.old.FaceEncoding;
import vip.yazilim.play2gether.web.service.ICrudService;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IFaceEncodingServiceI extends ICrudService<FaceEncoding, String> {

    public boolean saveFaceEncodingByStudentId(String studentId, String encoding) throws Exception;
}
