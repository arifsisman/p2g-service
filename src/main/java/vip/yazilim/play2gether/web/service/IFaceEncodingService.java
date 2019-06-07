package vip.yazilim.play2gether.web.service;

import vip.yazilim.play2gether.web.entity.FaceEncoding;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface IFaceEncodingService extends CrudService<FaceEncoding, String> {

    public boolean saveFaceEncodingByStudentId(String studentId, String encoding) throws Exception;
}
