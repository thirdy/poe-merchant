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

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

import java.util.logging.Logger;

import net.poemerchant.scraper.Key;

import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;

/**
 * 
 *
 */
public class ElasticSearch {
	private static Client client;
	private static Node node;
	
	private static final Logger logger = Logger.getLogger(ElasticSearch.class.getName());

	// public static Client
	public static void start() {
		// node = NodeBuilder.nodeBuilder().node();
		// client = node.client();
		client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						"localhost", 9300));
	}

	public static Client client() {
		return client;
	}

	public static void shutdown() {
		logger.info("Shutting down ES.");
		client.close();
	}

	public static IndexResponse indexRawContent(String json) {
		IndexResponse actionGet = index("rawcontent", "shop", json);
		return actionGet;
	}

	public static IndexResponse index(String index, String type, String json) {
		IndexResponse actionGet = client().prepareIndex(index, type)
				.setSource(json).execute().actionGet();
		return actionGet;
	}
	
	public static IndexResponse index(String index, String type,
			String id, String json) {
		IndexResponse actionGet = client().prepareIndex(index, type,id)
				.setSource(json).execute().actionGet();
		return actionGet;
	}

	public static String indexResponseToString(IndexResponse actionGet) {
		return actionGet.getIndex() + "/" + actionGet.getType() + "/"
				+ actionGet.getId() + " isCreated: " + actionGet.isCreated();
	}

	public static GetResponse get(String index, String type, String id) {
		GetResponse actionGet = client().prepareGet(index, type, id).execute()
				.actionGet();
		return actionGet;
	}
	
	public static SearchResponse query(String index, String type, String query) {
		SearchResponse response = client.prepareSearch(index)
		        .setTypes(type)
		        .setQuery(QueryBuilders.termQuery("_all", query))
		        .execute()
		        .actionGet();
		return response;
	}

	public static GetResponse getViaUri(String restUrl) {
		String[] params = restUrl.split("/");
		return get(params[0], params[1], params[2]);
	}

	public static DeleteByQueryResponse deleteItemsByShopId(String index, String type,
			String shopId) {
		DeleteByQueryResponse response = client.prepareDeleteByQuery(index)
				.setTypes(type)
		        .setQuery(termQuery(Key.shopId.name(), shopId))
		        .execute()
		        .actionGet();
		return response;
	}
}
