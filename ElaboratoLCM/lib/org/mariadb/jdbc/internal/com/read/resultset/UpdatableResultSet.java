/*
 *
 * MariaDB Client for Java
 *
 * Copyright (c) 2012-2014 Monty Program Ab.
 * Copyright (c) 2015-2020 MariaDB Corporation Ab.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to Monty Program Ab info@montyprogram.com.
 *
 * This particular MariaDB Client for Java file is work
 * derived from a Drizzle-JDBC. Drizzle-JDBC file which is covered by subject to
 * the following copyright and notice provisions:
 *
 * Copyright (c) 2009-2011, Marcus Eriksson
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of the driver nor the names of its contributors may not be
 * used to endorse or promote products derived from this software without specific
 * prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS  AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */

package org.mariadb.jdbc.internal.com.read.resultset;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.mariadb.jdbc.*;
import org.mariadb.jdbc.internal.ColumnType;
import org.mariadb.jdbc.internal.com.read.dao.Results;
import org.mariadb.jdbc.internal.com.read.resultset.rowprotocol.BinaryRowProtocol;
import org.mariadb.jdbc.internal.com.send.parameters.*;
import org.mariadb.jdbc.internal.io.input.PacketInputStream;
import org.mariadb.jdbc.internal.protocol.Protocol;
import org.mariadb.jdbc.internal.util.exceptions.ExceptionFactory;

public class UpdatableResultSet extends SelectResultSet {

  private static final int STATE_STANDARD = 0;
  private static final int STATE_UPDATE = 1;
  private static final int STATE_UPDATED = 2;
  private static final int STATE_INSERT = 3;
  private String database;
  private String table;

  private int notInsertRowPointer;

  private SQLException updateException;
  private SQLException insertException;

  private int state = STATE_STANDARD;
  private final ParameterHolder[] parameterHolders;
  private MariaDbConnection connection;
  private PreparedStatement refreshPreparedStatement = null;
  private ClientSidePreparedStatement deletePreparedStatement = null;

  /**
   * Constructor.
   *
   * @param columnsInformation column information
   * @param results results
   * @param protocol current protocol
   * @param reader stream fetcher
   * @param callableResult is it from a callableStatement ?
   * @param eofDeprecated is EOF deprecated
   * @throws IOException if any connection error occur
   * @throws SQLException if any connection error occur
   */
  public UpdatableResultSet(
      ColumnDefinition[] columnsInformation,
      Results results,
      Protocol protocol,
      PacketInputStream reader,
      boolean callableResult,
      boolean eofDeprecated)
      throws IOException, SQLException {
    super(columnsInformation, results, protocol, reader, callableResult, eofDeprecated);
    checkIfUpdatable(results);
    parameterHolders = new ParameterHolder[columnInformationLength];
  }

  @Override
  public int getConcurrency() {
    return CONCUR_UPDATABLE;
  }

  private void checkIfUpdatable(Results results) throws SQLException {

    database = null;
    table = null;

    // check that resultSet concern one table and database exactly
    for (ColumnDefinition columnDefinition : columnsInformation) {

      if (columnDefinition.getDatabase() == null || columnDefinition.getDatabase().isEmpty()) {

        cannotUpdateInsertRow(
            "The result-set contains fields without without any database information", true);
        return;

      } else {

        if (database != null && !database.equals(columnDefinition.getDatabase())) {
          cannotUpdateInsertRow("The result-set contains more than one database", true);
          return;
        }

        database = columnDefinition.getDatabase();
      }

      if (columnDefinition.getOriginalTable() == null
          || columnDefinition.getOriginalTable().isEmpty()) {

        cannotUpdateInsertRow(
            "The result-set contains fields without without any table information", true);
        return;

      } else {

        if (table != null && !table.equals(columnDefinition.getOriginalTable())) {
          cannotUpdateInsertRow("The result-set contains fields on different tables", true);
          return;
        }

        table = columnDefinition.getOriginalTable();
      }
    }

    if (database == null) {
      cannotUpdateInsertRow("The result-set does not contain any table information", true);
      return;
    }

    if (table == null) {
      cannotUpdateInsertRow("The result-set does not contain any table information", true);
      return;
    }

    // read table metadata
    if (results.getStatement() != null && results.getStatement().getConnection() != null) {

      connection = results.getStatement().getConnection();
      Statement stmt =
          connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM `" + database + "`.`" + table + "`");

      UpdatableColumnDefinition[] updatableColumns =
          new UpdatableColumnDefinition[columnInformationLength];

      boolean primaryFound = false;
      while (rs.next()) {
        // read SHOW COLUMNS information
        String fieldName = rs.getString("Field");
        boolean canBeNull = "YES".equals(rs.getString("Null"));
        boolean hasDefault = rs.getString("Default") != null;
        String extra = rs.getString("Extra");
        boolean generated = extra != null && !extra.isEmpty();
        boolean autoIncrement = "auto_increment".equals(extra);
        boolean primary = "PRI".equals(rs.getString("Key"));

        boolean found = false;

        // update column information with SHOW COLUMNS additional information
        for (int index = 0; index < columnInformationLength; index++) {
          ColumnDefinition columnDefinition = columnsInformation[index];
          if (fieldName.equals(columnDefinition.getOriginalName())) {
            updatableColumns[index] =
                new UpdatableColumnDefinition(
                    columnDefinition, canBeNull, hasDefault, generated, primary, autoIncrement);
            found = true;
          }
        }

        if (primary) {
          primaryFound = true;
        }

        if (!found) {
          if (primary) {
            // without primary key in resultSet, update/delete cannot be done, since query need
            // to be updated/deleted for this unknown identifier
            //
            // For insert, key is not mandatory in resultSet if automatically generated, but data
            // cannot be added to rows in adequate format
            cannotUpdateInsertRow(
                "Primary key field `" + fieldName + "` is not in result-set", false);
            return;
          }

          // check that missing field can be null / have default values / are generated
          // automatically
          if (!canBeNull && !hasDefault && !generated) {
            cannotInsertRow(
                "Field `"
                    + fieldName
                    + "` is not present in query returning "
                    + "fields and cannot be null");
          }
        }
      }

      if (!primaryFound) {
        // if there is no primary key (UNIQUE key are considered as primary by SHOW COLUMNS),
        // rows cannot be updated.
        cannotUpdateInsertRow("Table `" + database + "`.`" + table + "` has no primary key", true);
        return;
      }

      boolean ensureAllColumnHaveMeta = true;
      for (int index = 0; index < columnInformationLength; index++) {
        if (updatableColumns[index] == null) {
          // abnormal error : some field in META are not listed in SHOW COLUMNS
          cannotUpdateInsertRow(
              "Metadata information not available for table `"
                  + database
                  + "`.`"
                  + table
                  + "`, field `"
                  + columnsInformation[index].getOriginalName()
                  + "`",
              false);
          ensureAllColumnHaveMeta = false;
        }
      }
      if (ensureAllColumnHaveMeta) {
        columnsInformation = updatableColumns;
      }
    } else {
      throw new SQLException("abnormal error : connection is null");
    }
  }

