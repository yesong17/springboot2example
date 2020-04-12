package cn.net.colin.controller.sysManage;

import cn.net.colin.common.exception.entity.ResultCode;
import cn.net.colin.common.exception.entity.ResultInfo;
import cn.net.colin.common.util.SnowflakeIdWorker;
import cn.net.colin.common.util.SpringSecurityUtil;
import cn.net.colin.model.common.TreeNode;
import cn.net.colin.model.sysManage.*;
import cn.net.colin.service.sysManage.ISysOrgService;
import cn.net.colin.service.sysManage.ISysRoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Package: cn.net.colin.controller.sysManage
 * @Author: sxf
 * @Date: 2020-3-22
 * @Description: 角色管理
 */
@Controller
@RequestMapping("/roleManage")
public class RoleManageController {
    Logger logger = LoggerFactory.getLogger(RoleManageController.class);

    @Autowired
    private ISysRoleService sysRoleService;
    @Autowired
    private ISysOrgService sysOrgService;

    @GetMapping("/roleManageList")
    public String roleManageList(){
        return "sysManage/roleManage/roleManageList";
    }

    /**
     * 返回角色信息列表
     * @param paramMap
     *  roleName 角色名字（模糊查询）
     *  areaCode 地区编码
     *  page 页码
     *  limit 每页数据量
     * @return ResultInfo 自定义结果返回实体类
     * @throws IOException
     */
    @GetMapping("/roleList")
    @ResponseBody
    public ResultInfo roleList(@RequestParam Map<String,Object> paramMap) throws IOException {
        int pageNum = paramMap.get("page") == null ? 1 : Integer.parseInt(paramMap.get("page").toString());
        int pageSize = paramMap.get("limit") == null ? 10 : Integer.parseInt(paramMap.get("limit").toString());
        PageHelper.startPage(pageNum,pageSize);
        List<SysRole> roleList = sysRoleService.selectByParams(paramMap);
        PageInfo<SysRole> result = new PageInfo(roleList);
        return ResultInfo.ofDataAndTotal(ResultCode.SUCCESS,roleList,result.getTotal());
    }

    /**
     * 返回指定机构可用角色信息列表
     * @param orgCode 机构编码
     * @return ResultInfo 自定义结果返回实体类
     * @throws IOException
     */
    @GetMapping("/roleList/{orgCode}")
    @ResponseBody
    public ResultInfo roleList(@PathVariable("orgCode") String orgCode) throws IOException {
        SysUser sysUser = SpringSecurityUtil.getPrincipal();
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("orgCode",orgCode);
        SysOrg sysOrg = sysOrgService.selectByOrgCode(orgCode);
        if(sysOrg != null && sysOrg.getAreaCode() != null){
            params.put("areaCode",sysOrg.getAreaCode());
        }
        List<SysRole> roleList = sysRoleService.selectByParams(params);
        return ResultInfo.ofData(ResultCode.SUCCESS,roleList);
    }

    /**
     * 跳转到角色添加页面
     * @return
     */
    @GetMapping("/role")
    public String role(){
        return "sysManage/roleManage/saveOrUpdateRole";
    }

    /**
     * 保存角色信息
     * @param sysRole
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN_AUTH','INSERT_AUTH')")
    @PostMapping("/role")
    @ResponseBody
    public ResultInfo saveRole(SysRole sysRole,String [] systemPermissions){
        SysUser sysUser = SpringSecurityUtil.getPrincipal();
        sysRole.setId(SnowflakeIdWorker.generateId());
        sysRole.setCreateTime(new Date());
        if(sysUser != null){
            sysRole.setCreateUser(sysUser.getLoginName());
            if(sysRole.getRoleAttr() == 1){//私有
                sysRole.setOrgCode(sysUser.getOrgCode());
            }
        }
        int num = sysRoleService.saveRoleAndPermissions(sysRole,systemPermissions);
        ResultInfo resultInfo = ResultInfo.of(ResultCode.UNKNOWN_ERROR);
        if(num > 0){
            resultInfo = ResultInfo.ofData(ResultCode.SUCCESS,sysRole);
        }
        return resultInfo;
    }

    /**
     * 跳转到角色编辑页面
     * @return
     */
    @GetMapping("/role/{id}")
    public String role(@PathVariable("id") String id,Map<String,Object> modelMap){
        SysRole sysRole = sysRoleService.selectByPrimaryKey(Long.parseLong(id));
        modelMap.put("sysRole",sysRole);
        return "sysManage/roleManage/saveOrUpdateRole";
    }

