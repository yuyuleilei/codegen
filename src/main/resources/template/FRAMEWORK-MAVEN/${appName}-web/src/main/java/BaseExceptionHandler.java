import javax.servlet.http.HttpServletResponse;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xhh.bfun.bexception.BusinessException;
import com.xhh.bfun.bmodel.JsonResult;
import com.xhh.bfun.butils.JsonResultUtil;

@ControllerAdvice
public class BaseExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	@ResponseBody
	public JsonResult exceptionHandler(BusinessException e,HttpServletResponse response) {
		//打印错误信息
		ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        e.printStackTrace(pout);
        String msg = new String(out.toByteArray());
		logger.error(msg);
		//返回json信息
		JsonResult result = JsonResultUtil.error(e.getMsg());
		result.setCode(e.getCode());
		pout.close();
        try {
             out.close();
        } catch (Exception ee) {
        }
        return result;
	}

}
