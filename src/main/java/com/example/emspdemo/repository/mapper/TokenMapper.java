package com.example.emspdemo.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.emspdemo.repository.po.TokenPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TokenMapper extends BaseMapper<TokenPO> {
}
