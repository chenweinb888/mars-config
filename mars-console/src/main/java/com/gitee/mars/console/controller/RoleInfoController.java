package com.gitee.mars.console.controller;

import com.gitee.mars.common.annotation.IsMenu;
import com.gitee.mars.common.vo.RespVo;
import com.gitee.mars.core.service.RoleInfoService;
import com.gitee.mars.dao.entity.RoleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author fashionbrot
 * @version 0.1.0
 * @date 2019/12/8 22:45
 */
@Controller
@RequestMapping("/role")
public class RoleInfoController {
    @Autowired
    private RoleInfoService envInfoFacade;

    @GetMapping("/index")
    @IsMenu
    public String index(){
        return "/role/index";
    }

    @RequestMapping(value = "add")
    @ResponseBody
    public RespVo add(@RequestBody  RoleInfo roleInfo) {
        envInfoFacade.add(roleInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "update")
    @ResponseBody
    public RespVo update(@RequestBody RoleInfo roleInfo) {
        envInfoFacade.update(roleInfo);
        return RespVo.success();
    }

    @RequestMapping(value = "deleteById")
    @ResponseBody
    public RespVo deleteById(Long id) {
        envInfoFacade.deleteById(id);
        return RespVo.success();
    }

    @RequestMapping(value = "queryById")
    @ResponseBody
    public RoleInfo queryById(Long id) {
        return envInfoFacade.queryById(id);
    }

    @RequestMapping(value = "queryAll")
    @ResponseBody
    public List<RoleInfo> queryAll() {
        return envInfoFacade.queryAll();
    }


}