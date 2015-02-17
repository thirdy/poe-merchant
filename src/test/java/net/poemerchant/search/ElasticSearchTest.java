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
package net.poemerchant.search;

import static net.poemerchant.search.ElasticSearch.index;
import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Test;

public class ElasticSearchTest {

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Test
	public void testESIndex() throws Exception {
//		String json = "{" + "\"user\":[0, {\"x\":1}]" + "}";
//		ElasticSearch.start();
//
//		IndexResponse indexResponse = index("a", "y", "{\"x\":" + json + "}");
//		logger.info(ElasticSearch.indexResponseToString(indexResponse));
//
//		ElasticSearch.shutdown();
	}

	@Test
	public void testGet() throws Exception {

		ElasticSearch.start();

		GetResponse getResponse = ElasticSearch.get("a", "aaax",
				"AUtfGCt4CpNHi5NtDgGs");
		logger.info(getResponse.getSourceAsString());
		ElasticSearch.shutdown();
	}
	
	@Test
	public void testGetWithParse() throws Exception {
		
		ElasticSearch.start();
		
		GetResponse getResponse = ElasticSearch.getViaUri("rawanalysis/items/AUthcRl6qxvRNuDD37l-");
		logger.info(getResponse.getSourceAsString());
		ElasticSearch.shutdown();
	}
	
	@Test
	public void test_deleteAllFromIndex() throws Exception {
		ElasticSearch.start();
		ElasticSearch.deleteItemsByShopId("rawanalysis", "items9", "1196439");
		ElasticSearch.shutdown();
	}

	//

	//
	// // GetResponse getResponse = get(indexResponse.getIndex(),
	// indexResponse.getType(), indexResponse.getId());
	// // logger.info(getResponse.getSourceAsString());
	//
	// SearchResponse response = ElasticSearch.client().prepareSearch("a")
	// .setQuery(QueryBuilders.termQuery("verified", "false"))
	// .execute().actionGet();
	// logger.info(response.toString());
	//

}