    /**
     * 更新角色信息
     * @param sysRole
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN_AUTH','UPDATE_AUTH')")
    @PutMapping("/role")
    @ResponseBody
    public ResultInfo updateRole(SysRole sysRole,String [] systemPermissions){
        SysUser sysUser = SpringSecurityUtil.getPrincipal();
        if(sysUser != null){
            if(sysRole.getRoleAttr() == 1){//私有
                SysRole role = sysRoleService.selectByPrimaryKey(sysRole.getId());
                if(role.getRoleAttr() != 1){//共享改私有
                    sysRole.setOrgCode(sysUser.getOrgCode());
                }
            }
        }
        int num = sysRoleService.updateRoleAndPermissions(sysRole,systemPermissions);
        ResultInfo resultInfo = ResultInfo.of(ResultCode.UNKNOWN_ERROR);
        if(num > 0){
            resultInfo = ResultInfo.ofData(ResultCode.SUCCESS,sysRole);
        }
        return resultInfo;
    }

    /**
     * 根据id集合，删除角色
     * @param ids
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN_AUTH','DELETE_AUTH')")
    @DeleteMapping("/role")
    @ResponseBody
    public ResultInfo deleteRole(Long [] ids){
        int num = sysRoleService.deleteBatchByPrimaryKey(ids);
        ResultInfo resultInfo = ResultInfo.of(ResultCode.UNKNOWN_ERROR);
        if(num > 0){
            resultInfo = ResultInfo.of(ResultCode.SUCCESS);
        }
        return resultInfo;
    }

    /**
     * 跳转角色菜单授权页面
     * @return
     */
    @GetMapping("/roleAndMenu")
    public String roleAndMenu(){
        return "sysManage/roleManage/roleAndMenu";
    }

    /**
     * 获取以地区为父节点的角色ztree树结构数据
     * @param minAreaLevel 最小行政级别（例如：参数为4 则查询县级及以上行政级别的地区）
     * @return ResultInfo 自定义结果返回实体类
     * @throws IOException
     */
    @GetMapping("/roleWithAreaListTree/{minAreaLevel}")
    @ResponseBody
    public ResultInfo roleWithAreaListTree(@PathVariable("minAreaLevel") String minAreaLevel) throws IOException {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        if(minAreaLevel != null && !minAreaLevel.equals("")){
            paramMap.put("minAreaLevel",Integer.parseInt(minAreaLevel));
        }
        List<TreeNode> treeNodeList = sysRoleService.roleWithAreaListTree(paramMap);
        return ResultInfo.ofData(ResultCode.SUCCESS,treeNodeList);
    }
    /**
     * 保存角色菜单信息
     * @param params
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN_AUTH','INSERT_AUTH')")
    @PostMapping("/roleAndMenu")
    @ResponseBody
    public ResultInfo saveRoleAndMenu(@RequestParam Map<String,Object> params){
        int num = sysRoleService.saveRoleAndMenu(params);
        ResultInfo resultInfo = ResultInfo.of(ResultCode.UNKNOWN_ERROR);
        if(num > 0){
            resultInfo = ResultInfo.of(ResultCode.SUCCESS);
        }
        return resultInfo;
    }

    /**
     * 根据角色id，查询角色关联的菜单
     * @param roleId
     * @return
     */
    @GetMapping("/roleAndMenu/{roleId}")
    @ResponseBody
    public ResultInfo roleAndMenu(@PathVariable("roleId") String roleId){
        ResultInfo resultInfo = ResultInfo.of(ResultCode.UNKNOWN_ERROR);
        List<String> menuList = this.sysRoleService.selectMenuIdsByRoleId(Long.parseLong(roleId));
        resultInfo = ResultInfo.ofData(ResultCode.SUCCESS,menuList);
        return  resultInfo;
    }

    /**
     * 返回指定用户关联的角色id集合
     * @return ResultInfo 自定义结果返回实体类
     * @param userId 用户id
     * @throws IOException
     */
    @GetMapping("/userRoleList/{userId}")
    @ResponseBody
    public ResultInfo userRoleList(@PathVariable("userId") String userId) throws IOException {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("userId",Long.parseLong(userId));
        List<Long> roleIdList = this.sysRoleService.selectRoleIdListByUserId(paramMap);
        return ResultInfo.ofDataAndTotal(ResultCode.SUCCESS,roleIdList,roleIdList.size());
    }

    /**
     * 跳转角色用户绑定页面
     * @return
     */
    @GetMapping("/roleAndUser")
    public String roleAndUser(){
        return "sysManage/roleManage/roleAndUser";
    }

    /**
     * 角色绑定用户
     * @param roleId 角色id
     * @param users 用户id集合
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN_AUTH','INSERT_AUTH')")
    @PostMapping("/roleAndUser/{roleId}")
    @ResponseBody
    public ResultInfo roleAndUser(@PathVariable("roleId") String roleId,String [] users){
        ResultInfo resultInfo = ResultInfo.of(ResultCode.UNKNOWN_ERROR);
        int num = sysRoleService.saveRoleAndUsers(roleId,users);
        if(num > 0){
            resultInfo = ResultInfo.of(ResultCode.SUCCESS);
        }
        return resultInfo;
    }

    /**
     * 解除角色用户绑定关系
     * @param roleId 角色id
     * @param users 用户id集合
     * @return
     */
    @PreAuthorize("hasAnyAuthority('ADMIN_AUTH','DELETE_AUTH')")
    @DeleteMapping("/roleAndUser/{roleId}")
    @ResponseBody
    public ResultInfo deleteRoleAndUser(@PathVariable("roleId") String roleId,Long [] users){
        ResultInfo resultInfo = ResultInfo.of(ResultCode.UNKNOWN_ERROR);
        int num = sysRoleService.deleteRoleAndUser(roleId,users);
        if(num > 0){
            resultInfo = ResultInfo.of(ResultCode.SUCCESS);
        }
        return resultInfo;
    }

}
