package hulva.luva.wxx.platform.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HiveUtil
 * @author fl76
 */
public class HiveUtil {
	private static final Logger logger = LoggerFactory.getLogger(HiveUtil.class);
	private static final String DISABLE_CONCURRENCY = "set hive.support.concurrency=false";
	private static final String DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
	private static Driver HIVE_JDBC_DRIVER = null;
	private String url;
	private String userName;
	private String password;
	private Properties connectInfo;

	static {
		try {
			HIVE_JDBC_DRIVER = (Driver) Class.forName(DRIVER_NAME, true, Thread.currentThread().getContextClassLoader()).newInstance();
		} catch (Exception e) {
			logger.error("Load HIVE_JDBC_DRIVER error", e);
		}
	}

	public HiveUtil(String url,String userName,String password){
		this.url = url;
		this.userName = userName;
		this.password = password;
		this.connectInfo = new Properties();
        if (this.userName != null) {
        	connectInfo.put("user", this.userName);
        }
        if (this.password != null) {
        	connectInfo.put("password", this.password);
        }
	}

	public Connection getConnection() throws SQLException{
		if(HIVE_JDBC_DRIVER == null) {
			throw new SQLException("Hive jdbc driver not loaded!");
		}
		return HIVE_JDBC_DRIVER.connect(this.url, this.connectInfo);
	}

