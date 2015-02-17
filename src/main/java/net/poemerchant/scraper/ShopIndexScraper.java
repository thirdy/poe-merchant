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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.poemerchant.ex.PoEMerchantException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scrape the index of shop. e.g.
 * http://www.pathofexile.com/forum/view-forum/561/page/1
 */
public class ShopIndexScraper {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	private Document doc;
	private String url;
	private List<String> shopUrls;
	
	public ShopIndexScraper(String url) {
		this.url = url;
	}
	
	public List<String> scrape() throws PoEMerchantException {
		download();
		scrapeShopUrls();
		return shopUrls;
	}


	public List<String> scrapeShopUrls() {
		Elements shopLinks = doc.select("#view_forum_table").select("div.status").select("a");
		shopUrls = new ArrayList<String>(shopLinks.size());
		for (Element element : shopLinks) {
			String url = element.attr("href");
			url = "http://www.pathofexile.com" + url;
			shopUrls.add(url);
		}
		return shopUrls;
	}

	public Document download() throws PoEMerchantException {
		try {
			if (!url.startsWith("http")) {
				File file = new File(url);
				doc = Jsoup.parse(file, "UTF-8");
			} else {
				URL _url = asUrl(url);
				doc = Jsoup.parse(_url, 10000);
			}
		} catch (IOException e) {
			throw new PoEMerchantException(e);
		}
		return doc;
	}
}
