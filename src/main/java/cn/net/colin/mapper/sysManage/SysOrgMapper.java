package cn.net.colin.mapper.sysManage;

import cn.net.colin.model.sysManage.SysOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author sxf
 * date:2020/03/04 17:48
 */
public interface SysOrgMapper {
    /** 
     * 根据ID删除
     * @param id 主键ID
     * @return 返回删除成功的数量
     */
    int deleteByPrimaryKey(Long id);

    /** 
     * 添加对象所有字段
     * @param record 插入字段对象(必须含ID）
     * @return 返回添加成功的数量
     */
    int insert(SysOrg record);

    /** 
     * 添加对象对应字段
     * @param record 插入字段对象(必须含ID）
     * @return 返回添加成功的数量
     */
    int insertSelective(SysOrg record);

    /** 
     * 根据ID查询
     * @param id 主键ID
     * @return 返回查询的结果
     */
    SysOrg selectByPrimaryKey(Long id);

    /** 
     * 根据ID修改对应字段
     * @param record 修改字段对象(必须含ID）
     * @return 返回更新成功的数量
     */
    int updateByPrimaryKeySelective(SysOrg record);

    /** 
     * 根据ID修改所有字段(必须含ID）
     * @param record 修改字段对象(必须含ID）
     * @return 返回更新成功的数量
     */
    int updateByPrimaryKey(SysOrg record);

    /**
     * 根据ID修改所有字段(必须含ID）
     * @param sysOrgList 插入对象(必须含ID）集合
     * @return 返回插入成功的数量
     */
    int insertBatch(List<SysOrg> sysOrgList);

    /**
     * 批量插入（字段可选）
     * @param sysOrgList 插入对象(必须含ID）集合
     * @return 返回插入成功的数量
     */
    int insertBatchSelective(List<SysOrg> sysOrgList);

    /**
     * 批量修改
     * @param sysOrgList 修改对象(必须含ID）集合
     * @return 返回修改成功的数量
     */
    int updateBatchByPrimaryKey(List<SysOrg> sysOrgList);

    /**
     * 批量修改(字段可选)
     * @param sysOrgList 修改对象(必须含ID）集合
     * @return 返回修改成功的数量
     */
    int updateBatchByPrimaryKeySelective(List<SysOrg> sysOrgList);

    /**
     * 根据机构编码查询机构信息
     * @param orgCode
     * @return
     */
    SysOrg selectByOrgCode(@Param("orgCode") String orgCode);
}