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

import org.apache.commons.lang.StringUtils;

/**
 * Parses forum buyout data: <br/>
 * ~b/o 1 exa
 *
 */
public class Buyout {
	
	
	private String raw;
	private double amount;
	private Currency currency;
	private BuyoutMode buyoutMode;
	
	public static final Buyout NONE = new Buyout();

	public Buyout(String raw) {
		this.raw = raw;
		String[] split = raw.split(" ");
		this.buyoutMode = BuyoutMode.parse(split[0]);
		String amountRaw = split[1];
		// for typos e.g. ~b/o 0,5 exa
		amountRaw = StringUtils.replaceChars(amountRaw, ',', '.');
		this.amount = Double.parseDouble(amountRaw);
		this.currency = Currency.parse(split[2]);
	}


	private Buyout() {
		amount = 0;
		currency = Currency.unknown;
		buyoutMode = BuyoutMode.unknown;
	}


	public Currency getCurrency() {
		return currency;
	}

	public double getAmount() {
		return amount;
	}


	public BuyoutMode getBuyoutMode() {
		return buyoutMode;
	}
	
}
