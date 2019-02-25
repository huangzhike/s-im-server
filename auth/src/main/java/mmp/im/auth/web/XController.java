package mmp.im.auth.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;

import im.database.service.FriendMessageService;
import im.database.service.GroupMessageService;
import im.database.service.ServerService;
import im.database.service.SessionService;
import mmp.im.auth.database.service.XService;
import mmp.im.common.model.*;
import mmp.im.common.util.session.SessionUtil;
import mmp.im.common.util.spring.SpringContextHolder;
import mmp.im.common.util.token.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Controller
public class XController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());


    private final String JWT_HEADER = "JWT_HEADER";

    @Autowired
    private XService xService;

    @Autowired
    private FriendMessageService friendMessageService;

    @Autowired
    private GroupMessageService groupMessageService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ServerService serverService;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/list")
    @ResponseBody
    public Object list(
            @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Response response = new Response();
        PageHelper.startPage(currentPage, pageSize);

        return response;
    }


    @GetMapping("/")
    @ResponseBody
    public Object test(){

        friendMessageService.updateReadUserFriendMessageId("dd", "dd",666L);
        LOG.warn("--- {}", friendMessageService.getReadUserFriendMessageId("dd", "dd"));

        return new JSONObject();
    }



    @GetMapping("/getSessionList")
    @ResponseBody
    public Object getSessionList() {
        String token = request.getHeader(this.JWT_HEADER);

        String id = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (String) map.get("id"))
                .orElse(null);

        JSONObject jsonObject = new JSONObject();

        if (id != null) {
            Set<String> friendIdList = sessionService.getUserRecentFriendSession(id);
            Set<String> groupIdList = sessionService.getUserRecentGroupSession(id);

            JSONObject friendSessionMap = new JSONObject();
            jsonObject.put("friendSession", friendSessionMap);

            friendIdList.forEach(friendId -> {
                JSONObject map = new JSONObject();
                friendSessionMap.put(friendId, map);
                Set<FriendMessage> friendMessageList = friendMessageService.getFriendMessageList(SessionUtil.getSessionId(id, friendId), 0L, 30L);
                map.put("friendMessageList", friendMessageList);
                String readMessageId = friendMessageService.getReadUserFriendMessageId(id, friendId);
                map.put("readMessageId", readMessageId);
            });

            JSONObject groupSessionMap = new JSONObject();
            jsonObject.put("groupSessionMap", groupSessionMap);

            groupIdList.forEach(groupId -> {
                JSONObject map = new JSONObject();
                groupSessionMap.put(groupId, map);
                Set<GroupMessage> groupMessageList = groupMessageService.getGroupMessageList(groupId, 0L, 30L);
                map.put("groupMessageList", groupMessageList);
                String readMessageId = groupMessageService.getReadUserGroupMessageId(id, groupId);
                map.put("readMessageId", readMessageId);
            });

        }


        Response response = new Response();
        response.setSuccess(true);
        response.setData(jsonObject);

        return response;
    }

    @GetMapping("/getFriendList")
    @ResponseBody
    public Object getFriendList() {
        String token = request.getHeader(this.JWT_HEADER);

        List<User> friendList = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (String) map.get("id"))
                .map(id -> xService.getFriendList(id))
                .orElse(null);

        Response response = new Response();
        response.setSuccess(true);

        response.setData(friendList);

        return response;
    }

    @GetMapping("/getGroupList")
    @ResponseBody
    public Object getGroupList() {

        String token = request.getHeader(this.JWT_HEADER);

        List<Group> groupList = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (String) map.get("id"))
                .map(id -> xService.getUserGroupList(id))
                .orElse(null);

        Response response = new Response();
        response.setSuccess(true);

        response.setData(groupList);

        return response;
    }


    @GetMapping("/getGroupMemberList")
    @ResponseBody
    public Object getGroupMemberList() {

        String token = request.getHeader(this.JWT_HEADER);

        List<User> groupMemberList = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (String) map.get("id"))
                .map(id -> xService.getGroupUserList(id))
                .orElse(null);

        Response response = new Response();
        response.setSuccess(true);

        response.setData(groupMemberList);

        return response;
    }

    @GetMapping("/getGateList")
    @ResponseBody
    public Object getGateList() {

        Response response = new Response();
        response.setSuccess(true);
        response.setData(serverService.getGateList());
        return response;
    }

    @PostMapping("getToken")
    @ResponseBody
    public Object getToken(User user) {
        Response response = new Response();
        User u = xService.getUser(user.getId());
        if (u != null && user.getPassword().equals(u.getPassword())) {
            response.setSuccess(true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", u.getId());
            String token = JWTUtil.createJWT(jsonObject);
            response.setData(token);
        }
        response.setSuccess(false);
        return response;
    }

}
