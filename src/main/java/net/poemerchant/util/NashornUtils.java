package net.poemerchant.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import net.poemerchant.ex.PoEMerchantRuntimeException;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class NashornUtils {
	public static <T> T evalFunc(String js, String functionIdentifier,
			String... params) {

		ScriptEngine engine = new ScriptEngineManager()
				.getEngineByName("nashorn");
		try {
			Object eval = engine.eval(js);

			Invocable invocable = (Invocable) engine;

			T result = (T) invocable.invokeFunction(functionIdentifier, params);

			return result;
		} catch (ScriptException e) {
			throw new PoEMerchantRuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new PoEMerchantRuntimeException(e);
		}

	}

	public static <T> T evalJSArray(String raw) {
		String js = "var func1 = function() { " + "return " + raw + ";" + "}";
		return evalFunc(js, "func1");
	}

	public static Map<String, Object> evalJSArrayToMap(String raw,
			Predicate<Object> isNeeded) {
		ScriptObjectMirror evalJSArray = evalJSArray(raw);
		return somToMap(evalJSArray, isNeeded);
	}

	public static Map<String, Object> evalJSArrayToMap(String raw) {
		return evalJSArrayToMap(raw, Predicates.alwaysTrue());
	}

	public static Map<String, Object> somToMap(ScriptObjectMirror scriptObjectMirror, Predicate<Object> filter) {
		Map<String, Object> map = new HashMap<String, Object>();

		Set<Entry<String, Object>> entrySet = scriptObjectMirror.entrySet();

		for (Iterator<Entry<String, Object>> iter = entrySet.iterator(); iter.hasNext();) {
			Entry<String, Object> entry = iter.next();
			if (filter != null && filter.apply(entry.getKey())) {
				continue;
			}
			Object obj = entry.getValue();
			if (obj.getClass().equals(ScriptObjectMirror.class)) {
				ScriptObjectMirror som = (ScriptObjectMirror) obj;
				if (som.getClassName().equals("Array")) {
					obj = somToArray(som);
				}
			}
			map.put(entry.getKey(), obj);
		}
		return map;
	}

	public static Object[] somToArray(ScriptObjectMirror som) {
		Set<Entry<String, Object>> arrayEntries = som.entrySet();
		Object[] obj = new Object[arrayEntries.size()];
		int idx = 0;
		for (Entry<String, Object> entry : arrayEntries) {
			obj[idx] = entry.getValue();
			idx++;
		}
		return obj;
	}

}
