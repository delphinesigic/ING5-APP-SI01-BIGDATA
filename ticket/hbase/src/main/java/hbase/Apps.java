package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

public class Apps {
	
	static Configuration conf = HBaseConfiguration.create();
	
	public static void main(String[] args) throws Exception {
 
		conf.set("hbase.zookeeper.quorum", "zoo-1.au.adaltas.cloud,zoo-2.au.adaltas.cloud,zoo-3.au.adaltas.cloud");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		
		Connection connection = ConnectionFactory.createConnection(conf);
		
		Table table = connection.getTable(TableName.valueOf("ece_2021_fall_app_1:analyse_causale"));
		
		Scan s = new Scan();
        ResultScanner rs = table.getScanner(s);

         for (Result r : rs) {
              System.out.println(r.toString());
         }
	}

}
