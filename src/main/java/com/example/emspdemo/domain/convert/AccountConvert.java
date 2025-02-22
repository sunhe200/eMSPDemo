package com.example.emspdemo.domain.convert;

import com.example.emspdemo.domain.dto.AccountDTO;
import com.example.emspdemo.domain.po.AccountPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountConvert {
    AccountConvert INSTANCE = Mappers.getMapper(AccountConvert.class);

    AccountDTO po2Dto(AccountPO po);
    AccountPO dto2Po(AccountDTO dto);
}
