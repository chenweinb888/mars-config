package com.gitee.mars.dao.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gitee.mars.dao.entity.MenuRoleRelation;
import com.gitee.mars.dao.mapper.MenuRoleRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class MenuRoleRelationDao  {

    @Autowired
    private MenuRoleRelationMapper menuRoleRelationMapper;


    public int delete(QueryWrapper queryWrapper) {
        return menuRoleRelationMapper.delete(queryWrapper);
    }


    public int insert(MenuRoleRelation relation) {
        relation.setUpdateDate(new Date());
        return menuRoleRelationMapper.insert(relation);
    }
}