  private UpdatableColumnDefinition[] getUpdatableColumns() {
    return (UpdatableColumnDefinition[]) columnsInformation;
  }

  private void cannotUpdateInsertRow(String reason, boolean exceptionUpdateNotSupported) {
    if (updateException == null) {
      if (exceptionUpdateNotSupported) {
        updateException =
            new SQLFeatureNotSupportedException("ResultSet cannot be updated. " + reason);
      } else updateException = new SQLException("ResultSet cannot be updated. " + reason);
    }
    if (insertException == null) {
      if (exceptionUpdateNotSupported) {
        insertException = new SQLFeatureNotSupportedException("No row can be inserted. " + reason);
      } else insertException = new SQLException("No row can be inserted. " + reason);
    }
  }

  private void cannotInsertRow(String reason) {
    if (insertException == null) {
      insertException = new SQLException("No row can be inserted. " + reason);
    }
  }

  private void checkUpdatable(int position) throws SQLException {

    if (position <= 0 || position > columnInformationLength) {
      throw new SQLDataException("No such column: " + position, "22023");
    }

    if (state == STATE_STANDARD) {
      state = STATE_UPDATE;
    }
    if (state == STATE_UPDATE) {
      if (getRowPointer() < 0) {
        throw new SQLDataException("Current position is before the first row", "22023");
      }

      if (getRowPointer() >= getDataSize()) {
        throw new SQLDataException("Current position is after the last row", "22023");
      }

      if (updateException != null) {
        throw updateException;
      }
    }
    if (state == STATE_INSERT && insertException != null) {
      throw insertException;
    }
  }

  /** {inheritDoc}. */
  public void updateNull(int columnIndex) throws SQLException {
    checkUpdatable(columnIndex);

    parameterHolders[columnIndex - 1] = new NullParameter();
  }

  /** {inheritDoc}. */
  public void updateNull(String columnLabel) throws SQLException {
    updateNull(findColumn(columnLabel));
  }

  /** {inheritDoc}. */
  public void updateBoolean(int columnIndex, boolean bool) throws SQLException {
    checkUpdatable(columnIndex);
    parameterHolders[columnIndex - 1] = new ByteParameter(bool ? (byte) 1 : (byte) 0);
  }

