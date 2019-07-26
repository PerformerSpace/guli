package com.guli.ucenter.service;

import com.guli.ucenter.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author lyy
 * @since 2019-07-20
 */
public interface MemberService extends IService<Member> {

    Integer countRegisterByDay(String day);

}
