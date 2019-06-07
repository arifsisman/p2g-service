package vip.yazilim.play2gether.web.controller;

import vip.yazilim.play2gether.web.entity.old.SystemUser;
import vip.yazilim.play2gether.web.util.SecurityHelper;
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

    private Logger LOGGER = LoggerFactory.getLogger(PageControllerAdvice.class);

    @ModelAttribute
    public void handleRequest(HttpServletRequest request, Model model) {
        String requestURI = request.getRequestURI();
        SystemUser systemUser = SecurityHelper.getSystemUser();

        LOGGER.info("Adding systemUser to model."
                        + "\n\t User Name: {} {}"
                        + "\n\t User Role: {}"
                , systemUser.getFirstName()
                , systemUser.getLastName()
                , systemUser.getSystemRole().getName());
        model.addAttribute("systemUser", systemUser);

    }

}
