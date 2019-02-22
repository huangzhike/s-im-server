package mmp.im.auth.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import mmp.im.common.service.service.FriendMessageService;
import mmp.im.common.service.service.GroupMessageService;
import mmp.im.common.service.service.SessionService;
import mmp.im.common.service.service.XService;
import mmp.im.common.model.GateInfo;
import mmp.im.common.model.Group;
import mmp.im.common.model.Response;
import mmp.im.common.model.User;
import mmp.im.common.protocol.ProtobufMessage;
import mmp.im.common.util.SessionUtil;
import mmp.im.common.util.token.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;


@Controller
public class XController {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final CopyOnWriteArraySet<GateInfo> copyOnWriteArraySet = new CopyOnWriteArraySet<>();
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


    @GetMapping("/getSessionList")
    @ResponseBody
    public Object getSessionList() {
        String token = request.getHeader(this.JWT_HEADER);


        Long id = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (Long) map.get("id"))
                .orElse(null);

        JSONObject jsonObject = new JSONObject();
        if (id != null) {
            Set<String> friendIdList = sessionService.getRecentFriendSession(id);
            Set<String> groupIdList = sessionService.getRecentGroupSession(id);

            friendIdList.forEach(friendId -> {

                String sessionId = SessionUtil.getSessionId(id, Long.valueOf(friendId));

                List<ProtobufMessage.FriendMessage> friendMessageList = friendMessageService.getFriendMessage(sessionId, 0L, 30L);

                String readMessageId = friendMessageService.getOfflineUserFriendMessage(id, Long.valueOf(friendId));

            });

            groupIdList.forEach(groupId -> {

                List<ProtobufMessage.GroupMessage> groupMessageList = groupMessageService.getGroupMessage(Long.valueOf(groupId), 0L, 30L);

                String readMessageId = groupMessageService.getOfflineUserGroupMessage(id, Long.valueOf(groupId));

            });

            // List<ProtobufMessage.GroupMessage> groupMessageList = groupIdList.stream().map(groupId -> {
            //     return groupMessageService.getGroupMessage(Long.valueOf(groupId), 0L, 30L);
            // }).collect(Collectors.toList());


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

        List<User> groupList = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (Long) map.get("id"))
                .map(id -> xService.getFriendList(id))
                .orElse(null);


        Response response = new Response();
        response.setSuccess(true);

        response.setData(groupList);

        return response;
    }

    @GetMapping("/getGroupList")
    @ResponseBody
    public Object getGroupList() {

        String token = request.getHeader(this.JWT_HEADER);

        List<Group> groupList = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (Long) map.get("id"))
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

        List<User> groupList = Optional.ofNullable(token)
                .map(JWTUtil::parseJWT)
                .map(map -> (Long) map.get("id"))
                .map(id -> xService.getGroupUserList(id))
                .orElse(null);

        Response response = new Response();
        response.setSuccess(true);

        response.setData(groupList);

        return response;
    }

    @GetMapping("/getGateList")
    @ResponseBody
    public Object getGateList() {

        Response response = new Response();
        response.setSuccess(true);
        response.setData(this.copyOnWriteArraySet);
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
