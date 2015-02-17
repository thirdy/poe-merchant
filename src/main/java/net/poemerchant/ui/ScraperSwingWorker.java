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
package net.poemerchant.ui;

import static java.lang.String.format;
import static net.poemerchant.search.ElasticSearch.index;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.elasticsearch.action.index.IndexResponse;

import com.fasterxml.jackson.core.JsonProcessingException;

import net.poemerchant.Configuration;
import net.poemerchant.ex.PoEMerchantException;
import net.poemerchant.scraper.ShopIndexScraper;
import net.poemerchant.scraper.ShopScraper;
import net.poemerchant.search.ElasticSearch;
import net.poemerchant.tools.RawAnalysisTool;
import net.poemerchant.util.JsonUtils;

/**
 * 
 *
 */
public class ScraperSwingWorker extends SwingWorker<Void, Void> {

	private final String shopSubForumUrl;
	private PrintStream printStream;
	private Configuration configuration;

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	public ScraperSwingWorker(String shopSubForumUrl, PrintStream printStream, Configuration configuration) {
		this.shopSubForumUrl = shopSubForumUrl;
		this.printStream = printStream;
		this.configuration = configuration;
	}

	@Override
	public Void doInBackground() throws Exception {
		// all logger message will also go to our printStream 
		logger.addHandler(new StreamHandler(printStream, new Formatter() {
			
			@Override
			public String format(LogRecord record) {
//				System.out.println("intercept: " + record.getMessage());
				return record.getMessage() + System.lineSeparator();
			}
		}) {
			@Override
			public synchronized void publish(LogRecord record) {
				super.publish(record);
	            flush();
			}
		});
		
		ShopIndexScraper scraper = new ShopIndexScraper(shopSubForumUrl);
		
		logger.info("Scrapping shops subform index: " + shopSubForumUrl);
		
		List<String> shopUrls = null;
		try {
			shopUrls = scraper.scrape();
		} catch (PoEMerchantException e) {
			logger.severe("Error: " + e.getMessage() + ". Try again.");
			return null;
		}
		
//		shopUrls = new ArrayList();
//		shopUrls.add(this.getClass().getResource("/1148541.htm").getFile());
		logger.info("Sucessfully scraped subform, number of shops: " + shopUrls.size());

		for (String url : shopUrls) {
			logger.info("Scrapping shop: " + url);
			try {
				scrapeAndSaveShop(configuration.getIndex(), configuration.getType(), url);
			} catch (Exception e) {
				logger.severe("error caught for " + url + ". err msg: "
						+ e.getMessage());
			}
		}
		
		logger.info(String.format("Sucessfully scraped %d shops.", shopUrls.size()));

		return null;
	}
	
	private void scrapeAndSaveShop(String index, String type, String url)
			throws PoEMerchantException, JsonProcessingException {
		ShopScraper shopScraper = new ShopScraper(url);
		List<Map<String, Object>> list = shopScraper.scrape();
		
		// we delete any existing items from this shop
		String shopId = shopScraper.shopId();
		ElasticSearch.deleteItemsByShopId(index, type, shopId);
		
		logger.info(format("saving %d items",list.size()));
		
		saveAll(list, index, type);
	}

	private void saveAll(List<Map<String, Object>> items, String index, String type)
			throws JsonProcessingException {
		for (Map<String, Object> map : items) {
			String json = JsonUtils.asString(map);
			IndexResponse indexResponse = index(index, type, json);
		}
	}
}
