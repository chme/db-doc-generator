package io.github.chme.dbdocgenerator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Table {

  String catalog;
  String schema;
  String name;
  String type;
  String remarks;
  String sql;

  String title;
  String description;
  String category;

  @Builder.Default
  List<Column> columns = new ArrayList<>();

  @Builder.Default
  Map<String, Index> indexes = new HashMap<>();
}
