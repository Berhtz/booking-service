package com.berhtz.booking.config;

import java.util.List;
import java.time.LocalDate;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix="booking")
/*Для хранения списка выходных, часов работы для обычных и сокращенных дней,
а также выходных и макс. кол-ва людей, которые могут записаться на одно время */ 
public class Config {
    
    private Set<String> weekends;

    private List<String> workingTimeHoliday;
    private List<String> workingTimeRegular;

    private List<LocalDate> holidays;

    private Integer maxSlots;

    private Integer maxBookingsForClient;
}
