package ru.otus.hw.batch.cache;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MongoIdRelationCache<T> {
    private final Map<String, T> relations = new HashMap<>();

    public void put(String mongoId, T jpaEntity) {
        relations.put(mongoId, jpaEntity);
    }

    public T get(String mongoId) {
        return relations.get(mongoId);
    }

    public List<T> getAll() {
        return relations.values().stream().toList();
    }
}
