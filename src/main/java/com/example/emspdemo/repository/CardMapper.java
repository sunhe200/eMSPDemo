package com.example.emspdemo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.emspdemo.domain.po.CardPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CardMapper extends BaseMapper<CardPO> {
}
