package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private final AppProperties appProperties;

    private final ResourceProvider resourceProvider;

    @Override
    public List<Question> findAll() throws QuestionReadException {
        String testFileName = appProperties.getTestFileName();
        try (var questionsReader = resourceProvider.getResourceReader(testFileName)) {
            List<QuestionDto> questionDtoList = new CsvToBeanBuilder<QuestionDto>(questionsReader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();

            return questionDtoList.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (IOException e) {
            throw new QuestionReadException(e.getMessage(), e);
        }
    }
}
