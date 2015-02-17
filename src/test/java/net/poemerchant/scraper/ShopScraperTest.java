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

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.poemerchant.entity.Buyout;
import net.poemerchant.entity.BuyoutMode;
import net.poemerchant.entity.Currency;
import net.poemerchant.ex.PoEMerchantException;
import net.poemerchant.search.ElasticSearch;
import net.poemerchant.tools.RawAnalysisTool;
import net.poemerchant.util.JsonUtils;
import static net.poemerchant.search.ElasticSearch.*;
import static org.junit.Assert.*;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.jsoup.nodes.Document;
import org.junit.Ignore;
import org.junit.Test;

public class ShopScraperTest {

	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	@Test
	public void test_scrape() throws Exception {
		String url = RawAnalysisTool.class.getResource("/forum/shop/1148541.htm").getFile();
		ShopScraper shopScraper = new ShopScraper(url);

		List<Map<String, Object>> items = shopScraper.scrape();
		
		assertEquals("Zonx1", items.get(1).get(Key.accountName.name()));
		assertEquals("Zonxer", items.get(1).get(Key.ign.name()));
		assertEquals("1148541", items.get(1).get(Key.shopId.name()));
		Buyout buyout = (Buyout) items.get(1).get(Key.buyout.name());
		assertEquals(BuyoutMode.bo, buyout.getBuyoutMode());
		assertEquals(Currency.exalted, buyout.getCurrency());
	}

	@Test
	public void test_scrapeItemJSData() throws Exception {
		String url = RawAnalysisTool.class.getResource("/forum/shop/1148541.htm").getFile();
		ShopScraper shopScraper = new ShopScraper(url);
		
		shopScraper.download();
		List<Map<String, Object>> map = shopScraper.scrapeItemJSData();
		
		int expected = 117;
		assertEquals(String.format(
				"Number of items for testHTML %s should be %d.", url,
				expected), expected, map.size());
	}
	
	@Test
	public void test_scrapeAccountName() throws Exception {
		String expected = "Zonx1";
		String url = RawAnalysisTool.class.getResource("/forum/shop/1148541.htm").getFile();
		ShopScraper shopScraper = new ShopScraper(url);
		
		shopScraper.download();
		assertEquals(expected, shopScraper.scrapeAccountName());
	}
	
	@Test
	public void test_scrapeIGN() throws Exception {
		String expected = "Zonxer";
		String url = RawAnalysisTool.class.getResource("/forum/shop/1148541.htm").getFile();
		ShopScraper shopScraper = new ShopScraper(url);
		
		shopScraper.download();
		assertEquals(expected, shopScraper.scrapeIGN());
	}
	
	@Test
	public void test_scrapeItemBuyouts() throws Exception {
		String url = RawAnalysisTool.class.getResource("/forum/shop/1148541.htm").getFile();
		ShopScraper shopScraper = new ShopScraper(url);
		int expectedSize = 117;
		
		shopScraper.download();
		List<Buyout> buyouts = shopScraper.scrapeItemBuyouts(expectedSize);
		
		assertEquals(expectedSize, buyouts.size());
		assertEquals(Currency.exalted, buyouts.get(0).getCurrency());
		assertEquals(Currency.unknown, buyouts.get(40).getCurrency());
	}
	
	@Test
	@Ignore
	public void test_ActualDownload() throws Exception {
		String url = "http://www.pathofexile.com/forum/view-thread/1186526";
		ElasticSearch.start();
		ShopScraper shopScraper = new ShopScraper(url);
		shopScraper.download();
		ElasticSearch.shutdown();
	}
	
	@Test
	@Ignore
	public void test_ScrapeFromIndex() throws Exception {
		ElasticSearch.start();
		GetResponse response = ElasticSearch.getViaUri("html/shop/1186526");
		String raw = (String) response.getSource().get("raw");
		
		ShopScraper shopScraper = new ShopScraper(null, raw);
		List<Map<String, Object>> items = shopScraper.scrape();
		Map<String, Object> item = items.get(0);
		
		assertEquals("JoGivesUpHelloTorment", item.get(Key.ign));
		assertEquals("Soul Clasp", item.get(Key.name));
		
		ElasticSearch.shutdown();
	}

	// displayMode should not exist
	private void assertDisplayModeShouldNotExist(Map<String, Object> map) {
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			if (Map.class.isAssignableFrom(entry.getValue().getClass())) {
				assertDisplayModeShouldNotExist((Map<String, Object>)entry.getValue());
			} else if (entry.getKey().equalsIgnoreCase("displayMode")) {
				fail("displayMode detected at: " + map.toString());
			}
		}

	}

}
