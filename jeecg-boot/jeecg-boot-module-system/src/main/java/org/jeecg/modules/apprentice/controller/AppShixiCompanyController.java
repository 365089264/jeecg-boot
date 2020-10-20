package org.jeecg.modules.apprentice.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.apprentice.entity.AppShixiCompany;
import org.jeecg.modules.apprentice.service.IAppShixiCompanyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysUser;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 实习公司
 * @Author: jeecg-boot
 * @Date:   2020-10-20
 * @Version: V1.0
 */
@Api(tags="实习公司")
@RestController
@RequestMapping("/apprentice/appShixiCompany")
@Slf4j
public class AppShixiCompanyController extends JeecgController<AppShixiCompany, IAppShixiCompanyService> {
	@Autowired
	private IAppShixiCompanyService appShixiCompanyService;
	
	/**
	 * 分页列表查询
	 *
	 * @param appShixiCompany
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "实习公司-分页列表查询")
	@ApiOperation(value="实习公司-分页列表查询", notes="实习公司-分页列表查询")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(AppShixiCompany appShixiCompany,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<AppShixiCompany> queryWrapper = QueryGenerator.initQueryWrapper(appShixiCompany, req.getParameterMap());
		Page<AppShixiCompany> page = new Page<AppShixiCompany>(pageNo, pageSize);
		IPage<AppShixiCompany> pageList = appShixiCompanyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param appShixiCompany
	 * @return
	 */
	@AutoLog(value = "实习公司-添加")
	@ApiOperation(value="实习公司-添加", notes="实习公司-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody AppShixiCompany appShixiCompany) {
		appShixiCompanyService.save(appShixiCompany);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param appShixiCompany
	 * @return
	 */
	@AutoLog(value = "实习公司-编辑")
	@ApiOperation(value="实习公司-编辑", notes="实习公司-编辑")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody AppShixiCompany appShixiCompany) {
		appShixiCompanyService.updateById(appShixiCompany);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "实习公司-通过id删除")
	@ApiOperation(value="实习公司-通过id删除", notes="实习公司-通过id删除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		appShixiCompanyService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "实习公司-批量删除")
	@ApiOperation(value="实习公司-批量删除", notes="实习公司-批量删除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.appShixiCompanyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "实习公司-通过id查询")
	@ApiOperation(value="实习公司-通过id查询", notes="实习公司-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		AppShixiCompany appShixiCompany = appShixiCompanyService.getById(id);
		if(appShixiCompany==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(appShixiCompany);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param appShixiCompany
    */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AppShixiCompany appShixiCompany) {
        return super.exportXls(request, appShixiCompany, AppShixiCompany.class, "实习公司");
    }

	 /**
	  * 实习公司审核
	  * @param jsonObject
	  * @return
	  */
	 //@RequiresRoles({"admin"})
	 @RequestMapping(value = "/approve", method = RequestMethod.PUT)
	 public Result<?> approve(@RequestBody JSONObject jsonObject) {
		 Result<AppShixiCompany> result = new Result<AppShixiCompany>();
		 try {
			 String ids = jsonObject.getString("ids");
			 String status = jsonObject.getString("status");
			 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
			 if(("xizhuren".equals(sysUser.getPost())&&"1".equals(status))||("fuzhuren".equals(sysUser.getPost())&&"0".equals(status)))
			 {
				 String[] arr = ids.split(",");
				 for (String id : arr) {
					 if(oConvertUtils.isNotEmpty(id)) {
						 this.appShixiCompanyService.update(new AppShixiCompany().setStatus(Integer.parseInt(status)),
								 new UpdateWrapper<AppShixiCompany>().lambda().eq(AppShixiCompany::getId,id));
					 }
				 }
			 }
			 else{
				 result.error500("您不具备审批权限！");
				 return result;
			 }
		 } catch (Exception e) {
			 log.error(e.getMessage(), e);
			 result.error500("操作失败"+e.getMessage());
		 }
		 result.success("操作成功!");
		 return result;

	 }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, AppShixiCompany.class);
    }

}
