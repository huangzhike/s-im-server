package mmp.im.logic.web;

import com.github.pagehelper.PageHelper;
import mmp.im.common.model.User;
import mmp.im.logic.service.XService;
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

    @GetMapping("/list")
    @ResponseBody
    public Object list(Date time,
                       String type,
                       @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        PageHelper.startPage(currentPage, pageSize);

        return null;
    }


    @PostMapping("logout")
    @ResponseBody
    public Object logout(User user) {
        return null;
    }


    @GetMapping("/")
    public String index() {
        return "index";
    }
}
