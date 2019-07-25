package com.guli.common.handler;


import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.vo.R;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R error(Exception e){

        e.printStackTrace();
        return R.error();
    }


    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseBody
    public R BadSqlGrammarException(BadSqlGrammarException e){

        e.printStackTrace();
        return R.error().setResult(ResultCodeEnum.BAD_SQL_GRAMMAR);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public R HttpMessageNotReadableException(HttpMessageNotReadableException e){

        e.printStackTrace();
        return R.error().setResult(ResultCodeEnum.JSON_PARSE_ERROR);
    }

    @ExceptionHandler(GuliException.class)
    @ResponseBody
    public R GuliException(GuliException e){

        e.printStackTrace();
        return R.error().message(e.getMessage()).code(e.getCode());
    }


}
