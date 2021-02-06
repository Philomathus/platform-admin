package com.qiqilm.server.admin.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 请求日志方面
 *
 * @author dan
 * @version 1.0
 * @date 2020/11/04
 */
@Slf4j
@Component
@Aspect
public class RequestLogAspect {

    /**
     * 请求地址
     */
    @Pointcut("execution(* com.qiqilm.server.admin.controller..*(..))")
    public void requestServer() {
    }

    /**
     * 任务地址
     */
    @Pointcut("execution(* com.qiqilm.server.admin.task..*(..))")
    public void taskServer() {
    }

    /**
     * 请求切面
     *
     * @param proceedingJoinPoint 进行连接点
     * @return {@link Object}
     * @throws Throwable throwable
     * @author dan
     * @since 2020/11/17
     */
    @Around("requestServer()")
    public Object doRequestAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return runProceed(proceedingJoinPoint);
    }

    /**
     * 任务切面
     *
     * @param proceedingJoinPoint 进行连接点
     * @return {@link Object}
     * @throws Throwable throwable
     */
    @Around("taskServer()")
    public Object doTaskAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return runProceed(proceedingJoinPoint);
    }

    private Object runProceed(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        StringBuilder sb = new StringBuilder(1000);
        sb.append("\n").append("----------------------------").append("请求时间    ").append(getTime()).append("----------------------------\n");
        //类名称
        sb.append("Class     : ").append(proceedingJoinPoint.getSignature().getDeclaringTypeName()).append("\n");
        //方法名称
        sb.append("Method    : ").append(proceedingJoinPoint.getSignature().getName()).append("\n");
        //请求成功
        sb.append("Result    : ").append("成功").append("\n");
        //所有的请求参数
        sb.append("Params    : ").append(getRequestParamsByProceedingJoinPoint(proceedingJoinPoint)).append("\n");
        //接口时间
        sb.append("request Time: ").append(System.currentTimeMillis() - start).append("\n");
        //结果
//        sb.append("Result    : ").append(result).append("\n");
        //打印
        System.out.println(sb.toString());
        return result;
    }

    /**
     * 执行异常
     *
     * @param joinPoint 连接点
     * @param e         e
     * @return
     * @author dan
     * @since 2020/11/17
     */
    @AfterThrowing(pointcut = "requestServer()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, RuntimeException e) {
        Signature signature = joinPoint.getSignature();
        StringBuilder sb = new StringBuilder(1000);
        sb.append("\n").append("----------------------------").append("请求时间    ").append(getTime()).append("----------------------------\n");
        //类名称
        sb.append("Class     : ").append(signature.getDeclaringTypeName()).append("\n");
        //方法名称
        sb.append("Method    : ").append(signature.getName()).append("\n");
        //请求成功
        sb.append("Result    : ").append("失败").append("\n");
        //所有的请求参数
        sb.append("Params    : ").append(getRequestParamsByJoinPoint(joinPoint)).append("\n");
        //结果
        sb.append("Result    : ").append(getExceptionMsg(e)).append("\n");
        //打印
        System.out.println(sb.toString());
//        log.info("Error Request Info      : {}", JSON.toJSONString(requestErrorInfo));
    }

    /**
     * 获取入参
     *
     * @param proceedingJoinPoint
     * @return
     */
    private Map<String, Object> getRequestParamsByProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
        Map<String, Object> requestParams = new HashMap<>();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                Object value = paramValues[i];

                //如果是文件对象
                if (value instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) value;
                    value = file.getOriginalFilename();  //获取文件名
                }

                requestParams.put(paramNames[i], value);
            }
        }
        return requestParams;
    }

    public static final String getTime() {
        return dateTimeNow("yyyy-MM-dd HH:mm:ss");
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 获取exception的字符串
     *
     * @param e e
     * @return {@link String }
     * @author dan
     * @since 2020/11/17
     */
    public static String getExceptionMsg(Exception e) {
        StringWriter sw = new StringWriter();
        try {
            e.printStackTrace(new PrintWriter(sw));
        } finally {
            try {
                sw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sw.getBuffer().toString().replaceAll("\\$", "T");
    }

}
