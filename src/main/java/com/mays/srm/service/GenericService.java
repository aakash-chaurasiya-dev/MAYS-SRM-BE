package com.mays.srm.service;

import java.util.List;

public interface GenericService<REQ_DTO,RES_DTO, ID> {
    RES_DTO create(REQ_DTO requestDTO);
    RES_DTO getById(ID id);
    List<RES_DTO> getAll();
    RES_DTO update(ID id, REQ_DTO requestDTO);
    void delete(ID id);
}
