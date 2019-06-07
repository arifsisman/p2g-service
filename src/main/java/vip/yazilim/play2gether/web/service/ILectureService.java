package vip.yazilim.play2gether.web.service;

import vip.yazilim.play2gether.web.entity.Lecture;

import java.util.List;

/**
 * @author Emre Sen - 24.05.2019
 * @contact maemresen07@gmail.com
 */
public interface ILectureService extends CrudService<Lecture, String> {

    public List<Lecture> getLectureListByCourse(String courseUuid);

    public String getLectureInfo(String lectureUuid) throws Exception;
}
