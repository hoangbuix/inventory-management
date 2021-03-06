package com.inventory.dev.service;

import com.inventory.dev.entity.MenuEntity;
import com.inventory.dev.entity.Paging;
import com.inventory.dev.model.dto.MenuDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MenuService {
    List<MenuEntity> getListMenu(Paging paging, MenuEntity menu);

    void changeStatus(Integer id) throws Exception;

    void updatePermission(int roleId, int menuId, int permission) throws Exception;
}
