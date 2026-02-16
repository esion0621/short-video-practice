package com.video.backend.util;

import lombok.RequiredArgsConstructor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class HBaseUtil {

    private final Connection hBaseConnection;

    public Map<String, String> getRow(String tableName, String rowKey, String family) {
        try (Table table = hBaseConnection.getTable(TableName.valueOf(tableName))) {
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addFamily(Bytes.toBytes(family));
            Result result = table.get(get);
            if (result.isEmpty()) return null;
            Map<String, String> map = new HashMap<>();
            result.listCells().forEach(cell -> {
                String qualifier = Bytes.toString(cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength());
                String value = Bytes.toString(cell.getValueArray(), cell.getValueOffset(), cell.getValueLength());
                map.put(family + ":" + qualifier, value);
            });
            return map;
        } catch (IOException e) {
            throw new RuntimeException("HBase查询失败", e);
        }
    }

    public void putRow(String tableName, String rowKey, String family, String qualifier, String value) {
        try (Table table = hBaseConnection.getTable(TableName.valueOf(tableName))) {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
            table.put(put);
        } catch (IOException e) {
            throw new RuntimeException("HBase写入失败", e);
        }
    }
}
