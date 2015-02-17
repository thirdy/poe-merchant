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
 * ~b/o, ~c/o, ~price
 *
 */
public enum BuyoutMode {
	
	bo("~b/o"),
	co("~c/o"),
	price("~price"),
	unknown("~unknown");
	
	private final String stringForm;

	private BuyoutMode(String stringForm) {
		this.stringForm = stringForm;
	}

	public static BuyoutMode parse(String stringForm) {
		BuyoutMode[] values = values();
		for (BuyoutMode buyoutMode : values) {
			if (buyoutMode.stringForm.equalsIgnoreCase(stringForm)) {
				return buyoutMode;
			}
		}
		return unknown;
	}
}
