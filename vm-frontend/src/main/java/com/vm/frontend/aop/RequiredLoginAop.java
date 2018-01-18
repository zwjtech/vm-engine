package com.vm.frontend.aop;

import com.vm.base.exception.VmRuntimeException;
import com.vm.base.util.CommonUtil;
import com.vm.base.util.Response;
import com.vm.frontend.resolve.OnlineConstants;
import com.vm.frontend.resolve.OnlineUserMethodArgumentResolver;
import com.vm.frontend.service.dto.VmUsersDto;
import com.vm.frontend.service.exception.VmUsersException;
import com.vm.frontend.service.inf.VmUsersService;
import com.vm.frontend.util.SessionManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@Aspect
public class RequiredLoginAop extends CommonUtil {
    private final Logger logger = LoggerFactory.getLogger(RequiredLoginAop.class);


    @Pointcut("execution(* com.vm.frontend.controller..*.*(..))")
    public void declareJoinPointExpression() {
    }


    @Around("declareJoinPointExpression() && @annotation(requiredLogin)")
    public Object doAroundAdvice(ProceedingJoinPoint joinPoint, RequiredLogin requiredLogin) throws Exception {

        Object data = null;
        Response response = new Response();
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader(OnlineConstants.KEY_OF_ACCESS_TOKEN);
            Long userId = (Long) SessionManager.getOnlineUserInfo(token);
            if (userId == null) {
                throw new VmUsersException(VmUsersException.ErrorCode.USER_IS_OFFLINE.getCode(),
                        VmUsersException.ErrorCode.USER_IS_OFFLINE.getMsg());
            }

            /**
             * 接下来会执行{@link OnlineUserMethodArgumentResolver}
             */

            //执行方法
            data = joinPoint.proceed();

            //如果返回值为Map或者Response的实例，代表采用ajax方式
            if (data instanceof Map) {
                response.setData((Map<Object, Object>) data);
            } else if (data instanceof Response) {
                response = (Response) data;
            } else {//页面转发
                return data;
            }
        } catch (VmRuntimeException e) {//提供详细错误信息输出到前台
            e.printStackTrace();
            logger.error("ERROR ==>  {}", e.toString());
            response.setCode(e.getErrorCode());
            response.setMsg(e.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
            logger.error("ERROR ==>  {}", e.toString());
            response.setCode(Response.ResponseCode.FAILURE.getCode());
            response.setMsg(Response.ResponseCode.FAILURE.getMsg());
        }
        return response;

    }

//    private HttpServletRequest getHttpServletRequest() {
//        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        HttpServletRequest httpServletRequest = attributes.getRequest();
//        return httpServletRequest;
//    }

}
