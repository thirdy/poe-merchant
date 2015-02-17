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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

/**
 * 
 *
 */
public class RegexUtils {
	public static String extract(String string, String pattern, int group, boolean caseInSensitive)  {
		Pattern pat = null;
		if (caseInSensitive) {
			pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		} else {
			pat = Pattern.compile(pattern);
		}
			
		return extract(string, pat, group);
	}
	public static String extract(String string, Pattern pattern, int group)  {
		Matcher matcher = pattern.matcher(string);
		if (matcher.find()) {
			return matcher.group(group);
		}
		return Utils.EMPTY_STRING;
	}
}
