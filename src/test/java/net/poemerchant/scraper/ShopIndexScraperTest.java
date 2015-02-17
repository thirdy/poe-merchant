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

import static org.junit.Assert.*;

import java.util.List;

import net.poemerchant.tools.RawAnalysisTool;

import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * 
 *
 */
public class ShopIndexScraperTest {

	@Test
	public void test_scrape() throws Exception {
		String url = this.getClass().getResource("/forum/shopindex/torment_page1.htm").getFile();
		ShopIndexScraper scraper = new ShopIndexScraper(url);
		
		scraper.download();
		List<String> shopUrls = scraper.scrapeShopUrls();

		assertEquals("http://www.pathofexile.com/forum/view-thread/1196089", shopUrls.get(shopUrls.size() - 1));
		
	}
}
