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

import static net.poemerchant.scraper.Key.accountName;
import static net.poemerchant.scraper.Key.additionalProperties;
import static net.poemerchant.scraper.Key.buyout;
import static net.poemerchant.scraper.Key.corrupted;
import static net.poemerchant.scraper.Key.craftedMods;
import static net.poemerchant.scraper.Key.descrText;
import static net.poemerchant.scraper.Key.duplicated;
import static net.poemerchant.scraper.Key.explicitMods;
import static net.poemerchant.scraper.Key.flavourText;
import static net.poemerchant.scraper.Key.identified;
import static net.poemerchant.scraper.Key.ign;
import static net.poemerchant.scraper.Key.implicitMods;
import static net.poemerchant.scraper.Key.name;
import static net.poemerchant.scraper.Key.query;
import static net.poemerchant.scraper.Key.secDescrText;
import static net.poemerchant.scraper.Key.shopId;
import static net.poemerchant.scraper.Key.typeLine;

import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import net.poemerchant.scraper.Key;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.BevelBorder;

/**
 * 
 *
 */
public class ItemViewPanel extends JPanel {
	
	public static final List<Key> visibleFields = Arrays.asList(
			accountName,
			additionalProperties,
			buyout,
			corrupted,
//			cosmeticMods,
			craftedMods,
			descrText,
			duplicated,
			explicitMods,
			flavourText,
//			frameType,
//			h,
//			icon,
			identified,
			ign,
			implicitMods,
//			indexInShop,
//			league,
//			lockedToCharacter,
			name,
//			nextLevelRequirements,
			query,
			secDescrText,
			shopId,
//			support,
//			verified,
//			w
			typeLine
			);
	
	public ItemViewPanel(Map<String, Object> item) {
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
//		Set<Entry<String, Object>> entrySet = searchHits.entrySet();
		addNewItem(item);
//		JLabel lblTitlehere = new JLabel("titleHere");
//		add(lblTitlehere);
//		
//		String name = item.get(Key.NAME).toString();
//		lblTitlehere.setText(name);
		
		
//		JLabel label = new JLabel("New label");
//		add(label);
	}

	private void addNewItem(Map<String, Object> item) {
		Set<Entry<String, Object>> entrySet = item.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			if (visibleFields.contains(Key.valueOf(entry.getKey()))) {
				String value = entry.getValue().toString();
				JLabel lblTitlehere = new JLabel(entry.getKey() + ": " + value);
				lblTitlehere.setHorizontalAlignment(SwingConstants.CENTER);
				lblTitlehere.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
				lblTitlehere.setFont(new Font("Verdana", Font.PLAIN, 9));
				add(lblTitlehere);
			}
		}
		
	}
	
}
