package io.github.chme.dbdocgenerator.model;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseMetadataParser {

  @Inject
  DataSource dataSource;

  public List<Table> getTables() {
    List<Table> tables = new ArrayList<>();
    try (Connection con = dataSource.getConnection()) {
      DatabaseMetaData dbmd = con.getMetaData();
      ResultSet rs = dbmd.getTables(null, null, "%", new String[] {"TABLE", "VIEW"});
      while (rs.next()) {
        Table table = Table.builder()
            .catalog(rs.getString("TABLE_CAT"))
            .schema(rs.getString("TABLE_SCHEM"))
            .name(rs.getString("TABLE_NAME"))
            .type(StringUtils.capitalize(StringUtils.lowerCase(rs.getString("TABLE_TYPE"))))
            .remarks(rs.getString("REMARKS"))
            .sql(rs.getString("SQL"))
            .build();
        tables.add(table);

        addColumns(dbmd, table);

        printResultSet(dbmd.getExportedKeys(table.getCatalog(), table.getSchema(), table.getName()),
            "Exported Keys " + table.getName());

        printResultSet(dbmd.getImportedKeys(table.getCatalog(), table.getSchema(), table.getName()),
            "Imported Keys " + table.getName());

        printResultSet(
            dbmd.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, false),
            "Index Info " + table.getName());

        addIndexes(dbmd, table);
        enrichIndexWithForeignKeyReferences(dbmd, table);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return tables;
  }

  private Index mapIndex(String idxName, ResultSet indexInfo) {
    try {
      return Index.builder()
          .name(idxName)
          .unique(!indexInfo.getBoolean("NON_UNIQUE"))
          .build();
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void addColumns(DatabaseMetaData dbmd, Table table) throws SQLException {
    ResultSet rs = dbmd.getColumns(table.getCatalog(), table.getSchema(), table.getName(), null);
    while (rs.next()) {
      table.getColumns()
          .add(Column.builder()
              .name(rs.getString("COLUMN_NAME"))
              .type(rs.getString("TYPE_NAME"))
              .nullable(rs.getBoolean("NULLABLE"))
              .position(rs.getInt("ORDINAL_POSITION"))
              .size(rs.getInt("COLUMN_SIZE"))
              .decimalDigits(rs.getInt("DECIMAL_DIGITS"))
              .remarks(rs.getString("REMARKS"))
              .build());
    }
  }

  private void addIndexes(DatabaseMetaData dbmd, Table table) throws SQLException {
    ResultSet rs =
        dbmd.getIndexInfo(table.getCatalog(), table.getSchema(), table.getName(), false, false);
    while (rs.next()) {
      String idxName = rs.getString("INDEX_NAME");
      if (idxName.endsWith("_INDEX_3")) {
        idxName = idxName.substring(0, idxName.lastIndexOf("_INDEX_3"));
      }
      Index idx = table.getIndexes()
          .computeIfAbsent(idxName, k -> mapIndex(k, rs));
      idx.getColumns()
          .add(rs.getString("COLUMN_NAME"));
    }
  }

  private void enrichIndexWithForeignKeyReferences(DatabaseMetaData dbmd, Table table)
      throws SQLException {
    ResultSet rs = dbmd.getImportedKeys(table.getCatalog(), table.getSchema(), table.getName());
    while (rs.next()) {
      table.getIndexes()
          .computeIfPresent(rs.getString("FK_NAME"), (k, v) -> mapReferenceTable(rs, v));
    }
  }

  private Index mapReferenceTable(ResultSet exportedKeys, Index idx) {
    try {
      idx.setReferencesTable(exportedKeys.getString("PKTABLE_NAME"));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return idx;
  }

  private void printResultSet(ResultSet rs, String description) throws SQLException {
    log.debug("        ---- " + description + " ---------------------------");
    while (rs.next()) {
      printResultSetRow(rs);
    }
  }

  private void printResultSetRow(ResultSet rs) throws SQLException {
    log.debug("        ---- Result Set ---------------------------");
    int count2 = rs.getMetaData()
        .getColumnCount();
    for (int i = 1; i <= count2; i++) {
      log.debug("        {} ({}): {}",
          rs.getMetaData()
              .getColumnName(i),
          rs.getMetaData()
              .getColumnTypeName(i),
          rs.getString(i));
    }
  }
}