	public boolean execute(String hiveScript) throws SQLException {
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				return execute(stmt, hiveScript);
			}
		}
	}
	
	public void execute(List<String> hiveScripts) throws SQLException {
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				if(hiveScripts != null && hiveScripts.size() > 0) {
					for(String hiveScript : hiveScripts) {
						if(hiveScript != null && hiveScript.trim().length() != 0) {
							execute(stmt, hiveScript);
						}
					}
				}
			}
		}
	}
	
	public void executeQuery(String hiveScript, Consumer<ResultSet> consumer) throws SQLException {
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				consumer.accept(executeQuery(stmt, hiveScript));
			}
		}
	}

	public void executeQuery(List<String> hiveScripts, String selectScript, Consumer<ResultSet> consumer) throws SQLException {
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				if (hiveScripts != null && hiveScripts.size() > 0) {
					for (String hiveScript : hiveScripts) {
						if (hiveScript != null && hiveScript.trim().length() != 0) {
							execute(stmt, hiveScript);
						}
					}
					if (selectScript != null && selectScript.trim().length() != 0) {
						consumer.accept(executeQuery(stmt, selectScript));
					}
				}
			}
		}
	}

	public boolean tableExists(String tableName) throws SQLException {
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				try(ResultSet rs = executeQuery(stmt, "show tables '" + tableName + "'")) {
					if(rs.next()) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
	}

	public void dropTable(String tableName) throws SQLException {
		String dropHql = "drop table if exists " + tableName;
		execute(dropHql);
	}

	public void dropPartition(String tableName, String partitionDef) throws SQLException {
		String dropPartition = "alter table "+ tableName + " drop if exists partition(" + partitionDef + ")";
		execute(dropPartition);
	}

	public void dropPartitions(String tableName, Set<String> partitionDefs) throws SQLException {
		List<String> dropPartitions = new ArrayList<String>();
		for(String partitionDef : partitionDefs) {
			dropPartitions.add("alter table "+ tableName + " drop if exists partition(" + partitionDef + ")");
		}
		execute(dropPartitions);
	}

	public void addPartition(String tableName, String partitionDef, String location) throws SQLException {
		String addPartition = "alter table " + tableName + " add partition (" + partitionDef + ")\n"
				+ "location '" + location + "'";
		execute(addPartition);
	}

	public List<String> partitions(String tableName) throws SQLException {
		List<String> partitions = new ArrayList<String>();
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				try(ResultSet rs = executeQuery(stmt, "show partitions " + tableName)) {
					String partition = null;
					while(rs.next()) {
						partition = rs.getString(1);
						partition = partition.replace("=", "='") + "'";
						partitions.add(partition);
					}
				}
			}
		}
		return partitions;
	}

	public Map<String, String> partitionToLocation(String tableName) throws SQLException {
		Map<String, String> partitionToLocation = new LinkedHashMap<String, String>();
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				try(ResultSet rs = executeQuery(stmt, "show partitions " + tableName)) {
					String partition = null;
					String location = null;
					while(rs.next()) {
						partition = rs.getString(1);
						partition = partition.replace("=", "='") + "'";
						location = getPartitionLocation(conn, tableName, partition);
						partitionToLocation.put(partition, location);
					}
				}
			}
		}
		return partitionToLocation;
	}

	public String getPartitionLocation(String tableName, String partitionDef) throws SQLException {
		try(Connection conn = getConnection()) {
			return getPartitionLocation(conn, tableName, partitionDef);
		}
	}

	private String getPartitionLocation(Connection conn, String tableName, String partitionDef) throws SQLException {
		String descHql = "describe formatted " + tableName + " partition (" + partitionDef + ")";
		try(Statement stmt = conn.createStatement()) {
			try(ResultSet rs = executeQuery(stmt, descHql)) {
				while (rs.next()) {
					if("Location:".equalsIgnoreCase(rs.getString(1).trim())) {
						return rs.getString(2);
					}
				}
			}
		}
		return null;
	}

	//Get column definition EXCLUDE partition columns:
	//+--------------------------+-----------------------+-----------------------+--+
	//|         col_name         |       data_type       |        comment        |
	//+--------------------------+-----------------------+-----------------------+--+
	//| col1                     | int                   |                       |
	//| col2                     | string                |                       |
	//| col3                     | string                |                       |
	//| col4                     | boolean               |                       |
	//| dt                       | string                |                       |
	//|                          | NULL                  | NULL                  |
	//| # Partition Information  | NULL                  | NULL                  |
	//| # col_name               | data_type             | comment               |
	//|                          | NULL                  | NULL                  |
	//| dt                       | string                |                       |
	//+--------------------------+-----------------------+-----------------------+--+
	public String tableColumnDef(String tableName) throws SQLException {
		List<String> columns = new ArrayList<String>();
		Map<String, String> columnType = new HashMap<String, String>();
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				try(ResultSet rs = executeQuery(stmt, "describe " + tableName)) {
					String column = null;
					String type = null;
					while(rs.next()) {
						column = rs.getString(1);
						type = rs.getString(2);
						columns.add(column);
						columnType.put(column, type);
					}
				}
			}
		}
		if(!columns.isEmpty()) {
			int firstEmpty = columns.indexOf("");
			if(firstEmpty != -1) {
				List<String> realColumns = columns.subList(0, firstEmpty);
				List<String> partitionColumns = columns.subList(firstEmpty + 1, columns.size());
				int partitionColumnNumber = (partitionColumns.size() - 1) - partitionColumns.indexOf("");
				columns = realColumns.subList(0, realColumns.size() - partitionColumnNumber);
			}
			StringBuffer buffer = new StringBuffer();
			for(String c : columns) {
				buffer.append(",").append(c).append(" ").append(columnType.get(c));
			}
			return buffer.length() > 0 ? buffer.substring(1) : "";
		}
		return "";
	}

	//Get real column name EXCLUDE partition columns:
	//+--------------------------+-----------------------+-----------------------+--+
	//|         col_name         |       data_type       |        comment        |
	//+--------------------------+-----------------------+-----------------------+--+
	//| col1                     | int                   |                       |
	//| col2                     | string                |                       |
	//| col3                     | string                |                       |
	//| col4                     | boolean               |                       |
	//| dt                       | string                |                       |
	//|                          | NULL                  | NULL                  |
	//| # Partition Information  | NULL                  | NULL                  |
	//| # col_name               | data_type             | comment               |
	//|                          | NULL                  | NULL                  |
	//| dt                       | string                |                       |
	//+--------------------------+-----------------------+-----------------------+--+
	public String tableColumns(String tableName, String alias) throws SQLException {
		List<String> columns = new ArrayList<String>();
		try(Connection conn = getConnection()) {
			try(Statement stmt = conn.createStatement()) {
				try(ResultSet rs = executeQuery(stmt, "describe " + tableName)) {
					while(rs.next()) {
						columns.add(rs.getString(1));
					}
				}
			}
		}
		if(!columns.isEmpty()) {
			int firstEmpty = columns.indexOf("");
			if(firstEmpty != -1) {
				List<String> realColumns = columns.subList(0, firstEmpty);
				List<String> partitionColumns = columns.subList(firstEmpty + 1, columns.size());
				int partitionColumnNumber = (partitionColumns.size() - 1) - partitionColumns.indexOf("");
				columns = realColumns.subList(0, realColumns.size() - partitionColumnNumber);
			}
			if(alias != null && alias.trim().length() > 0) {
				StringBuffer buffer = new StringBuffer();
				for(String c : columns) {
					buffer.append(",").append(alias).append(".").append(c).append(" as ").append(c);
				}
				return buffer.length() > 0 ? buffer.substring(1) : "";
			} else {
				return String.join(",", columns);
			}
		}
		return "";
	}

	public String tableColumns(String tableName) throws SQLException {
		return tableColumns(tableName, null);
	}

	private ResultSet executeQuery(Statement stmt, String query) throws SQLException {
		stmt.execute(DISABLE_CONCURRENCY);
		return stmt.executeQuery(query);
	}

	private boolean execute(Statement stmt, String query) throws SQLException {
		stmt.execute(DISABLE_CONCURRENCY);
		return stmt.execute(query);
	}

}
