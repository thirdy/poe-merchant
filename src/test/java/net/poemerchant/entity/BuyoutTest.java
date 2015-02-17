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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 *
 */
public class BuyoutTest {
	
	/**
	 * See http://stackoverflow.com/questions/5686755/meaning-of-epsilon-argument-of-assertequals-for-double-values
	 */
	private static final double DELTA = 1e-15;
	
	@Test
	public void test_1ex() throws Exception {
		Buyout bo = new Buyout("~b/o 1 exa");
		assertEquals(Currency.exalted, bo.getCurrency());
		assertEquals(1, bo.getAmount(), DELTA);
	}
	
	@Test
	public void test_10ex() throws Exception {
		Buyout bo = new Buyout("~b/o 10 exa");
		assertEquals(Currency.exalted, bo.getCurrency());
		assertEquals(10, bo.getAmount(), DELTA);
	}
	
	@Test
	public void test_10ch() throws Exception {
		Buyout bo = new Buyout("~b/o 10 ch");
		assertEquals(Currency.chaos, bo.getCurrency());
		assertEquals(10, bo.getAmount(), DELTA);
	}
	
	@Test
	public void test_190chaos() throws Exception {
		Buyout bo = new Buyout("~b/o 190 chaos");
		assertEquals(Currency.chaos, bo.getCurrency());
		assertEquals(190, bo.getAmount(), DELTA);
	}
	
	@Test
	public void test_1andHalf_Exa_Typo() throws Exception {
		Buyout bo = new Buyout("~b/o 1,5 exa");
		assertEquals(Currency.exalted, bo.getCurrency());
		assertEquals(1.5, bo.getAmount(), DELTA);
	}
}
