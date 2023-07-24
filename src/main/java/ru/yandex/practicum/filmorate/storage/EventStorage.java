package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EventNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
@Qualifier("eventStorage")
@AllArgsConstructor
public class EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public Event addEvent(int entityId, String eventType, String operation, int userId) {
        String sql = "insert into events (user_id, event_type_id, event_operation_id, entity_id, event_timestamp)" +
                    "values (?, ?, ?, ?, ?)";
        Event event = new Event(0, entityId, eventType, operation, userId, Instant.now().toEpochMilli());
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
                stmt.setInt(1, event.getUserId());
                stmt.setInt(2, eventTypeId(event.getEventType()));
                stmt.setInt(3, eventOperationId(event.getOperation()));
                stmt.setInt(4, event.getEntityId());
                stmt.setLong(5, event.getTimestamp());
                return stmt;
            }, keyHolder);

            event.setEventId(keyHolder.getKey().intValue());

            return event;
        } catch (DataIntegrityViolationException e) {
            throw new UserNotFoundException(String.format("User by id %d not found", event.getUserId()));
        }
    }

    public List<Event> getAllEvents(int id) {
        try {
            String sql = "select event_id, user_id, et.event_type_name, " +
                    "eo.event_operation_name, entity_id, event_timestamp " +
                    "from events e " +
                    "join event_types et on e.event_type_id = et.event_type_id " +
                    "join event_operations eo on e.event_operation_id = eo.event_operation_id " +
                    "where user_id = ?;";
            return jdbcTemplate.queryForObject(sql, eventRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    private RowMapper<List<Event>> eventRowMapper() {
        return (rs, rowNum) -> {
            List<Event> events = new ArrayList<>();
            do {
                events.add(new Event(rs.getInt("event_id"),
                        rs.getInt("entity_id"),
                        rs.getString("event_type_name"),
                        rs.getString("event_operation_name"),
                        rs.getInt("user_id"),
                        rs.getLong("event_timestamp")));
            } while (rs.next());
            return events;
        };
    }

    private int eventTypeId(String type) {
        switch (type) {
            case "LIKE": return 1;
            case "REVIEW": return 2;
            case "FRIEND": return 3;
            default: throw new EventNotFoundException(String.format("Event type %s not found", type));
        }
    }

    private int eventOperationId(String operation) {
        switch (operation) {
            case "REMOVE": return 1;
            case "ADD": return 2;
            case "UPDATE": return 3;
            default: throw new EventNotFoundException(String.format("Event operation %s not found", operation));
        }
    }
}