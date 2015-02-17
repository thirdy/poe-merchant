/**
 * Copyright (C) 2015  Vicente de Rivera III
 *
 * This file is part of poe-merchant.
 * 
 *    poe-merchant is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 * 
 *    poe-merchant is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 * 
 *    You should have received a copy of the GNU General Public License
 *    along with poe-merchant.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.poemerchant.scraper;

import static net.poemerchant.util.Utils.asUrl;
import static org.apache.commons.lang.StringUtils.indexOf;
import static org.apache.commons.lang.StringUtils.left;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;















import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.poemerchant.entity.Buyout;
import net.poemerchant.entity.BuyoutMode;
import net.poemerchant.ex.PoEMerchantException;
import net.poemerchant.ex.PoEMerchantRuntimeException;
import net.poemerchant.search.ElasticSearch;
import net.poemerchant.util.JsonUtils;
import net.poemerchant.util.NashornUtils;
import net.poemerchant.util.RegexUtils;
import net.poemerchant.util.Utils;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.tartarus.snowball.Among;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.io.Files;

/**
 * Web scrapes 1 forum shop at a time.
 */
public class ShopScraper {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	Document doc;
	private String url;
	
	List<Map<String, Object>> items = Collections.emptyList();
	private String accountName;
	private String ign;
	private List<Buyout> buyouts;
	private String shopId;

	private String html;

	// TODO, refactor, move downloading logic to another class
	public ShopScraper(String url) {
		this.url = url;
	}
	
	public ShopScraper(String url, String html) {
		this.url = url;
		this.html = html;
	}

	public List<Map<String, Object>> scrape() throws PoEMerchantException {
		try {
			download();
			if (doc == null) {
				// download failed, lets skip.
				return Collections.emptyList();
			}
			scrapeItemJSData();
			scrapeItemBuyouts(items.size());
			scrapeAccountName();
			scrapeIGN();
			scrapeShopId();
			mergeData();
		} catch (Exception e) {
			throw new PoEMerchantException(e);
		}
		return items;
	}
	
	private String scrapeShopId() {
		this.shopId = StringUtils.substringAfterLast(url, "/");
		this.shopId = StringUtils.substringBefore(shopId, ".");
		return shopId;
	}

	private void mergeData() {
		for (int i = 0; i < items.size(); i++) {
			Map<String, Object> item = items.get(i);
			Object buyout = buyouts.get(i);
			item.put(Key.buyout.name(), buyout);
			item.put(Key.ign.name(), ign);
			item.put(Key.accountName.name(), accountName);
			item.put(Key.shopId.name(), shopId);
		}
	}

	public List<Map<String, Object>> scrapeItemJSData() throws PoEMerchantException {
		String raw = scrapeItemJSArray(doc);
//		logger.info(raw);
		
		String jsFile = this.getClass().getResource("/js/item-array-formatter.js").getFile();
		String js = loadJSParser(jsFile);
		
		js = "var raw = " + raw + ";" + js ;
		ScriptObjectMirror som = NashornUtils.evalFunc(js, "func1");
		
		items = parseItemJSArray(som); 
		
		return items;
	}


	private List<Map<String, Object>> parseItemJSArray(ScriptObjectMirror som) {
		List<Map<String, Object>> items = new ArrayList<Map<String, Object>>(som.entrySet().size());
		
		Object[] itemsArr = NashornUtils.somToArray(som);
		for (int i = 0; i < itemsArr.length; i++) {
			ScriptObjectMirror itemSom = (ScriptObjectMirror) itemsArr[i];
			Map<String, Object> itemMap = NashornUtils.somToMap(itemSom, null);
			items.add(itemMap);
		}
		
		return items;
	}


	private String loadJSParser(String jsFile) {
		String js = null;
		try {
			js = Files.toString(new File(jsFile), Charsets.UTF_8);
		} catch (IOException e) {
			throw new PoEMerchantRuntimeException(e);
		}
		return js;
	}


	public Document download() throws PoEMerchantException {
		try {
			if (html != null) {
				doc = Jsoup.parse(html);
			} else if (!url.startsWith("http")) {
				File file = new File(url);
				doc = Jsoup.parse(file, "UTF-8");
			} else {
//				URL _url = asUrl(url);
//				doc = Jsoup.parse(_url, 10000);
				Connection connection = Jsoup.connect(url);
				connection.timeout(10000);
				Response response = connection.execute();
				String body = response.body();
				Map<String, String> map = new HashMap<String, String>();
				map.put("url", url);
				map.put("raw", body);
				String json = JsonUtils.asString(map);
				ElasticSearch.index("html", "shop", scrapeShopId(), json);
				doc = response.parse();
			}
		} catch (IOException e) {
//			e.printStackTrace();
			// TODO, create a retry feature
//			throw new PoEMerchantException(e);
			logger.log(Level.SEVERE, String.format("Failed to download %s. Skipping this shop for now. Exception message: %s.", url, e.getMessage()), e);
		}
		return doc;
	}

	private String scrapeItemJSArray(Document doc) {
		Element scriptDataElem = doc.select("script").last(); // a with href
		String raw = scriptDataElem.data();
		raw = StringUtils.substringBetween(raw, "new R(", ")).run();");
		return raw;
	}

	public String scrapeAccountName() {
		Element profileLink = doc.select(".profile-link").select("a[href^=/account/view-profile]").first();
		// TODO, take the challenge data also
		// TODO, take the twitch data
		accountName = profileLink.text();
		return accountName;
	}
	
	public String scrapeShopName() {
		// TODO, extractShopName
		return null;
	}

	public String scrapeIGN() {
		Element forumTable = doc.select("table.forumTable.forumPostListTable").select("div.content").first();
		String bigDivContentString = forumTable.toString();
		ign = RegexUtils.extract(bigDivContentString, "My\\sIGN\\sis\\s\\b(.*)\\b", 1, true);
		return ign;
	}
	
	public List<Buyout> scrapeItemBuyouts(int noOfItems) {
		buyouts = new ArrayList<Buyout>(noOfItems);
		for (int i = 0; i < noOfItems; i++) {
			Buyout buyout = Buyout.NONE;
			Node itemElem = doc.select("#item-fragment-" + i).first();
			Element itemElemNext = doc.select("#item-fragment-" + (i + 1)).first();
			if (itemElem != null) {
				while (!itemElem.equals(itemElemNext)) {
					itemElem = itemElem.nextSibling();
					
					if (itemElem == null) {
						// case where there is no b/o set and we've reached the end of a spoiler
						break;
					}
					
					if (Element.class.isAssignableFrom(itemElem.getClass())) 
						continue;
					String boRaw = StringUtils.trim(itemElem.toString());
					String[] split = StringUtils.split(boRaw);
					if (split.length == 3) {
						BuyoutMode buyoutMode = BuyoutMode.parse(split[0]);
						if (buyoutMode != BuyoutMode.unknown) {
							buyout = new Buyout(boRaw);
							break;
						}
					}
				}
			} else {
				logger.severe("Actual item in the OP was not found. Buyout will be defaulted to NONE. Item index is " + i);
			}
			buyouts.add(buyout);
		}
		
		return buyouts;
	}

	public String shopId() {
		return shopId;
	}


}
