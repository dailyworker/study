package io.dailyworker.framework.db;

import io.dailyworker.framework.aop.CustomHttpRequestLocal;
import io.dailyworker.framework.aop.CustomRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Table {

    private static final int MAX_RECORD_COUNT = 10000;
    private final LinkedHashMap<String , BigDecimal> colNameMap = new LinkedHashMap<>();
    private final ArrayList<String[]> records = new ArrayList<String[]>();

    private HashMap<String, String> addedData = null;
    private boolean isNextRecord = false;

    public Table() {}

    public Table(String[] colNames) {
        int index = 0;
        if(colNames == null) {
            throw new RuntimeException("허용되지 않은 값입니다. colNames = " + colNames);
        }
        for(String colName : colNames) {
            this.colNameMap.put(colName, new BigDecimal(index));
            index++;
        }
    }

    public boolean addRecord(String[] record) {
        if(hasMaxRecord()) {
            this.isNextRecord = true;
            return false;
        }
        if(record == null) {
            throw new RuntimeException("레코드는 Null일 수 없습니다.");
        }
        if(colNameMap.size() != record.length) {
            throw new RuntimeException("컬럼이 제대로 생성되지 않았습니다.");
        }
        records.add(record);
        return true;
    }

    public boolean addRecord() {
        if(hasMaxRecord()) {
            this.isNextRecord = true;
            return false;
        }
        String[] data = new String[colNameMap.size()];
        records.add(data);
        return true;
    }

    public void addColumn(String colName) {
        if(colNameMap.containsKey(colName)) {
            return;
        }
        BigDecimal colPoint = new BigDecimal(colNameMap.size());
        colNameMap.put(colName, colPoint);
    }

    public String findByColAndRow(String colName, int row) {
        BigDecimal maybeColNo = colNameMap.get(colName);
        int colNo = getColNo(colName, maybeColNo);

        String[] record = records.get(row);
        if(record == null) {
            throw new RuntimeException("존재 하지 않는 레코드입니다." + row);
        }
        if(record.length == 0) {
            return "";
        }

        if(record.length - 1 < colNo) {
            if(addedData == null) {
                return "";
            }
            String key = colNo + "_" + row;
            String thisData = addedData.get(key);
            if(thisData == null) {
                return "";
            }
            return thisData;
        }

        String thisData = record[colNo];
        if(thisData == null) {
            return "";
        }
        return thisData;
    }

    public String find(String colName, int row) {
        return findByColAndRow(colName, row);
    }

    public void update(String colName, int row, String data) {
        BigDecimal maybeColNo = colNameMap.get(colName);
        int colNo = getColNo(colName, maybeColNo);

        if(records.size() <= row) {
            throw new RuntimeException("현재 레코드의 크기보다 row가 작을 수 없습니다.");
        }

        String[] record = records.get(row);

        if(record == null) {
            throw new RuntimeException("레코드는 null일 수 없습니다.");
        }

        if(record.length - 1 < colNo) {
            if (addedData == null) {
                addedData = new HashMap<String, String>();
            }
            String key = colNo + "_" + row;
            addedData.put(key, data);
            return;
        }
        record[colNo] = data;
    }

    public CustomRequest getCustomRequest(int row) {
        CustomRequest customRequest = new CustomHttpRequestLocal();
        Iterator<String> iterator = colNameMap.keySet().iterator();
        while(iterator.hasNext()) {
            String colName = iterator.next();
            String data = findByColAndRow(colName, row);
            customRequest.put(colName, data);
        }
        return customRequest;
    }

    public CustomRequest getCustomRequest() {
        if(size() != 1) {
            throw new RuntimeException("record는 1개만 가질 수 있습니다.");
        }
        return getCustomRequest(0);
    }

    public int size() {
        return records.size();
    }

    public String[] getColumns() {
        int i = 0;
        String[] cols = new String[colNameMap.size()];

        Iterator<String> iterator = colNameMap.keySet().iterator();
        while (iterator.hasNext()) {
            String colName = iterator.next();
            cols[i] = colName;
            i++;
        }
        return cols;
    }

    public boolean isNextRecord() {
        return this.isNextRecord;
    }

    public static int MAX_RECORD_COUNT() {
        return MAX_RECORD_COUNT;
    }

    private int getColNo(String colName, BigDecimal maybeColNo) {
        if(maybeColNo == null) {
            throw new RuntimeException("존재 하지 않는 컬럼 명입니다." + colName);
        }
        return maybeColNo.intValue();
    }

    private boolean hasMaxRecord() {
        return records.size() + 1 > MAX_RECORD_COUNT;
    }

}
