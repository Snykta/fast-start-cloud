package com.snykta.system.service.impl;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.snykta.tools.constant.DictValueConstant;
import com.snykta.tools.exception.ServiceException;
import com.snykta.system.mapper.SysUserMapper;
import com.snykta.system.dto.SysUserDto;
import com.snykta.system.entity.SysUserEntity;
import com.snykta.system.service.ISysUserService;
import com.snykta.tools.utils.CyConvertUtil;
import com.snykta.tools.utils.CyEncryptUtil;
import com.snykta.tools.utils.CyObjUtil;
import com.snykta.tools.utils.CyStrUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.snykta.basic.web.web.service.BaseService;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户信息表
 *
 * 1、禁止删除 @Transactional(readOnly = true)！！！！增删改的方法上必须加上 @Transactional(rollbackFor = Exception.class)否则将操作失败！！！
 * 2、禁止继承或者实现mybatisPlus自带的crud基类service
 * 3、所有crud操作下沉到Mapper层，service只关心业务
 *
 *
 * @author Snykta
 * @since 2023-09-27
 */
@Slf4j
@Service("sysUserService")
@Transactional(readOnly = true)
public class SysUserServiceImpl extends BaseService implements ISysUserService {

    private final SysUserMapper sysUserMapper;

    public SysUserServiceImpl(SysUserMapper sysUserMapper) {
        this.sysUserMapper = sysUserMapper;
    }


    /**
     * 登录
     * @param phoneNumber
     * @param password
     * @return
     */
    @Override
    public SysUserDto doLogin(String phoneNumber, String password) {
        if (CyStrUtil.isEmpty(phoneNumber)) {
            throw new ServiceException("请输入登录手机号");
        }
        if (CyStrUtil.isEmpty(password)) {
            throw new ServiceException("请输入登录密码");
        }
        SysUserEntity sysUserEntity = sysUserMapper.selectOne(Wrappers.<SysUserEntity>lambdaQuery().eq(SysUserEntity::getPhoneNumber, phoneNumber));
        if (CyObjUtil.isNull(sysUserEntity)) {
            throw new ServiceException("账户或密码不正确");
        }
        if (!CyStrUtil.equals(CyEncryptUtil.decryptAes(sysUserEntity.getPassword()), password)) {
            throw new ServiceException("账户或密码不正确");
        }
        SysUserDto sysUserDto = CyConvertUtil.convertToDto(sysUserEntity, SysUserDto.class);
        // TODO 添加权限、角色


        return sysUserDto;
    }

    /**
     * 注册
     * @param sysUserDto
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doRegister(SysUserDto sysUserDto) {
        if (CyObjUtil.isNull(sysUserDto)) {
            throw new ServiceException("注册信息不能为空");
        }
        if (CyStrUtil.isEmpty(sysUserDto.getPhoneNumber())) {
            throw new ServiceException("手机号不能为空");
        }
        if (CyStrUtil.isEmpty(sysUserDto.getPassword())) {
            throw new ServiceException("密码不能为空");
        }
        if (CyStrUtil.isEmpty(sysUserDto.getNickName())) {
            throw new ServiceException("昵称不能为空");
        }
        boolean existsFlag = sysUserMapper.exists(Wrappers.<SysUserEntity>lambdaQuery().eq(SysUserEntity::getPhoneNumber, sysUserDto.getPhoneNumber()));
        if (existsFlag) {
            throw new ServiceException("注册手机号已存在");
        }
        sysUserDto.setId(null);
        sysUserDto.setStatusCode(DictValueConstant.sys_status_code_正常);
        sysUserDto.setPassword(CyEncryptUtil.encryptAes(sysUserDto.getPassword()));
        sysUserDto.setCreateId(null);
        sysUserDto.setCreateName(null);
        sysUserDto.setCreateTime(null);
        sysUserMapper.insert(CyConvertUtil.convertToEntity(sysUserDto, SysUserEntity.class));
    }


}