package com.rekoe.module.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import com.rekoe.common.Message;
import com.rekoe.domain.AcquisitionTemp;
import com.rekoe.domain.ArticleCategory;
import com.rekoe.domain.CmsAcquisition;
import com.rekoe.service.AcquisitionService;
import com.rekoe.service.AcquisitionTempService;
import com.rekoe.service.ArticleCategoryService;

@IocBean
@At("/admin/acquisition")
@RequiresAuthentication
public class AcquisitionAct {

	@Inject
	private AcquisitionService acquisitionService;

	@Inject
	private ArticleCategoryService articleCategoryService;

	@Inject
	private AcquisitionTempService acquisitionTempService;

	@At
	@Ok("fm:template.admin.article_acquisition.list")
	public Object list(@Param("pageNumber") Integer pageNumber) {
		return acquisitionService.getListByPager(pageNumber, 20);
	}

	@At
	@Ok("fm:template.admin.article_acquisition.add")
	public List<ArticleCategory> add() {
		List<ArticleCategory> list = articleCategoryService.query(null, null);
		return list;
	}

	@At
	@Ok("json")
	public Message save(@Param("::acqu.") CmsAcquisition acquisition, HttpServletRequest req) {
		acquisitionService.insert(acquisition);
		return Message.success("admin.message.success", req);
	}

	@At
	@Ok("json")
	public Message start(@Param("id") int id, HttpServletRequest req) {
		acquisitionService.start(id);
		return Message.success("admin.message.success", req);
	}

	@At
	@Ok("fm:template.admin.article_acquisition.edit")
	public CmsAcquisition edit(@Param("id") long id) {
		return acquisitionService.fetch(id);
	}

	@At
	@Ok("json")
	public Message update(@Param("::acqu.") CmsAcquisition acquisition, HttpServletRequest req) {
		acquisitionService.update(acquisition);
		return Message.success("admin.message.success", req);
	}

	@At
	@Ok("fm:template.admin.article_acquisition.progress_data")
	public int v_progress_data(HttpServletRequest req) {
		List<AcquisitionTemp> list = acquisitionTempService.getList();
		req.setAttribute("list", list);
		return acquisitionTempService.getPercent();
	}

	@At
	@Ok("json")
	public Message v_check_complete(HttpServletRequest req) {
		List<AcquisitionTemp> list = acquisitionTempService.getList();
		if (Lang.isEmpty(list)) {
			return Message.success("admin.message.success", req);
		}
		return Message.error("admin.message.success", req);
	}

	@At
	@Ok("fm:template.admin.article_acquisition.progress")
	public void progress() {

	}
	
	@At
	@Ok("json")
	public Message delete(@Param("ids") String[] ids, HttpServletRequest req) {
		acquisitionService.delete(ids);
		return Message.success("admin.message.success", req);
	}
}
