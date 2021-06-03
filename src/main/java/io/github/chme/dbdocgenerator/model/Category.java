package io.github.chme.dbdocgenerator.model;

import java.util.ArrayList;
import java.util.List;
import io.github.chme.dbdocgenerator.OutputConfiguration.CategoryConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Category {

  String name;
  String title;
  String description;
  boolean isDefault;
  @Builder.Default
  List<Table> tables = new ArrayList();

  public static Category create(CategoryConfig config) {
    return Category.builder()
        .name(config.getName())
        .title(config.getTitle()
            .orElse(config.getName()))
        .description(config.getDescription()
            .orElse(null))
        .isDefault(config.isDefault())
        .build();
  }
}
