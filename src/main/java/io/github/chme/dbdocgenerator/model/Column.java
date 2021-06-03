package io.github.chme.dbdocgenerator.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public
class Column {
  String name;
  int position;
  String type;
  int size;
  int decimalDigits;
  boolean nullable;
  String remarks;
}
