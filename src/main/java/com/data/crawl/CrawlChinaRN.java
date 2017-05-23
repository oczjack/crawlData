package com.data.crawl;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.util.StringUtils;

/**
 * brief description
 * <p>Date : 2016年6月23日 上午9:32:58</p>
 * <p>Module : </p>
 * <p>Description: 采集中研网行业风险数据</p>
 * <p>Remark : </p>
 * @author 郑建国
 * @version 1.0
 * <p>----------------------------------------------------------------------</p>
 * <p> 修改历史</p>
 * <p> 序号 			日期 						修改人				 修改原因		</p>
 * <p>  1 	2016年6月23日 上午9:32:58	 		郑建国 							</p>
 * <p>----------------------------------------------------------------------</p>
 */
public class CrawlChinaRN {
	private static final int timeout = 60000;
	public static void main(String[] args){
		crawl();
	}
	/**
	 * 
	* 方法简要描述信息.
	* <p> 描述 : 采集行业风险方法</p>
	* <p> 备注 : 其他对方法的说明信息</p>
	* @param 
	* @return 
	* @throws 
	*
	 */
	public static void crawl(){
		Long start = System.currentTimeMillis();
		//行业预警
		String url = "http://www.chinairn.com/data/moref26fff1.html";
		
		Map<String,String> urlMap = crawlIndustryUrlInfo(url);
		
		for(String industry:urlMap.keySet()){
			
			String standardIndustry = getIndustry(industry);
			
			if(standardIndustry==null){
				standardIndustry = industry;
			}
			
			String nextUrl = urlMap.get(industry);
			
			do{
				//采集到一页数据
				crawlData(nextUrl,standardIndustry);
				nextUrl = getNextUrl(nextUrl);
			
			}while(!nextUrl.equals(""));
			
		}
		
		Long end = System.currentTimeMillis();
		
		System.out.println("采集完成。采集共耗时："+(end-start));
	
	}
	/**
	 * 
	* 方法简要描述信息.
	* <p> 描述 : 采集行业URL集合数据</p>
	* <p> 备注 : 其他对方法的说明信息</p>
	* @param url要采集的主页面地址
	* @return 
	* @throws 
	*
	 */
	public static Map<String,String> crawlIndustryUrlInfo(String url){
		
		Map<String,String> industryMap = new HashMap<String,String>();
		
		try {
			
			Document doc = Jsoup.connect(url).timeout(timeout).get();
			
			if(doc!=null){
				
				Elements industryElements = doc.select("div.hangyeshow").select("div.left").select("a");
				
				for(Element industryElement:industryElements){
					
					String industryUrl = industryElement.attr("abs:href");
					String industryValue = industryElement.text();
					
					industryMap.put(industryValue, industryUrl);
				}
			
			}else{
				
				System.out.println("连接失败！！！！");
			
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return industryMap;
	
	}
	/**
	 * 
	* 方法简要描述信息.
	* <p> 描述 : 方法的主要功能和使用场合</p>
	* <p> 备注 : 其他对方法的说明信息</p>
	* @param url 要采集的行业主页面地址
	* @return 
	* @throws 
	*
	 */
	public static void crawlData(String url,String industry){
		
		try {
			
			Document doc = Jsoup.connect(url).timeout(timeout).get();
			
			if(doc!=null){
				
				String id = "";//url的md5
				String title = "";//标题
				String source = "中研网";//来源
				String time = "";//发布时间
				String content = "";//新闻内容
				String getTime = "";//采集时间
				String keyWord = industry;//行业
				
				//采集数据
				Elements infoElements = doc.select("div.content_main>div.news_list.relative.left.clearfloat");
				
				for(Element infoElement :infoElements){
					
					Element titleTag = infoElement.select("p.h1>a").first();
					String detailUrl = titleTag.absUrl("href");
					
					id =StringUtils.md5(detailUrl);
					title = titleTag.text();
					time = infoElement.select("div.pubtime.left").text();
					content = getContent(detailUrl);
					System.out.println("采集到数据=====》》》id："+id+"|title:"+title
							+"|source:"+source+"|time:"+time+"|detailUrl:"+detailUrl
							+"|content:"+content+"|getTime:"+getTime+"|keyWord:"+keyWord);
				}
				
			}
		
		}catch(SocketTimeoutException se){
			
			crawlData(url,industry);
		} catch (IOException e) {
			
			e.printStackTrace();
		
		} 
		
		
	}
	/**
	 * 
	* 方法简要描述信息.
	* <p> 描述 : 采集内容</p>
	* <p> 备注 : 其他对方法的说明信息</p>
	* @param 
	* @return 
	* @throws 
	*
	 */
	public static String getContent(String url){
		String content = "";
		
		try {
			Document contentDoc = Jsoup.connect(url).timeout(timeout).get();
			
			if(contentDoc != null){
				
				content = contentDoc.select("div.contents").text();
				
				if(contentDoc.select("div.webpage").size()>0){
					
					String nextContentPageUrl = contentDoc.select("div.webpage>a").last().absUrl("href");
					
					if(nextContentPageUrl != null&&!nextContentPageUrl.equals("")){
						content += getContent(nextContentPageUrl);
					}
				
				}
			
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
		
		}
		
		return content;
	
	} 
	/**
	 * 
	* 方法简要描述信息.
	* <p> 描述 : 获取下一页的URL</p>
	* <p> 备注 : 其他对方法的说明信息</p>
	* @param 
	* @return 
	* @throws 
	*
	 */
	public static String getNextUrl(String url){
		String nextUrl = "";
		try {
			
			Document doc = Jsoup.connect(url).timeout(timeout).get();
			if(doc!=null){
				
				nextUrl = doc.select("div.pagelist.left.clearfloat>a:contains(下一页)").last().absUrl("href");
				String current = doc.select("div.pagelist.left.clearfloat>a.hover").first().absUrl("href");
				
				if(nextUrl.equals(current)){
					nextUrl = "";
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return nextUrl;
	}
	/**
	 * 
	* 方法简要描述信息.
	* <p> 描述 : 获取中研网的行业对应的标准行业名称</p>
	* <p> 备注 : 其他对方法的说明信息</p>
	* @param 
	* @return 
	* @throws 
	*
	 */
	public static String getIndustry(String industry){
		
		Map<String,String> industryMap = new HashMap<String,String>();
		
		industryMap.put("建筑房地产", "房产建筑");
		industryMap.put("农林牧渔业", "农林牧渔");
		industryMap.put("家用电器", "电子电器");
		industryMap.put("零售商贸", "商业百货");
		industryMap.put("医疗医药保健", "医疗医药保健");
		industryMap.put("IT与通讯", "软件服务与通讯");
		industryMap.put("轻工纺织服装", "轻工塑胶纺织服装");
		industryMap.put("食品饮料酒业", "食品饮料酒业");
		industryMap.put("酒店旅游餐饮", "旅游餐饮");
		industryMap.put("金融保险投资", "金融保险投资");
		industryMap.put("建材家具纸业", "建材家具");
		industryMap.put("能源矿产环保", "能源矿产煤炭");
		industryMap.put("石油化工", "石油化工");
		industryMap.put("家用日化", "石油化工");
		industryMap.put("车辆交通运输", "交运物流");
		industryMap.put("机械电子", "机械行业");
		industryMap.put("出版传媒包装", "造纸印刷包装");
		industryMap.put("其他行业", "商业百货");
		
		return industryMap.get(industry);
	
	} 
	
}
