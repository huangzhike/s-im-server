package mmp.im.auth.exception;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
@Order(-1000)
public class GlobalExceptionResolver implements HandlerExceptionResolver {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        ex.printStackTrace();
        // AJAX JSON
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        ResponseBody responseBodyAnno = null;
        if (handlerMethod != null) {
            responseBodyAnno = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ResponseBody.class);
        }

        ModelAndView modelAndView = new ModelAndView();

        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("msg", ex.getMessage());

        if (responseBodyAnno != null) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write(JSON.toJSONString(map));
            } catch (Exception e) {
                LOG.error("Exception... {}", e);
            }
        } else {
            modelAndView = new ModelAndView("error", map);
        }
        return modelAndView;

    }

}