  /** {inheritDoc}. */
  public void updateBoolean(String columnLabel, boolean value) throws SQLException {
    updateBoolean(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateByte(int columnIndex, byte value) throws SQLException {
    checkUpdatable(columnIndex);
    parameterHolders[columnIndex - 1] = new ByteParameter(value);
  }

  /** {inheritDoc}. */
  public void updateByte(String columnLabel, byte value) throws SQLException {
    updateByte(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateShort(int columnIndex, short value) throws SQLException {
    checkUpdatable(columnIndex);
    parameterHolders[columnIndex - 1] = new ShortParameter(value);
  }

  /** {inheritDoc}. */
  public void updateShort(String columnLabel, short value) throws SQLException {
    updateShort(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateInt(int columnIndex, int value) throws SQLException {
    checkUpdatable(columnIndex);
    parameterHolders[columnIndex - 1] = new IntParameter(value);
  }

  /** {inheritDoc}. */
  public void updateInt(String columnLabel, int value) throws SQLException {
    updateInt(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateFloat(int columnIndex, float value) throws SQLException {
    checkUpdatable(columnIndex);
    parameterHolders[columnIndex - 1] = new FloatParameter(value);
  }

  /** {inheritDoc}. */
  public void updateFloat(String columnLabel, float value) throws SQLException {
    updateFloat(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateDouble(int columnIndex, double value) throws SQLException {
    checkUpdatable(columnIndex);
    parameterHolders[columnIndex - 1] = new DoubleParameter(value);
  }

  /** {inheritDoc}. */
  public void updateDouble(String columnLabel, double value) throws SQLException {
    updateDouble(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateBigDecimal(int columnIndex, BigDecimal value) throws SQLException {
    checkUpdatable(columnIndex);
    if (value == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.DECIMAL);
      return;
    }
    parameterHolders[columnIndex - 1] = new BigDecimalParameter(value);
  }

  /** {inheritDoc}. */
  public void updateBigDecimal(String columnLabel, BigDecimal value) throws SQLException {
    updateBigDecimal(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateString(int columnIndex, String value) throws SQLException {
    checkUpdatable(columnIndex);
    if (value == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.STRING);
      return;
    }
    parameterHolders[columnIndex - 1] = new StringParameter(value, noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateString(String columnLabel, String value) throws SQLException {
    updateString(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateBytes(int columnIndex, byte[] value) throws SQLException {
    checkUpdatable(columnIndex);
    if (value == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.BLOB);
      return;
    }
    parameterHolders[columnIndex - 1] = new ByteArrayParameter(value, noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateBytes(String columnLabel, byte[] value) throws SQLException {
    updateBytes(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateDate(int columnIndex, Date date) throws SQLException {
    checkUpdatable(columnIndex);
    if (date == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.DATE);
      return;
    }
    parameterHolders[columnIndex - 1] = new DateParameter(date, TimeZone.getDefault(), options);
  }

  /** {inheritDoc}. */
  public void updateDate(String columnLabel, Date value) throws SQLException {
    updateDate(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateTime(int columnIndex, Time time) throws SQLException {
    checkUpdatable(columnIndex);
    if (time == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.TIME);
      return;
    }
    parameterHolders[columnIndex - 1] =
        new TimeParameter(time, TimeZone.getDefault(), options.useFractionalSeconds);
  }

  /** {inheritDoc}. */
  public void updateTime(String columnLabel, Time value) throws SQLException {
    updateTime(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateTimestamp(int columnIndex, Timestamp timeStamp) throws SQLException {
    checkUpdatable(columnIndex);
    if (timeStamp == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.DATETIME);
      return;
    }
    parameterHolders[columnIndex - 1] =
        new TimestampParameter(timeStamp, timeZone, options.useFractionalSeconds);
  }

  /** {inheritDoc}. */
  public void updateTimestamp(String columnLabel, Timestamp value) throws SQLException {
    updateTimestamp(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateAsciiStream(int columnIndex, InputStream inputStream) throws SQLException {
    updateAsciiStream(columnIndex, inputStream, Long.MAX_VALUE);
  }

  /** {inheritDoc}. */
  public void updateAsciiStream(String columnLabel, InputStream inputStream) throws SQLException {
    updateAsciiStream(findColumn(columnLabel), inputStream);
  }

  /** {inheritDoc}. */
  public void updateAsciiStream(int columnIndex, InputStream inputStream, int length)
      throws SQLException {
    updateAsciiStream(columnIndex, inputStream, (long) length);
  }

  /** {inheritDoc}. */
  public void updateAsciiStream(String columnLabel, InputStream inputStream, int length)
      throws SQLException {
    updateAsciiStream(findColumn(columnLabel), inputStream, length);
  }

  /** {inheritDoc}. */
  public void updateAsciiStream(int columnIndex, InputStream inputStream, long length)
      throws SQLException {
    checkUpdatable(columnIndex);
    if (inputStream == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.BLOB);
      return;
    }
    parameterHolders[columnIndex - 1] =
        new StreamParameter(inputStream, length, noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateAsciiStream(String columnLabel, InputStream inputStream, long length)
      throws SQLException {
    updateAsciiStream(findColumn(columnLabel), inputStream, length);
  }

  /** {inheritDoc}. */
  public void updateBinaryStream(int columnIndex, InputStream inputStream, int length)
      throws SQLException {
    updateBinaryStream(columnIndex, inputStream, (long) length);
  }

  /** {inheritDoc}. */
  public void updateBinaryStream(int columnIndex, InputStream inputStream, long length)
      throws SQLException {
    checkUpdatable(columnIndex);
    if (inputStream == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.BLOB);
      return;
    }
    parameterHolders[columnIndex - 1] =
        new StreamParameter(inputStream, length, noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateBinaryStream(String columnLabel, InputStream inputStream, int length)
      throws SQLException {
    updateBinaryStream(findColumn(columnLabel), inputStream, (long) length);
  }

  /** {inheritDoc}. */
  public void updateBinaryStream(String columnLabel, InputStream inputStream, long length)
      throws SQLException {
    updateBinaryStream(findColumn(columnLabel), inputStream, length);
  }

  /** {inheritDoc}. */
  public void updateBinaryStream(int columnIndex, InputStream inputStream) throws SQLException {
    updateBinaryStream(columnIndex, inputStream, Long.MAX_VALUE);
  }

  /** {inheritDoc}. */
  public void updateBinaryStream(String columnLabel, InputStream inputStream) throws SQLException {
    updateBinaryStream(findColumn(columnLabel), inputStream);
  }

  /** {inheritDoc}. */
  public void updateCharacterStream(int columnIndex, Reader reader, int length)
      throws SQLException {
    updateCharacterStream(columnIndex, reader, (long) length);
  }

  /** {inheritDoc}. */
  public void updateCharacterStream(int columnIndex, Reader value) throws SQLException {
    updateCharacterStream(columnIndex, value, Long.MAX_VALUE);
  }

  /** {inheritDoc}. */
  public void updateCharacterStream(String columnLabel, Reader reader, int length)
      throws SQLException {
    updateCharacterStream(findColumn(columnLabel), reader, (long) length);
  }

  /** {inheritDoc}. */
  public void updateCharacterStream(int columnIndex, Reader value, long length)
      throws SQLException {
    checkUpdatable(columnIndex);
    if (value == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.BLOB);
      return;
    }
    parameterHolders[columnIndex - 1] = new ReaderParameter(value, length, noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateCharacterStream(String columnLabel, Reader reader, long length)
      throws SQLException {
    updateCharacterStream(findColumn(columnLabel), reader, length);
  }

  /** {inheritDoc}. */
  public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
    updateCharacterStream(findColumn(columnLabel), reader, Long.MAX_VALUE);
  }

  private void updateInternalObject(
      final int parameterIndex, final Object obj, final int targetSqlType, final long scaleOrLength)
      throws SQLException {
    switch (targetSqlType) {
      case Types.ARRAY:
      case Types.DATALINK:
      case Types.JAVA_OBJECT:
      case Types.REF:
      case Types.ROWID:
      case Types.SQLXML:
      case Types.STRUCT:
        throw ExceptionFactory.INSTANCE.notSupported("Type not supported");
      default:
        break;
    }

    if (obj == null) {
      updateNull(parameterIndex);
    } else if (obj instanceof String) {
      if (targetSqlType == Types.BLOB) {
        throw ExceptionFactory.INSTANCE.create("Cannot convert a String to a Blob");
      }
      String str = (String) obj;
      try {
        switch (targetSqlType) {
          case Types.BIT:
          case Types.BOOLEAN:
            updateBoolean(parameterIndex, !("false".equalsIgnoreCase(str) || "0".equals(str)));
            break;
          case Types.TINYINT:
            updateByte(parameterIndex, Byte.parseByte(str));
            break;
          case Types.SMALLINT:
            updateShort(parameterIndex, Short.parseShort(str));
            break;
          case Types.INTEGER:
            updateInt(parameterIndex, Integer.parseInt(str));
            break;
          case Types.DOUBLE:
          case Types.FLOAT:
            updateDouble(parameterIndex, Double.parseDouble(str));
            break;
          case Types.REAL:
            updateFloat(parameterIndex, Float.parseFloat(str));
            break;
          case Types.BIGINT:
            updateLong(parameterIndex, Long.parseLong(str));
            break;
          case Types.DECIMAL:
          case Types.NUMERIC:
            updateBigDecimal(parameterIndex, new BigDecimal(str));
            break;
          case Types.CLOB:
          case Types.NCLOB:
          case Types.CHAR:
          case Types.VARCHAR:
          case Types.LONGVARCHAR:
          case Types.NCHAR:
          case Types.NVARCHAR:
          case Types.LONGNVARCHAR:
            updateString(parameterIndex, str);
            break;
          case Types.TIMESTAMP:
            if (str.startsWith("0000-00-00")) {
              updateTimestamp(parameterIndex, null);
            } else {
              updateTimestamp(parameterIndex, Timestamp.valueOf(str));
            }
            break;
          case Types.TIME:
            updateTime(parameterIndex, Time.valueOf((String) obj));
            break;
          case Types.TIME_WITH_TIMEZONE:
            parameterHolders[parameterIndex - 1] =
                new OffsetTimeParameter(
                    OffsetTime.parse(str), timeZone, options.useFractionalSeconds, options);
            break;
          case Types.TIMESTAMP_WITH_TIMEZONE:
            parameterHolders[parameterIndex - 1] =
                new ZonedDateTimeParameter(
                    ZonedDateTime.parse(str, BasePrepareStatement.SPEC_ISO_ZONED_DATE_TIME),
                    timeZone,
                    options.useFractionalSeconds,
                    options);
            break;
          default:
            throw ExceptionFactory.INSTANCE.create(
                "Could not convert [" + str + "] to " + targetSqlType);
        }
      } catch (IllegalArgumentException e) {
        throw ExceptionFactory.INSTANCE.create(
            "Could not convert [" + str + "] to " + targetSqlType, e);
      }
    } else if (obj instanceof Number) {
      Number bd = (Number) obj;
      switch (targetSqlType) {
        case Types.TINYINT:
          updateByte(parameterIndex, bd.byteValue());
          break;
        case Types.SMALLINT:
          updateShort(parameterIndex, bd.shortValue());
          break;
        case Types.INTEGER:
          updateInt(parameterIndex, bd.intValue());
          break;
        case Types.BIGINT:
          updateLong(parameterIndex, bd.longValue());
          break;
        case Types.FLOAT:
        case Types.DOUBLE:
          updateDouble(parameterIndex, bd.doubleValue());
          break;
        case Types.REAL:
          updateFloat(parameterIndex, bd.floatValue());
          break;
        case Types.DECIMAL:
        case Types.NUMERIC:
          if (obj instanceof BigDecimal) {
            updateBigDecimal(parameterIndex, (BigDecimal) obj);
          } else if (obj instanceof Double || obj instanceof Float) {
            updateDouble(parameterIndex, bd.doubleValue());
          } else {
            updateLong(parameterIndex, bd.longValue());
          }
          break;
        case Types.BIT:
          updateBoolean(parameterIndex, bd.shortValue() != 0);
          break;
        case Types.CHAR:
        case Types.VARCHAR:
          updateString(parameterIndex, bd.toString());
          break;
        default:
          throw ExceptionFactory.INSTANCE.create(
              "Could not convert [" + bd + "] to " + targetSqlType);
      }
    } else if (obj instanceof byte[]) {
      if (targetSqlType == Types.BINARY
          || targetSqlType == Types.VARBINARY
          || targetSqlType == Types.LONGVARBINARY) {
        updateBytes(parameterIndex, (byte[]) obj);
      } else {
        throw ExceptionFactory.INSTANCE.create(
            "Can only convert a byte[] to BINARY, VARBINARY or LONGVARBINARY");
      }

    } else if (obj instanceof Time) {
      updateTime(parameterIndex, (Time) obj); // it is just a string anyway
    } else if (obj instanceof Timestamp) {
      updateTimestamp(parameterIndex, (Timestamp) obj);
    } else if (obj instanceof Date) {
      updateDate(parameterIndex, (Date) obj);
    } else if (obj instanceof java.util.Date) {
      long time = ((java.util.Date) obj).getTime();
      if (targetSqlType == Types.DATE) {
        updateDate(parameterIndex, new Date(time));
      } else if (targetSqlType == Types.TIME) {
        updateTime(parameterIndex, new Time(time));
      } else if (targetSqlType == Types.TIMESTAMP) {
        updateTimestamp(parameterIndex, new Timestamp(time));
      }
    } else if (obj instanceof Boolean) {
      updateBoolean(parameterIndex, (Boolean) obj);
    } else if (obj instanceof Blob) {
      updateBlob(parameterIndex, (Blob) obj);
    } else if (obj instanceof Clob) {
      updateClob(parameterIndex, (Clob) obj);
    } else if (obj instanceof InputStream) {
      updateBinaryStream(parameterIndex, (InputStream) obj, scaleOrLength);
    } else if (obj instanceof Reader) {
      updateCharacterStream(parameterIndex, (Reader) obj, scaleOrLength);
    } else if (obj instanceof LocalDateTime) {
      updateTimestamp(parameterIndex, Timestamp.valueOf((LocalDateTime) obj));
    } else if (obj instanceof Instant) {
      updateTimestamp(parameterIndex, Timestamp.from((Instant) obj));
    } else if (obj instanceof LocalDate) {
      updateDate(parameterIndex, Date.valueOf((LocalDate) obj));
    } else if (obj instanceof OffsetDateTime) {
      parameterHolders[parameterIndex - 1] =
          new ZonedDateTimeParameter(
              ((OffsetDateTime) obj).toZonedDateTime(),
              timeZone,
              options.useFractionalSeconds,
              options);
    } else if (obj instanceof OffsetTime) {
      parameterHolders[parameterIndex - 1] =
          new OffsetTimeParameter(
              (OffsetTime) obj, timeZone, options.useFractionalSeconds, options);
    } else if (obj instanceof ZonedDateTime) {
      parameterHolders[parameterIndex - 1] =
          new ZonedDateTimeParameter(
              (ZonedDateTime) obj, timeZone, options.useFractionalSeconds, options);
    } else if (obj instanceof LocalTime) {
      updateTime(parameterIndex, Time.valueOf((LocalTime) obj));
    } else {
      throw ExceptionFactory.INSTANCE.create(
          "Could not set parameter in setObject, could not convert: "
              + obj.getClass()
              + " to "
              + targetSqlType);
    }
  }

  /** {inheritDoc}. */
  public void updateObject(int columnIndex, Object value, int scaleOrLength) throws SQLException {
    checkUpdatable(columnIndex);
    updateInternalObject(
        columnIndex,
        value,
        columnsInformation[columnIndex - 1].getColumnType().getSqlType(),
        scaleOrLength);
  }

  /** {inheritDoc}. */
  public void updateObject(int columnIndex, Object value) throws SQLException {
    checkUpdatable(columnIndex);
    updateInternalObject(
        columnIndex,
        value,
        columnsInformation[columnIndex - 1].getColumnType().getSqlType(),
        Long.MAX_VALUE);
  }

  /** {inheritDoc}. */
  public void updateObject(String columnLabel, Object value, int scaleOrLength)
      throws SQLException {
    updateObject(findColumn(columnLabel), value, scaleOrLength);
  }

  /** {inheritDoc}. */
  public void updateObject(String columnLabel, Object value) throws SQLException {
    updateObject(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateLong(int columnIndex, long value) throws SQLException {
    checkUpdatable(columnIndex);
    parameterHolders[columnIndex - 1] = new LongParameter(value);
  }

  /** {inheritDoc}. */
  public void updateLong(String columnLabel, long value) throws SQLException {
    updateLong(findColumn(columnLabel), value);
  }

  /** {inheritDoc}. */
  public void updateRef(int columnIndex, Ref ref) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("REF not supported");
  }

  /** {inheritDoc}. */
  public void updateRef(String columnLabel, Ref ref) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("REF not supported");
  }

  /** {inheritDoc}. */
  public void updateBlob(int columnIndex, Blob blob) throws SQLException {
    checkUpdatable(columnIndex);
    if (blob == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.BLOB);
      return;
    }
    parameterHolders[columnIndex - 1] =
        new StreamParameter(blob.getBinaryStream(), blob.length(), noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateBlob(String columnLabel, Blob blob) throws SQLException {
    updateBlob(findColumn(columnLabel), blob);
  }

  /** {inheritDoc}. */
  public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
    updateBlob(columnIndex, inputStream, Long.MAX_VALUE);
  }

  /** {inheritDoc}. */
  public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
    updateBlob(findColumn(columnLabel), inputStream, Long.MAX_VALUE);
  }

  /** {inheritDoc}. */
  public void updateBlob(int columnIndex, InputStream inputStream, long length)
      throws SQLException {
    checkUpdatable(columnIndex);
    if (inputStream == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.BLOB);
      return;
    }
    parameterHolders[columnIndex - 1] =
        new StreamParameter(inputStream, length, noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateBlob(String columnLabel, InputStream inputStream, long length)
      throws SQLException {
    updateBlob(findColumn(columnLabel), inputStream, length);
  }

  /** {inheritDoc}. */
  public void updateClob(int columnIndex, Clob clob) throws SQLException {
    checkUpdatable(columnIndex);
    if (clob == null) {
      parameterHolders[columnIndex - 1] = new NullParameter(ColumnType.BLOB);
      return;
    }
    parameterHolders[columnIndex - 1] =
        new ReaderParameter(clob.getCharacterStream(), clob.length(), noBackslashEscapes);
  }

  /** {inheritDoc}. */
  public void updateClob(String columnLabel, Clob clob) throws SQLException {
    updateClob(findColumn(columnLabel), clob);
  }

  /** {inheritDoc}. */
  public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
    updateCharacterStream(columnIndex, reader, length);
  }

  /** {inheritDoc}. */
  public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
    updateCharacterStream(findColumn(columnLabel), reader, length);
  }

  /** {inheritDoc}. */
  public void updateClob(int columnIndex, Reader reader) throws SQLException {
    updateCharacterStream(columnIndex, reader);
  }

  /** {inheritDoc}. */
  public void updateClob(String columnLabel, Reader reader) throws SQLException {
    updateCharacterStream(findColumn(columnLabel), reader);
  }

  /** {inheritDoc}. */
  public void updateArray(int columnIndex, Array array) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("Arrays not supported");
  }

  /** {inheritDoc}. */
  public void updateArray(String columnLabel, Array array) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("Arrays not supported");
  }

  /** {inheritDoc}. */
  public void updateRowId(int columnIndex, RowId rowId) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("RowIDs not supported");
  }

  /** {inheritDoc}. */
  public void updateRowId(String columnLabel, RowId rowId) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("RowIDs not supported");
  }

  /** {inheritDoc}. */
  public void updateNString(int columnIndex, String value) throws SQLException {
    updateString(columnIndex, value);
  }

  /** {inheritDoc}. */
  public void updateNString(String columnLabel, String value) throws SQLException {
    updateString(columnLabel, value);
  }

  /** {inheritDoc}. */
  public void updateNClob(int columnIndex, NClob nclob) throws SQLException {
    updateClob(columnIndex, nclob);
  }

  /** {inheritDoc}. */
  public void updateNClob(String columnLabel, NClob nclob) throws SQLException {
    updateClob(columnLabel, nclob);
  }

  /** {inheritDoc}. */
  public void updateNClob(int columnIndex, Reader reader) throws SQLException {
    updateClob(columnIndex, reader);
  }

  /** {inheritDoc}. */
  public void updateNClob(String columnLabel, Reader reader) throws SQLException {
    updateClob(columnLabel, reader);
  }

  /** {inheritDoc}. */
  public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
    updateClob(columnIndex, reader, length);
  }

  /** {inheritDoc}. */
  public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
    updateClob(columnLabel, reader, length);
  }

  /** {inheritDoc}. */
  @Override
  public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("SQlXML not supported");
  }

  /** {inheritDoc}. */
  @Override
  public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
    throw ExceptionFactory.INSTANCE.notSupported("SQLXML not supported");
  }

  /** {inheritDoc}. */
  public void updateNCharacterStream(int columnIndex, Reader value, long length)
      throws SQLException {
    updateCharacterStream(columnIndex, value, length);
  }

  /** {inheritDoc}. */
  public void updateNCharacterStream(String columnLabel, Reader reader, long length)
      throws SQLException {
    updateCharacterStream(columnLabel, reader, length);
  }

  /** {inheritDoc}. */
  public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException {
    updateCharacterStream(columnIndex, reader);
  }

  /** {inheritDoc}. */
  public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
    updateCharacterStream(columnLabel, reader);
  }

  /** {inheritDoc}. */
  public void insertRow() throws SQLException {
    if (state == STATE_INSERT) {

      boolean hasGeneratedPrimaryFields = false;
      int generatedSqlType = 0;
      HashMap<Integer, ParameterHolder> paramMap = new HashMap<>();

      // Create query will all field with WHERE clause contain primary field.
      // if field are not updated, value DEFAULT will be set
      // (if field has no default, then insert will throw an exception that will be return to
      // user)
      StringBuilder insertSql = new StringBuilder("INSERT `" + database + "`.`" + table + "` ( ");
      StringBuilder valueClause = new StringBuilder();
      StringBuilder returningClause = new StringBuilder();
      int fieldsIndex = 0;
      boolean firstParam = true;

      for (int pos = 0; pos < columnInformationLength; pos++) {
        UpdatableColumnDefinition colInfo = getUpdatableColumns()[pos];

        if (pos != 0) {
          returningClause.append(", ");
        }
        returningClause.append("`").append(colInfo.getOriginalName()).append("`");

        ParameterHolder value = parameterHolders[pos];
        if (value != null) {
          if (!firstParam) {
            insertSql.append(",");
            valueClause.append(", ");
          }
          insertSql.append("`").append(colInfo.getOriginalName()).append("`");
          valueClause.append("?");
          paramMap.put((fieldsIndex++) + 1, value);
          firstParam = false;
        } else {
          if (colInfo.isPrimary()) {
            if (colInfo.isAutoIncrement() || colInfo.hasDefault()) {
              if (colInfo.isAutoIncrement()) {
                hasGeneratedPrimaryFields = true;
                generatedSqlType = colInfo.getColumnType().getSqlType();
              } else if (!connection.isServerMariaDb()
                  || !connection.versionGreaterOrEqual(10, 5, 1)) {
                // driver cannot know generated default value like uuid().
                // but for server 10.5+, will use RETURNING to know primary key
                throw new SQLException(
                    String.format(
                        "Cannot call insertRow() not setting value for primary key %s "
                            + "with default value before server 10.5",
                        colInfo.getOriginalName()));
              }
            } else {
              throw new SQLException(
                  String.format(
                      "Cannot call insertRow() not setting value for primary key %s",
                      colInfo.getOriginalName()));
            }
            // paramMap.put((fieldsIndex++) + 1, new DefaultParameter());
          } else if (!colInfo.hasDefault()) {
            if (!firstParam) {
              insertSql.append(",");
              valueClause.append(", ");
            }
            firstParam = false;
            insertSql.append("`").append(colInfo.getOriginalName()).append("`");
            valueClause.append("?");

            paramMap.put((fieldsIndex++) + 1, new NullParameter());
          }
        }
      }
      insertSql.append(") VALUES (").append(valueClause).append(")");
      if (connection.isServerMariaDb() && connection.versionGreaterOrEqual(10, 5, 1)) {
        insertSql.append(" RETURNING ").append(returningClause);
      }

      try (BasePrepareStatement insertPreparedStatement =
          (row instanceof BinaryRowProtocol)
              ? connection.serverPrepareStatement(insertSql.toString())
              : connection.clientPrepareStatement(insertSql.toString())) {

        for (Map.Entry<Integer, ParameterHolder> entry : paramMap.entrySet()) {
          insertPreparedStatement.setParameter(entry.getKey(), entry.getValue());
        }

        ResultSet insertRs = insertPreparedStatement.executeQuery();
        if (connection.isServerMariaDb() && connection.versionGreaterOrEqual(10, 5, 1)) {
          if (insertRs.next()) {
            byte[] rowByte = ((SelectResultSet) insertRs).getCurrentRowData();
            addRowData(rowByte);
          }
        } else if (hasGeneratedPrimaryFields) {
          // primary is auto_increment (only one field)
          ResultSet rsKey = insertPreparedStatement.getGeneratedKeys();
          if (rsKey.next()) {

            prepareRefreshStmt();
            refreshPreparedStatement.setObject(1, rsKey.getObject(1), generatedSqlType);
            SelectResultSet rs = (SelectResultSet) refreshPreparedStatement.executeQuery();

            // update row data only if not deleted externally
            if (rs.next()) {
              addRowData(rs.getCurrentRowData());
            }
          }
        } else {
          addRowData(refreshRawData());
        }
      }
      Arrays.fill(parameterHolders, null);
    }
  }

  /** {inheritDoc}. */
  public void updateRow() throws SQLException {

    if (state == STATE_INSERT) {
      throw new SQLException("Cannot call updateRow() when inserting a new row");
    }

    if (state == STATE_UPDATE) {

      // state is STATE_UPDATE, meaning that at least one field is modified, update query can be
      // run.
      // Construct UPDATE query according to modified field only
      StringBuilder updateSql = new StringBuilder("UPDATE `" + database + "`.`" + table + "` SET ");
      StringBuilder whereClause = new StringBuilder(" WHERE ");

      boolean firstUpdate = true;
      boolean firstPrimary = true;
      int fieldsToUpdate = 0;
      for (int pos = 0; pos < columnInformationLength; pos++) {
        UpdatableColumnDefinition colInfo = getUpdatableColumns()[pos];

        ParameterHolder value = parameterHolders[pos];
        if (colInfo.isPrimary()) {
          if (!firstPrimary) {
            whereClause.append("AND ");
          }
          firstPrimary = false;
          whereClause.append("`").append(colInfo.getOriginalName()).append("` = ? ");
        }

        if (value != null) {
          if (!firstUpdate) {
            updateSql.append(",");
          }
          firstUpdate = false;
          fieldsToUpdate++;
          updateSql.append("`").append(colInfo.getOriginalName()).append("` = ? ");
        }
      }
      updateSql.append(whereClause);

      ClientSidePreparedStatement preparedStatement =
          connection.clientPrepareStatement(updateSql.toString());
      int fieldsIndex = 0;
      int fieldsPrimaryIndex = 0;
      for (int pos = 0; pos < columnInformationLength; pos++) {
        UpdatableColumnDefinition colInfo = getUpdatableColumns()[pos];
        ParameterHolder value = parameterHolders[pos];

        if (value != null) {
          preparedStatement.setParameter((fieldsIndex++) + 1, value);
        }

        if (colInfo.isPrimary()) {
          preparedStatement.setObject(
              fieldsToUpdate + (fieldsPrimaryIndex++) + 1,
              getObject(pos + 1),
              colInfo.getColumnType().getSqlType());
        }
      }
      preparedStatement.execute();

      state = STATE_UPDATED;

      refreshRow();

      Arrays.fill(parameterHolders, null);
      state = STATE_STANDARD;
    }
  }

  /** {inheritDoc}. */
  public void deleteRow() throws SQLException {

    if (state == STATE_INSERT) {
      throw new SQLException("Cannot call deleteRow() when inserting a new row");
    }

    if (updateException != null) {
      throw updateException;
    }

    if (getRowPointer() < 0) {
      throw new SQLDataException("Current position is before the first row", "22023");
    }

    if (getRowPointer() >= getDataSize()) {
      throw new SQLDataException("Current position is after the last row", "22023");
    }

    if (deletePreparedStatement == null) {
      // Create query with WHERE clause contain primary field.
      StringBuilder deleteSql =
          new StringBuilder("DELETE FROM `" + database + "`.`" + table + "` WHERE ");
      boolean firstPrimary = true;
      for (int pos = 0; pos < columnInformationLength; pos++) {
        UpdatableColumnDefinition colInfo = getUpdatableColumns()[pos];

        if (colInfo.isPrimary()) {
          if (!firstPrimary) {
            deleteSql.append("AND ");
          }
          firstPrimary = false;
          deleteSql.append("`").append(colInfo.getOriginalName()).append("` = ? ");
        }
      }
      deletePreparedStatement = connection.clientPrepareStatement(deleteSql.toString());
    }

    int fieldsPrimaryIndex = 1;

    for (int pos = 0; pos < columnInformationLength; pos++) {
      UpdatableColumnDefinition colInfo = getUpdatableColumns()[pos];
      if (colInfo.isPrimary()) {
        deletePreparedStatement.setObject(
            fieldsPrimaryIndex++, getObject(pos + 1), colInfo.getColumnType().getSqlType());
      }
    }

    deletePreparedStatement.executeUpdate();

    deleteCurrentRowData();
  }

  private void prepareRefreshStmt() throws SQLException {
    if (refreshPreparedStatement == null) {
      // Construct SELECT query according to column metadata, with WHERE part containing primary
      // fields
      StringBuilder selectSql = new StringBuilder("SELECT ");
      StringBuilder whereClause = new StringBuilder(" WHERE ");

      boolean firstPrimary = true;
      for (int pos = 0; pos < columnInformationLength; pos++) {
        UpdatableColumnDefinition colInfo = getUpdatableColumns()[pos];
        if (pos != 0) {
          selectSql.append(",");
        }
        selectSql.append("`").append(colInfo.getOriginalName()).append("`");

        if (colInfo.isPrimary()) {
          if (!firstPrimary) {
            whereClause.append("AND ");
          }
          firstPrimary = false;
          whereClause.append("`").append(colInfo.getOriginalName()).append("` = ? ");
        }
      }
      selectSql
          .append(" FROM `")
          .append(database)
          .append("`.`")
          .append(table)
          .append("`")
          .append(whereClause);

      // row's raw bytes must be encoded according to current resultSet type
      // Create Server or Client PrepareStatement accordingly
      if (isBinaryEncoded()) {
        refreshPreparedStatement = connection.serverPrepareStatement(selectSql.toString());
      } else {
        refreshPreparedStatement = connection.clientPrepareStatement(selectSql.toString());
      }
    }
  }

  private byte[] refreshRawData() throws SQLException {
    prepareRefreshStmt();
    int fieldsPrimaryIndex = 1;
    for (int pos = 0; pos < columnInformationLength; pos++) {
      UpdatableColumnDefinition colInfo = getUpdatableColumns()[pos];
      if (colInfo.isPrimary()) {
        ParameterHolder value = parameterHolders[pos];

        if (state != STATE_STANDARD && value != null) {
          // Row has just been updated using updateRow() methods.
          // updateRow has changed primary key, must use the new value.
          if (isBinaryEncoded()) {
            ((ServerSidePreparedStatement) refreshPreparedStatement)
                .setParameter(fieldsPrimaryIndex++, value);
          } else {
            ((ClientSidePreparedStatement) refreshPreparedStatement)
                .setParameter(fieldsPrimaryIndex++, value);
          }
        } else {
          refreshPreparedStatement.setObject(
              fieldsPrimaryIndex++, getObject(pos + 1), colInfo.getColumnType().getSqlType());
        }
      }
    }

    SelectResultSet rs = (SelectResultSet) refreshPreparedStatement.executeQuery();

    // update row data only if not deleted externally
    if (rs.next()) {
      return rs.getCurrentRowData();
    }
    return new byte[0];
  }

  /** {inheritDoc}. */
  public void refreshRow() throws SQLException {
    if (state == STATE_INSERT) {
      throw new SQLException("Cannot call deleteRow() when inserting a new row");
    }

    if (getRowPointer() < 0) {
      throw new SQLDataException("Current position is before the first row", "22023");
    }

    if (getRowPointer() >= getDataSize()) {
      throw new SQLDataException("Current position is after the last row", "22023");
    }

    if (updateException == null) {
      updateRowData(refreshRawData());
    }
  }

  /** {inheritDoc}. */
  public void cancelRowUpdates() {
    Arrays.fill(parameterHolders, null);
    state = STATE_STANDARD;
  }

  /** {inheritDoc}. */
  public void moveToInsertRow() throws SQLException {
    if (insertException != null) {
      throw insertException;
    }
    Arrays.fill(parameterHolders, null);
    state = STATE_INSERT;
    notInsertRowPointer = getRowPointer();
  }

  /** {inheritDoc}. */
  public void moveToCurrentRow() {
    Arrays.fill(parameterHolders, null);
    state = STATE_STANDARD;
    setRowPointer(notInsertRowPointer);
  }

  @Override
  public void beforeFirst() throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    super.beforeFirst();
  }

  @Override
  public boolean first() throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    return super.first();
  }

  @Override
  public boolean last() throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    return super.last();
  }

  @Override
  public void afterLast() throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    super.afterLast();
  }

  @Override
  public boolean absolute(int row) throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    return super.absolute(row);
  }

  @Override
  public boolean relative(int rows) throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    return super.relative(rows);
  }

  @Override
  public boolean next() throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    return super.next();
  }

  @Override
  public boolean previous() throws SQLException {
    if (state == STATE_INSERT) {
      state = STATE_UPDATE;
      setRowPointer(notInsertRowPointer);
    }
    return super.previous();
  }

  public void close() throws SQLException {
    if (refreshPreparedStatement != null) refreshPreparedStatement.close();
    super.close();
  }
}
