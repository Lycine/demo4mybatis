package org.jozif.demo4mybatis.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jozif.demo4mybatis.entity.User;
import org.jozif.demo4mybatis.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "添加用户", notes = "只填写必要信息")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "name", value = "用户昵称", required = true, dataType = "String", paramType = "path"),
//            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String", paramType = "path"),
//            @ApiImplicitParam(name = "email", value = "邮箱", required = true, dataType = "String", paramType = "path"),
//            @ApiImplicitParam(name = "salt", value = "盐值", required = true, dataType = "String", paramType = "path")
//    })
    @ResponseBody
    @RequestMapping(value = "/add", produces = {"application/json;charset=UTF-8"}, method = {RequestMethod.GET, RequestMethod.POST})
    public int addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @ApiOperation(value = "查询用户", notes = "填写页码和大小")
    @ResponseBody
    @RequestMapping(value = "/all/{pageNum}/{pageSize}", produces = {"application/json;charset=UTF-8"}, method = {RequestMethod.GET})
    public Object findAllUser(@ApiParam(value = "页码", required = true) @PathVariable("pageNum") int pageNum,
                              @ApiParam(value = "每页大小", required = true) @PathVariable("pageSize") int pageSize) {

        return userService.findAllUser(pageNum, pageSize);
    }
}