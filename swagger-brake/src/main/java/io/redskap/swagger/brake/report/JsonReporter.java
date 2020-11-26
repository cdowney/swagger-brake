package io.redskap.swagger.brake.report;

import static java.util.stream.Collectors.groupingBy;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.redskap.swagger.brake.core.BreakingChange;
import io.redskap.swagger.brake.report.file.DirectoryCreator;
import io.redskap.swagger.brake.report.file.FileWriter;
import io.redskap.swagger.brake.report.json.JsonConverter;
import io.redskap.swagger.brake.runner.OutputFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class JsonReporter extends AbstractFileReporter implements CheckableReporter {
    private static final String FILENAME = "swagger-brake.json";
    private final JsonConverter jsonConverter;

    public JsonReporter(FileWriter fileWriter, DirectoryCreator directoryCreator, JsonConverter jsonConverter) {
        super(fileWriter, directoryCreator);
        this.jsonConverter = jsonConverter;
    }

    @Override
    protected String getFilename() {
        return FILENAME;
    }

    @Override
    protected String toFileContent(Collection<BreakingChange> breakingChanges) {
        Map<String, List<BreakingChange>> nameMapping = breakingChanges.stream().collect(groupingBy(BreakingChange::getRuleCode));
        return jsonConverter.convert(nameMapping);
    }

    @Override
    public boolean canReport(OutputFormat format) {
        return OutputFormat.JSON.equals(format);
    }
}
