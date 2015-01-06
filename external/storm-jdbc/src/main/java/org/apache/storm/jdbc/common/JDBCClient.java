/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.jdbc.common;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class JDBCClient {
    private static final Logger LOG = LoggerFactory.getLogger(JDBCClient.class);

    private HikariDataSource dataSource;

    public JDBCClient(Map<String, Object> map) {
        Properties properties = new Properties();
        properties.putAll(map);
        HikariConfig config = new HikariConfig(properties);
        this.dataSource = new HikariDataSource(config);
    }

    public int insert(String tableName, List<List<Column>> columnLists) {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            StringBuilder sb = new StringBuilder();
            sb.append("Insert into ").append(tableName).append(" (");
            Collection<String> columnNames = Collections2.transform(columnLists.get(0), new Function<Column, String>() {
                @Override
                public String apply(Column input) {
                    return input.getColumnName();
                }
            });
            String columns = Joiner.on(",").join(columnNames);
            sb.append(columns).append(") values ( ");

            String placeHolders = StringUtils.chop(StringUtils.repeat("?,", columnNames.size()));
            sb.append(placeHolders).append(")");

            String query = sb.toString();
            if(LOG.isDebugEnabled()) {
                LOG.debug("Executing query " + query);
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            for(List<Column> columnList : columnLists) {
                setPreparedStatementParams(preparedStatement, columnList);
            }

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert in table " + tableName, e);
        } finally {
            closeConnection(connection);
        }
    }

    public List<List<Column>> select(String sqlQuery, List<Column> queryParams) {
        Connection connection = null;
        Map<String, Integer> columnSchemaMap = new HashMap<String, Integer>();
        try {
            connection = this.dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            setPreparedStatementParams(preparedStatement, queryParams);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<List<Column>> rows = Lists.newArrayList();
            while(resultSet.next()){
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<Column> row = Lists.newArrayList();
                for(int i=1 ; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i);
                    int columnType = metaData.getColumnType(i);
                    Object val = null;
                    Class columnJavaType = Util.getJavaType(columnType);
                    if (columnJavaType == String.class) {
                        row.add(new Column<String>(columnLabel, resultSet.getString(columnLabel), columnType));
                    } else if (columnJavaType == Integer.class) {
                        row.add(new Column<Integer>(columnLabel, resultSet.getInt(columnLabel), columnType));
                    } else if (columnJavaType == Double.class) {
                        row.add(new Column<Double>(columnLabel, resultSet.getDouble(columnLabel), columnType));
                    } else if (columnJavaType == Float.class) {
                        row.add(new Column<Float>(columnLabel, resultSet.getFloat(columnLabel), columnType));
                    } else if (columnJavaType == Short.class) {
                        row.add(new Column<Short>(columnLabel, resultSet.getShort(columnLabel), columnType));
                    } else if (columnJavaType == Boolean.class) {
                        row.add(new Column<Boolean>(columnLabel, resultSet.getBoolean(columnLabel), columnType));
                    } else if (columnJavaType == byte[].class) {
                        row.add(new Column<byte[]>(columnLabel, resultSet.getBytes(columnLabel), columnType));
                    } else if (columnJavaType == Long.class) {
                        row.add(new Column<Long>(columnLabel, resultSet.getLong(columnLabel), columnType));
                    } else if (columnJavaType == Date.class) {
                        row.add(new Column<Date>(columnLabel, resultSet.getDate(columnLabel), columnType));
                    } else if (columnJavaType == Time.class) {
                        row.add(new Column<Time>(columnLabel, resultSet.getTime(columnLabel), columnType));
                    } else if (columnJavaType == Timestamp.class) {
                        row.add(new Column<Timestamp>(columnLabel, resultSet.getTimestamp(columnLabel), columnType));
                    } else {
                        throw new RuntimeException("type =  " + columnType + " for column " + columnLabel + " not supported.");
                    }
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute select query " + sqlQuery, e);
        } finally {
            closeConnection(connection);
        }
    }

    public Map<String, Integer> getColumnSchema(String tableName) {
        Connection connection = null;
        Map<String, Integer> columnSchemaMap = new HashMap<String, Integer>();
        try {
            connection = this.dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, null, tableName, null);
            while (resultSet.next()) {
                columnSchemaMap.put(resultSet.getString("COLUMN_NAME"), resultSet.getInt("DATA_TYPE"));
            }
            return columnSchemaMap;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get schema for table " + tableName, e);
        } finally {
            closeConnection(connection);
        }
    }

    public void executeSql(String sql) {
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute SQL", e);
        } finally {
            closeConnection(connection);
        }
    }

    private void setPreparedStatementParams(PreparedStatement preparedStatement, List<Column> columnList) throws SQLException {
        int index = 1;
        for (Column column : columnList) {
            Class columnJavaType = Util.getJavaType(column.getSqlType());
            if (column.getVal() == null) {
                preparedStatement.setNull(index, column.getSqlType());
            } else if (columnJavaType == String.class) {
                preparedStatement.setString(index, (String) column.getVal());
            } else if (columnJavaType == Integer.class) {
                preparedStatement.setInt(index, (Integer) column.getVal());
            } else if (columnJavaType == Double.class) {
                preparedStatement.setDouble(index, (Double) column.getVal());
            } else if (columnJavaType == Float.class) {
                preparedStatement.setFloat(index, (Float) column.getVal());
            } else if (columnJavaType == Short.class) {
                preparedStatement.setShort(index, (Short) column.getVal());
            } else if (columnJavaType == Boolean.class) {
                preparedStatement.setBoolean(index, (Boolean) column.getVal());
            } else if (columnJavaType == byte[].class) {
                preparedStatement.setBytes(index, (byte[]) column.getVal());
            } else if (columnJavaType == Long.class) {
                preparedStatement.setLong(index, (Long) column.getVal());
            } else if (columnJavaType == Date.class) {
                preparedStatement.setDate(index, (Date) column.getVal());
            } else if (columnJavaType == Time.class) {
                preparedStatement.setTime(index, (Time) column.getVal());
            } else if (columnJavaType == Timestamp.class) {
                preparedStatement.setTimestamp(index, (Timestamp) column.getVal());
            } else {
                throw new RuntimeException("Unknown type of value " + column.getVal() + " for column " + column.getColumnName());
            }
            ++index;
        }
    }

    private void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to close connection", e);
            }
        }
    }
}
