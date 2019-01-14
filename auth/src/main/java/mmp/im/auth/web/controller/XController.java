package mmp.im.auth.web.controller;

import mmp.im.auth.model.Response;
import mmp.im.auth.model.User;
import mmp.im.auth.service.XService;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
public class XController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private XService xService;

    @GetMapping("/listRecord")
    @ResponseBody
    public Object listRecord(Date beginTime, Date endTime,
                             String callNo, String calledNo, String status,
                             String connectType,
                             @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                             @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {

        PageHelper.startPage(currentPage, pageSize);

        LOG.info(currentPage + " " + pageSize);
        LOG.info(beginTime + " " + endTime + " " + callNo + " " + calledNo + " " + status + " " + connectType);


        return null;
    }

    @PostMapping("login")
    @ResponseBody
    public Object login(User user) {

        User u = xService.getUser(user);

        if (u != null) {
            return Response.success().setData("token");
        }

        return Response.fail();
    }

    @PostMapping("logout")
    @ResponseBody
    public Object logout(User user) {

        return Response.success();
    }

    @PostMapping("checkToken")
    @ResponseBody
    public Object checkToken(User user) {

        return Response.success();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
