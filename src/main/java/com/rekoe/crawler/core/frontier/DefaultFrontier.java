package com.rekoe.crawler.core.frontier;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.nutz.log.Logs;

import com.rekoe.crawler.core.CrawlerController;
import com.rekoe.crawler.core.data.Task;
import com.rekoe.crawler.core.data.queue.SimpleTaskQueue;
import com.rekoe.crawler.core.data.queue.TaskQueue;
import com.rekoe.crawler.core.data.queue.UrlQueue;
import com.rekoe.crawler.core.data.uri.CrawlLinkURI;

/**
 * 边界控制器 接口 实现类
 * 主要是加载爬行种子链接并根据加载的种子链接初始化任务队列，以备线程控制器（ProcessorThreadPool）开启的任务执行线程
 * （ProcessorThread）使用
 */
public class DefaultFrontier implements Frontier {
	private static final org.nutz.log.Log log = Logs.get();
	/** 爬虫控制器 */
	private CrawlerController controller;
	/** 计划UrlQueue对象 */
	private TaskQueue<CrawlLinkURI> planUrlQueue = new UrlQueue();
	/** 任务对象 */
	private TaskQueue<Task> taskQueue = new SimpleTaskQueue();
	/** 任务总数 */
	private transient int taskSize;

	/** 需要采集的数量 */
	private int gatherNum;

	public synchronized void finished(Task task) {
		task.finished();
		taskQueue.addExecTask(task);
		int finishedNum = taskQueue.getExecTaskNum();
		log.infof("已经完成任务：%s 个", finishedNum);
		if (this.gatherNum - 1 == finishedNum) {
			taskQueue.clear();
		}
	}

	/**
	 * 初始化 <li>加载爬行种子链接</li> <li>初始化任务队列</li>
	 */
	public void initialize(CrawlerController c) {
		this.controller = c;
		this.gatherNum = c.getCrawlScope().getGatherNum();
		loadSeeds();
		initTask();
		this.taskSize = taskQueue.getUnExecTaskNum();
	}

	/**
	 * 初始化任务队列
	 */
	private void initTask() {
		log.info("=========初始化任务队列=========");
		int planNum = 1;
		List<CrawlLinkURI> gatherList = new ArrayList<CrawlLinkURI>();
		while (!planIsEmpty()) {
			CrawlLinkURI url = getPlan();
			log.info(url);
			populateTaskQueue(url, planNum, gatherList);
			planNum++;
		}
		controller.addLinkList(gatherList);
		log.info("=========初始化任务队列结束,任务总数：=========" + taskQueue.getUnExecTaskNum() + "个");
	}

	/**
	 * 组装任务队列
	 * 
	 * @param map
	 */
	private void populateTaskQueue(CrawlLinkURI uri, int planNum, List<CrawlLinkURI> gatherList) {
		log.info("=========开始组装任务队列=========");
		try {
			String html = this.controller.getHandler().handleResponse(this.controller.getHostCache().getHttpHostUrl(uri));
			int taskNum = 1;
			// List<CrawlLinkURI> crawlURIList =
			// this.controller.getHtmlParserWrapper().getCrawlURIList(html,
			// this.controller.getCrawlScope().getSavePath(), uri);
			List<CrawlLinkURI> crawlURIList = this.controller.getHtmlParserWrapper().getLinkAreaUrlList(html, uri);
			/*
			 * if (this.controller.getCrawlScope().isAllowRepeat()) { //
			 * 从采集历史表中检查是否已经采集过 for (CrawlLinkURI crawlURI : crawlURIList) { //
			 * if
			 * (this.controller.getCrawlScope().getCrawlerPersistent().check(false
			 * ,crawlURI.getUrl())){ crawlURIList.remove(crawlURI); // } } }
			 */
			// 添加到临时表中
			for (CrawlLinkURI crawlURI : crawlURIList) {
				if (gatherList.size() >= gatherNum) {
					break;
				}
				Task task = new Task();
				task.setPlanNum(planNum);
				task.setTaskNum(taskNum);
				task.setCurrTaskTotalNum(crawlURIList.size());
				task.setCrawlURI(crawlURI);
				task.setController(this.controller);
				task.getContentBean().setTitle(crawlURI.getTitle());
				task.getContentBean().setAcquId(this.controller.getCrawlScope().getId());
				if (StringUtils.isNotEmpty(crawlURI.getResURI().getUrl())) {
					task.getContentBean().setTitleImg(crawlURI.getResURI().getUrl());
				}
				task.getContentBean().getResCrawlURIList().add(crawlURI.getResURI());
				addTask(task);
				taskNum++;
				gatherList.add(crawlURI);
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

	/**
	 * 加载种子链接
	 */
	private void loadSeeds() {
		log.info("=========开始加载种子链接=========");
		List<String> plans = this.controller.getCrawlScope().getSeeds();
		for (int i = 0; i < plans.size(); i++) {
			CrawlLinkURI url = this.controller.getUriHelper().populateCrawlURI(null, plans.get(i), "");
			url.setSeed(true);
			addPlan(url);
		}
		log.info("=========加载种子链接结束,共加载种子数：=========" + planUrlQueue.getUnExecTaskNum() + "个");
	}

	/**
	 * 判断当前边界控制器任务队列是否为空
	 */
	public boolean isEmpty() {
		return taskIsEmpty();
	}

	/**
	 * 取得下一个任务
	 */
	public Task next() {
		return getTask();
	}

	/**
	 * 连接URL对象入队列
	 * 
	 * @param url
	 *            url对象
	 */
	private void addPlan(CrawlLinkURI url) {
		planUrlQueue.addUnExecTask(url);
	}

	/**
	 * 连接URL对象出队列
	 * 
	 * @param urlQueue
	 *            当前线程的队列
	 * @return url对象
	 */
	private CrawlLinkURI getPlan() {
		return planUrlQueue.unExecTaskDeQueue();
	}

	/**
	 * 判断当前对象是否为空
	 * 
	 * @param urlQueue
	 *            当前线程的队列
	 * @return true/flase
	 */
	private boolean planIsEmpty() {
		return planUrlQueue.isEmpty();
	}

	/**
	 * 任务对象入队列
	 * 
	 * @param map
	 *            任务对象
	 */
	private void addTask(Task task) {
		taskQueue.addUnExecTask(task);
	}

	/**
	 * 任务对象出队列
	 * 
	 * @param urlQueue
	 *            当前线程的队列
	 * @return 任务对象
	 */
	private Task getTask() {
		return taskQueue.unExecTaskDeQueue();
	}

	/**
	 * 判断当前对象是否为空
	 * 
	 * @param urlQueue
	 *            当前线程的队列
	 * @return true/flase
	 */
	private boolean taskIsEmpty() {
		return taskQueue.isEmpty();
	}

	/**
	 * 取得任务总数
	 * 
	 * @return 任务总数
	 */
	public int getTaskSize() {
		return taskSize;
	}

	/**
	 * 取得已执行任务总数
	 * 
	 * @return 已执行任务总数
	 */
	public int getExecTaskNum() {
		return taskQueue.getExecTaskNum();
	}

	/**
	 * 取得未执行任务总数
	 * 
	 * @return 未执行任务总数
	 */
	public int getUnExecTaskNum() {
		return taskQueue.getUnExecTaskNum();
	}

	/**
	 * 销毁对象
	 */
	public void destory() {
		taskQueue.clear();
		planUrlQueue.clear();
		controller = null;
		taskSize = 0;
	}

}
