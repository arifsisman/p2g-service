package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.constant.Privileges;
import vip.yazilim.p2g.web.entity.Privilege;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.repository.IPrivilegeRepo;
import vip.yazilim.p2g.web.service.p2g.relation.IPrivilegeService;
import vip.yazilim.p2g.web.service.p2g.relation.IRoomUserService;
import vip.yazilim.p2g.web.util.DBHelper;
import vip.yazilim.spring.core.exception.general.database.DatabaseException;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class PrivilegeService extends ACrudServiceImpl<Privilege, String> implements IPrivilegeService {

    @Autowired
    private IPrivilegeRepo privilegeRepo;

    @Autowired
    private IRoomUserService roomUserService;

    @Override
    protected JpaRepository<Privilege, String> getRepository() {
        return privilegeRepo;
    }

    @Override
    protected String getId(Privilege privilege) {
        return privilege.getName();
    }

    @Override
    protected Privilege preInsert(Privilege entity) {
        entity.setUuid(DBHelper.getRandomUuid());
        return entity;
    }

    @Override
    public List<Privilege> setRolePrivileges(String roleName, Privileges... privileges) throws DatabaseReadException {
        List<Privilege> privilegeList = new LinkedList<>();
        try {
            for (Privileges p : privileges) {
                Privilege rolePrivilege = new Privilege();
                rolePrivilege.setRoleName(roleName);
                rolePrivilege.setName(p.getPrivilegeName());

                privilegeList.add(create(rolePrivilege));
            }
        } catch (Exception e) {
            String err = String.format("Can not set privileges for role[%s]", roleName);
            throw new DatabaseReadException(err, e);
        }

        return privilegeList;
    }

    @Override
    public List<Privilege> getRolePrivileges(String roleName) throws DatabaseReadException {
        try {
            return privilegeRepo.findByRoleName(roleName);
        } catch (Exception e) {
            throw new DatabaseReadException("Can not get privileges", e);
        }
    }

    @Override
    public String[] getUserPrivileges(User user) throws DatabaseException {
        List<String> roles = new LinkedList<>();
        List<String> privileges = new LinkedList<>();

        roles.add(user.getRole());
        roomUserService.getRoomUser(user.getUuid()).ifPresent(role -> roles.add(role.getRoleName()));

        for (String role: roles) {
            getRolePrivileges(role).stream().map(Privilege::getName).forEach(privileges::add);
        }

        return privileges.toArray(new String[0]);
    }
}
