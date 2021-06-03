package io.github.chme.dbdocgenerator;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import io.quarkus.arc.config.ConfigProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@ConfigProperties(prefix = "output")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutputConfiguration {

  String file;
  @Builder.Default
  String title = "Tables";
  @Singular
  List<TableConfig> tables;
  @Singular
  List<CategoryConfig> categories;

  public boolean hasTableConfig() {
    return !tables.isEmpty() && StringUtils.isNotBlank(tables.get(0).name);
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class TableConfig {
    String name;
    @Builder.Default
    Optional<String> title = Optional.empty();
    @Builder.Default
    Optional<String> description = Optional.empty();
    @Builder.Default
    Optional<String> category = Optional.empty();
  }

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategoryConfig {
    String name;
    @Builder.Default
    Optional<String> title = Optional.empty();
    @Builder.Default
    Optional<String> description = Optional.empty();
    @Builder.Default
    boolean isDefault = false;
  }
}
