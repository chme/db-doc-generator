package io.github.chme.dbdocgenerator.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Index {

  String name;
  boolean unique;
  String referencesTable;

  @Builder.Default
  List<String> columns = new ArrayList<>();
}
