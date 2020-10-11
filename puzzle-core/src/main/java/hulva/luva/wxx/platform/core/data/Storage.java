package hulva.luva.wxx.platform.core.data;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.iq80.leveldb.ChangeEnum;
import org.iq80.leveldb.LocalStrage;
import org.iq80.leveldb.Row;

public class Storage implements Closeable{
	
	String keyspaces;
	
	public Storage(){}
	public Storage(String key) {
		this.keyspaces = key;
	}
	
	public String getKeyspaces() {
        return keyspaces;
    }

    public void forEach(Consumer<Row> consumer) {
		LocalStrage.forEach(keyspaces, consumer);
	}
    
    public void forEach(int batch, Consumer<Set<Row>> consumer) {
		LocalStrage.forEach(keyspaces, batch, consumer);
	}
	
	public long count() {
		return LocalStrage.count(keyspaces);
	}
	
	public boolean isEmpty() {
	    return LocalStrage.isEmpty(keyspaces);
	}
	
	public Row get(String key) {
		return LocalStrage.get(keyspaces, key);
	}
	
	public Map<String, Row> getAll(Set<String> keys){
		return LocalStrage.getAll(keyspaces, keys);
	}
	
	public List<String> getAllKeyspacesName() {
		return LocalStrage.getAllKeyspacesName();
	}
	
	public void put(Row row){
		LocalStrage.put(keyspaces, row);
	}
	
	public void put(String key, TreeMap<String, String> data){
		LocalStrage.put(keyspaces, new Row(keyspaces, key, data));
	}
	
	public synchronized void putAll(Set<Row> rows) {
		LocalStrage.putAll(keyspaces, rows);
	}
	
	public void remove(String key) {
		LocalStrage.remove(keyspaces, key);
	}
	
	public void removeAll(Set<String> rows) {
		LocalStrage.removeAll(keyspaces, rows);
	}
	
	public ChangeEnum set(String key, TreeMap<String, String> data){
		return LocalStrage.putAndGet(keyspaces, new Row(keyspaces, key, data));
	}

	public void setVersion(long version) {
		LocalStrage.updateVersion(keyspaces, version);
	}
	
	public long version(){
		return LocalStrage.getVersion(keyspaces);
	}
	
	public void clear(){
		LocalStrage.clear(keyspaces);
	}
	
	@Override
	public void close() {
		LocalStrage.close(keyspaces);
	}
}
