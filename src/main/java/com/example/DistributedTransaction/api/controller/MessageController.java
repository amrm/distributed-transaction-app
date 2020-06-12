package com.example.DistributedTransaction.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RestController
public class MessageController {


    private static final String JMS_DESTINATION="queue";
    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping
    public Collection<Map<String,String>> read(){
        return jdbcTemplate.query("select * from message", new RowMapper<Map<String, String>>() {
            @Nullable
            @Override
            public Map<String, String> mapRow(ResultSet resultSet, int i) throws SQLException {
                Map<String, String> res=new HashMap<>();
                res.put("id",resultSet.getString("id"));
                res.put("message",resultSet.getString("message"));
                return res;
            }
        });
    }

    @Transactional
    @PostMapping
    public void write(@RequestBody Map<String,String> payload, @RequestParam Optional<Boolean> rollback){

        String message=payload.get("name");
        String id=UUID.randomUUID().toString();

        jdbcTemplate.update("insert into message (id,message) values (?,?)",id,message);

        jmsTemplate.convertAndSend(JMS_DESTINATION,message);

        if(rollback.orElse(false)){
            throw new RuntimeException("Coudn't write message");
        }

    }
}
