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
package net.poemerchant.util;

import java.awt.Component;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.poemerchant.ex.PoEMerchantRuntimeException;
import net.poemerchant.ui.ScraperSwingWorker;

/**
 *
 */
public class Utils {
	public static final String EMPTY_STRING = "";

	public static URL asUrl(String url) {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new PoEMerchantRuntimeException(e);
		}
	}

	public static Component getActiveFrame(Component c) {
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(c);
		return topFrame;
	}
}
