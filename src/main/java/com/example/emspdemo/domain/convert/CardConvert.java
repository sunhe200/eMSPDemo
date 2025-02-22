package com.example.emspdemo.domain.convert;

import com.example.emspdemo.domain.dto.CardDTO;
import com.example.emspdemo.domain.po.CardPO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CardConvert {
    CardConvert INSTANCE = Mappers.getMapper(CardConvert.class);

    CardPO dto2PO(CardDTO dto);
    CardDTO po2Dto(CardPO po);
}
