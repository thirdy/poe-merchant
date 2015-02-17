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
package net.poemerchant.entity;

/**
 * 
 *
 */
public enum Currency {
	// @formatter:off
	chrom("chromatic"), 
	alt("alteration"), 
	jewel("jwl"),
	chance(),
	chisel("chis"),
	fuse(""),
	alch("alc"),
	scour(),
	chaos("ch"),
	regret(),
	regal(),
	gcp("gemcutter", "prism"),
	divine(),
	exalted("exa"),
	mirror(),
	unknown();
	// @formatter:on

	private final String[] synonyms;

	private Currency(String... synonyms) {
		this.synonyms = synonyms;
	}

	public static Currency parse(String currencyStr) {
		Currency[] values = values();
		for (Currency currency : values) {
			if (currency.toString().equalsIgnoreCase(currencyStr)) {
				return currency;
			}
			for (String synonym : currency.synonyms) {
				if (synonym.equalsIgnoreCase(currencyStr)) {
					return currency;
				}
			}
		}
		return unknown;
	}
}
