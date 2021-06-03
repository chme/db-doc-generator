package io.github.chme.dbdocgenerator;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import io.github.chme.dbdocgenerator.OutputConfiguration.TableConfig;
import io.github.chme.dbdocgenerator.model.Category;
import io.github.chme.dbdocgenerator.model.Table;
import io.quarkus.qute.Template;
import lombok.extern.slf4j.Slf4j;

@Dependent
@Slf4j
public class TemplateRenderer {

  @Inject
  Template database;

  @Inject
  OutputConfiguration outputConfiguration;

  public String render(List<Table> tables) {
    log.debug("{}", outputConfiguration.tables);

    List<Category> categories = createCategories();
    addTables(categories, tables);
    String renderedTemplate = database.instance()
        .data("title", outputConfiguration.title)
        .data("categories", categories)
        .render();
    return renderedTemplate;
  }

  private List<Category> createCategories() {
    List<Category> categories = outputConfiguration.categories.stream()
        .map(Category::create)
        .collect(Collectors.toList());
    return categories;
  }

  private List<Category> addTables(List<Category> categories, List<Table> tables) {
    Map<String, Table> tablesMap = tables.stream()
        .collect(Collectors.toMap(t -> t.getName()
            .toUpperCase(), Function.identity()));
    if (outputConfiguration.hasTableConfig()) {
      outputConfiguration.tables.stream()
          .map(c -> findAndEnrich(c, tablesMap))
          .forEach(t -> addToCategory(categories, t));
    } else {
      tables.stream()
          .sorted(Comparator.comparing(Table::getName))
          .forEach(t -> addToCategory(categories, t));
    }
    return categories;
  }

  private Table findAndEnrich(TableConfig config, Map<String, Table> tablesMap) {
    Table table = tablesMap.remove(config.name.toUpperCase());
    table.setTitle(config.title.orElse(null));
    table.setDescription(config.description.orElse(null));
    table.setCategory(config.category.orElse(null));
    return table;
  }

  private void addToCategory(List<Category> categories, Table table) {
    Optional<Category> matchingCategory = categories.stream()
        .filter(c -> StringUtils.equals(c.getName(), table.getCatalog()))
        .findFirst()
        .or(() -> getDefaultCategory(categories));
    matchingCategory.ifPresent(c -> c.getTables()
        .add(table));
  }

  private Optional<Category> getDefaultCategory(List<Category> categories) {
    return categories.stream()
        .filter(Category::isDefault)
        .findFirst();
  }
}
