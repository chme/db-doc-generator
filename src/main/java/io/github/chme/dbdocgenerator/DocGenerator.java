package io.github.chme.dbdocgenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import io.github.chme.dbdocgenerator.model.Table;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import lombok.extern.slf4j.Slf4j;

@QuarkusMain
@Slf4j
public class DocGenerator implements QuarkusApplication {

  @Inject
  OutputConfiguration outputConfiguration;

  @Inject
  DatabaseMetadataParser dbParser;

  @Inject
  TemplateRenderer renderer;

  @Override
  public int run(String... args) throws Exception {
    List<Table> tablesData = dbParser.getTables();
    String renderedTemplate = renderer.render(tablesData);
    writeToFile(renderedTemplate);
    return 0;
  }

  private void writeToFile(String renderedTemplate) throws AssertionError {
    log.debug(renderedTemplate);

    try (BufferedWriter writer = createWriter();) {
      writer.write(renderedTemplate);
    } catch (IOException e) {
      throw new AssertionError(e);
    }
  }

  private BufferedWriter createWriter() throws IOException {
    return new BufferedWriter(new FileWriter(outputConfiguration.file));
  }
}
