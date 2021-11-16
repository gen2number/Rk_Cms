package com.rekoe.crawler.core.processor.extractor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.Lang;
import org.nutz.log.Logs;

import com.rekoe.crawler.core.data.Task;
import com.rekoe.crawler.core.data.uri.CrawlResURI;

/**
 * 任务处理器接口-抽取内容资源实现类
 */
public class ExtractorContentResourceProcessor extends Extractor {

	private static final org.nutz.log.Log log = Logs.get();
	public ExtractorContentResourceProcessor() {
		super();
		setDefaultNextProcessor(new ExtractorFieldProcessor());
	}

	@Override
	protected void extract(Task task) {
		log.info("=========抽取内容资源=========");
		List<CrawlResURI> resCrawlURIList = task.getContentBean().getResCrawlURIList();
		if (task.getController().getCrawlScope().isExtractContentRes() && !Lang.isEmpty(resCrawlURIList)) {
			CrawlResURI parentCrawlResURI = new CrawlResURI();
			parentCrawlResURI.setPort(task.getCrawlURI().getPort());
			parentCrawlResURI.setHost(task.getCrawlURI().getHost());
			parentCrawlResURI.setRawPath(task.getCrawlURI().getRawPath());
			for (CrawlResURI crawlURI : resCrawlURIList) {
				crawlURI.setParentURI(parentCrawlResURI);
				String key = crawlURI.getNewResUrl();
				String value = crawlURI.getOriginResUrl();
				if (StringUtils.isNotEmpty(value)) {
					String context = task.getController().getHandler().handleResponse(task.getController().getHostCache().getHttpHostUrl(crawlURI));
					log.info(context);
					// TODOｓａｖｅ　ｔｏ　ｆｉｌｅ
					// task.getController().getCrawlScope().getFileHelper().saveFile(entity.getContent(),
					// Constants.SYSTEM_ROOT_PATH +key);
				}
			}
		}

	}

}
