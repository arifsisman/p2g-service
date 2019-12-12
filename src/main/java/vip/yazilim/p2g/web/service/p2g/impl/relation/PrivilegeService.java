package vip.yazilim.p2g.web.service.p2g.impl.relation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import vip.yazilim.p2g.web.entity.Privilege;
import vip.yazilim.p2g.web.repository.IPrivilegeRepo;
import vip.yazilim.p2g.web.service.p2g.relation.IPrivilegeService;
import vip.yazilim.spring.core.exception.general.database.DatabaseReadException;
import vip.yazilim.spring.core.service.ACrudServiceImpl;

import java.util.List;

/**
 * @author mustafaarifsisman - 12.12.2019
 * @contact mustafaarifsisman@gmail.com
 */
@Service
public class PrivilegeService extends ACrudServiceImpl<Privilege, String> implements IPrivilegeService {

    @Autowired
    private IPrivilegeRepo privilegeRepo;

    @Override
    protected JpaRepository<Privilege, String> getRepository() {
        return privilegeRepo;
    }

    @Override
    protected String getId(Privilege privilege) {
        return privilege.getPrivilegeName();
    }

    @Override
    public List<Privilege> setPrivilegeList(String roleName, List<Privilege> privilegeList) throws DatabaseReadException {
        try {
            for (Privilege p : privilegeList) {
                p.setRoleName(roleName);
                create(p);
            }
        } catch (Exception e) {
            String err = String.format("Can not set privileges for role[%s]", roleName);
            throw new DatabaseReadException(err, e);
        }

        return privilegeList;
    }

    @Override
    public List<Privilege> getPrivilegeList(String roleName) throws DatabaseReadException {
        try {
            return privilegeRepo.findByRoleName(roleName);
        } catch (Exception e) {
            throw new DatabaseReadException("Can not get privileges", e);
        }
    }
}
