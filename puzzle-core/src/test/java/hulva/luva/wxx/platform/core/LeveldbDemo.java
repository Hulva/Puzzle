package hulva.luva.wxx.platform.core;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import org.iq80.leveldb.LocalStrage;
import org.iq80.leveldb.Row;

import hulva.luva.wxx.platform.util.StringUtil;

public class LeveldbDemo {
	
	public static void main(String[] args) {
		Set<Row> rows = new HashSet<>();
		int i = 1000000;
		while(i-->0){
			Row row = new Row("demo", StringUtil.next(10));
			TreeMap<String, String> treeMap = new TreeMap<String, String>();
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			treeMap.put(StringUtil.next(5), StringUtil.next(5));
			row.setData(treeMap);
			
			rows.add(row);
			if(rows.size() > 1000){
				LocalStrage.putAll("demo", rows);
				rows = new HashSet<>();
				System.out.println(i);
			}
		}
		
	}
}
