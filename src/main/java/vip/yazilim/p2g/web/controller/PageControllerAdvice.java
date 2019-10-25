package vip.yazilim.p2g.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import vip.yazilim.p2g.web.entity.User;
import vip.yazilim.p2g.web.service.ISystemRoleService;
import vip.yazilim.p2g.web.util.SecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Emre Sen - 25.05.2019
 * @contact maemresen07@gmail.com
 */
@ControllerAdvice(basePackages = "vip.yazilim.play2gether.web")
public class PageControllerAdvice {

    @Autowired
    private ISystemRoleService systemRoleService;

//    public SystemRole getSystemRoleByUuid(String userUuid){
//        return systemRoleService.getSystemRoleByUuid(userUuid).get();
//    }

    private Logger LOGGER = LoggerFactory.getLogger(PageControllerAdvice.class);

    @ModelAttribute
    public void handleRequest(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        User user = SecurityHelper.getSystemUser();


        LOGGER.info("Adding systemUser to model."
                        + "\n\t User Name: {} {}"
                        + "\n\t User Role: {}"
                , user.getFirstName()
                , user.getLastName()
                , systemRoleService.getSystemRoleByUuid(user.getUuid()));
        model.addAttribute("systemUser", user);

    }

}
