package com.example.emspdemo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.emspdemo.repository.po.AccountPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<AccountPO> {
}
