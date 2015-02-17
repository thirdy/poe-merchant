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
package net.poemerchant.tools;

import static java.lang.String.format;
import static net.poemerchant.search.ElasticSearch.index;
import static org.apache.commons.lang.StringUtils.indexOf;
import static org.apache.commons.lang.StringUtils.substring;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.poemerchant.ex.PoEMerchantException;
import net.poemerchant.scraper.ShopIndexScraper;
import net.poemerchant.scraper.ShopScraper;
import net.poemerchant.search.ElasticSearch;
import net.poemerchant.util.JsonUtils;
import net.poemerchant.util.NashornUtils;

import org.elasticsearch.action.index.IndexResponse;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 *
 */
public class RawAnalysisTool {
	private final static Logger logger = Logger.getLogger(RawAnalysisTool.class
			.getName());

	public static void main(String[] args) throws Exception {
		ElasticSearch.start();

		String index = "rawanalysis";
		String type = "items9";

		List<String> shopUrls = shopUrls();
		
		for (String url : shopUrls) {
			logger.info("Scrapping shop: " + url);
			try {
				scrapeAndSaveShop(index, type, url);
			} catch (Exception e) {
				logger.info("error caught for " + url + ". err msg: " + e.getMessage());
			}
		}
		

		ElasticSearch.shutdown();
	}

	private static List<String> shopUrls() throws PoEMerchantException {
		String url = RawAnalysisTool.class.getResource("/forum/shopindex/torment_page1.htm").getFile();
		ShopIndexScraper scraper = new ShopIndexScraper(url);
		List<String> shopUrls = scraper.scrape();
		return shopUrls;
	}

	private static void scrapeAndSaveShop(String index, String type, String url)
			throws PoEMerchantException, JsonProcessingException {
		ShopScraper shopScraper = new ShopScraper(url);
		List<Map<String, Object>> list = shopScraper.scrape();
		
		// we delete any existing items from this shop
		String shopId = shopScraper.shopId();
		ElasticSearch.deleteItemsByShopId(index, type, shopId);
		
		logger.info(format("saving %d items",list.size()));
		
		RawAnalysisTool cr = new RawAnalysisTool();
		cr.saveAll(list, index, type);
	}

	private void saveAll(List<Map<String, Object>> items, String index, String type)
			throws JsonProcessingException {
		for (Map<String, Object> map : items) {
			String json = JsonUtils.asString(map);
			IndexResponse indexResponse = index(index, type, json);
//			logger.info(ElasticSearch.indexResponseToString(indexResponse));
		}
	}

}
