package ${appBaseDir}.aspect;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 日志记录
 * @className: com.ithxh.base.aop.LogAspect.java
 * @author hejianhui@wegooooo.com
 * @date: 2016年11月17日 下午8:28:03
 */
@Aspect
@Component
public class ServiceAspect {
	
	private final Logger logger = Logger.getLogger(this.getClass());

	private long startTimeMillis = 0; // 开始时间
	private long endTimeMillis = 0; // 结束时间
	private Object result;
	private StringBuffer sb = new StringBuffer();
	
	@Pointcut("execution(public * ${appBaseDir}.service.*.*(..)) || execution(public * ${appBaseDir}.*.service.*.*(..)) || execution(public * ${appBaseDir}.*.*.service.*.*(..))")
    public void logPoint() {}

	@Before(value="logPoint()")
	public void doBeforeInServiceLayer(JoinPoint joinPoint) {
		startTimeMillis = System.currentTimeMillis(); // 记录方法开始执行的时间
	}

	@After(value="logPoint()")
	public void doAfterInServiceLayer(JoinPoint jp) {
		try {
			endTimeMillis = System.currentTimeMillis(); // 记录方法执行完成的时间
			sb.append("-----------------------------").append(jp.getSignature().getDeclaringTypeName()).append(" start-----------------------------");
			sb.append("\n\n\n目标方法：").append(jp.getSignature().getDeclaringTypeName()).append(".").append(jp.getSignature().getName());
			sb.append("\n\n传入参数："+ Arrays.toString(jp.getArgs()));
			this.printOptLog();
			sb.append("-----------------------------").append(jp.getSignature().getDeclaringTypeName()).append(" end-----------------------------");
		} catch (Exception e) {
			logger.error("日志切面出错",e);
		}
	}

	@Around(value = "logPoint()")
	public Object doAround(ProceedingJoinPoint pj) throws Throwable {
		Object result = pj.proceed();// result的值就是被拦截方法的返回值
		this.result = result;
		return result;
	}

	private void printOptLog() {
		String optTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTimeMillis);
		sb.append("\n\n开始时间：" + optTime + " \n消耗时间：" + (endTimeMillis - startTimeMillis) + "ms");
		if (result!=null) {
			sb.append("\n\n返回结果："+result);
			sb.append("\n\n");
		}
	}
}
