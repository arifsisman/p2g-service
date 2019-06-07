package vip.yazilim.play2gether.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "p2g_user")
public class P2GUser {

    @Id
    private String uuid;

    private boolean online;



    @OneToOne
    @JoinColumn(name = "system_user_uuid")
    private SystemUser systemUser;


    @OneToMany(mappedBy = "student")
    @JsonIgnore
    private List<Enrollment> enrollmentList;

    @OneToMany(mappedBy = "student")
    private List<FaceEncoding> faceEncodingList;
}
