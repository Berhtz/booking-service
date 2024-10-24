package com.berhtz.booking.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.berhtz.booking.client.Client;
import com.berhtz.booking.client.ClientRepository;
import com.berhtz.booking.config.Config;

@Service
public class BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    private Config config;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<Booking> getAllBookings(LocalDate date) {
        return bookingRepository.findByDate(date);
    }

    public Map<LocalTime, Integer> getAvailableBookings(LocalDate date) {
        Map<LocalTime, Integer> availableHours = new HashMap<>();

        List<String> hours;

        // Получаем список рабочих часов на основе обычного дня или праздника
        if (isHoliday(date)) {
            hours = config.getWorkingTimeHoliday();
        } else {
            hours = config.getWorkingTimeRegular();
        }

        // Для каждого времени в списке считаем количество доступных слотов
        for (String hour : hours) {
            LocalTime time = LocalTime.parse(hour); // Парсим время из строки
            int availableSlots = config.getMaxSlots() - countClientsCountByDateAndTime(date, time);
            availableHours.put(time, availableSlots); // Добавляем время и доступные слоты в Map
        }

        return availableHours;
    }

    public Long addBooking(Long clientId, LocalDateTime dateTime) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        LocalTime time = dateTime.toLocalTime();
        LocalDate date = dateTime.toLocalDate();

        Optional<Booking> existingBooking = bookingRepository.findByDateAndTime(date, time);

        validateBooking(clientId, dateTime);

        if (!existingBooking.isPresent()) {
            return createNewBooking(client, date, time);
        }
        return addClientToExistingBooking(existingBooking.get(), client, dateTime);

    }

    public ResponseEntity<Void> cancel(Long clientId, Long bookingId) {
        // проверка существует ли запись в БД
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // проверка есть ли клиент в этой записи
        int clientsInBooking = countClientInBooking(bookingId, clientId);
        if (clientsInBooking > 0) {
            deleteClient(bookingId, clientId);

            // если после удаления клиента у записи нет других клиентов, удаляем запись
            if ((clientsInBooking - 1) == 0) {
                bookingRepository.delete(booking);
            }
            return ResponseEntity.noContent().build();

        } else {
            throw new IllegalStateException("Client doesnt have this booking");
        }
    }

    private int countClientsCountByDateAndTime(LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(DISTINCT bc.client_id) AS client_count " +
                "FROM bookings b " +
                "JOIN booking_clients bc ON b.id = bc.booking_id " +
                "WHERE b.date = ? AND b.time = ?";

        return jdbcTemplate.queryForObject(sql, Integer.class, date, time);
    }

    private Integer countClientInBooking(Long bookingId, Long clientId) {
        String sql = "SELECT COUNT(*) " +
                "FROM booking_clients " +
                "WHERE booking_id = ? " +
                "AND client_id = ?;";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, bookingId, clientId);
        return count;
    }

    private Integer deleteClient(Long bookingId, Long clientId) {
        String sql = "DELETE FROM booking_clients " +
                "WHERE booking_id = ? AND client_id = ?;";

        int count = jdbcTemplate.update(sql, bookingId, clientId);
        // count > 0 значи запись удалена
        return count;
    }

    private Integer countClientBookingsOnDate(Long clientId, LocalDate date) {
        String sql = "SELECT COUNT(*) " +
                "FROM bookings b " +
                "JOIN booking_clients c ON b.id = c.booking_id " +
                "WHERE c.client_id = ? " +
                "AND b.date = ?;";

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, clientId, date);
        return count;
    }

    private void validateBooking(Long clientId, LocalDateTime dateTime) {
        validateClientBookingLimit(clientId, dateTime.toLocalDate());
        validateWeekend(dateTime);
        validateWorkingHours(dateTime);
    }

    private void validateClientBookingLimit(Long clientId, LocalDate date) {
        if (countClientBookingsOnDate(clientId, date) >= config.getMaxBookingsForClient()) {
            throw new IllegalArgumentException("Exceeded number of bookings in a day");
        }
    }

    private void validateWeekend(LocalDateTime dateTime) {
        // проверка на выходной день (не рабочий день, запись не доступна)
        String dayOfWeek = dateTime.getDayOfWeek().toString();
        if (config.getWeekends().contains(dayOfWeek)) {
            throw new IllegalArgumentException("Cant reserve in weekends");
        }
    }

    private void validateWorkingHours(LocalDateTime dateTime) {
        // выясняем праздничный день (сокращенный график) или обычный
        List<String> workingTime = isHoliday(dateTime.toLocalDate()) ? config.getWorkingTimeHoliday()
                : config.getWorkingTimeRegular();

        String timeCheck = dateTime.toLocalTime().toString();
        if (!workingTime.contains(timeCheck)) {
            throw new IllegalArgumentException("Cant reserve out of working hours");
        }
    }

    private Boolean isHoliday(LocalDate date) {
        return config.getHolidays().contains(date);
    }

    private Long createNewBooking(Client client, LocalDate date, LocalTime time) {
        Booking newBooking = new Booking();
        Set<Client> clientSet = new HashSet<>();
        newBooking.setDate(date);
        newBooking.setTime(time);
        clientSet.add(client);
        newBooking.setClients(clientSet);
        return bookingRepository.save(newBooking).getId();
    }

    private Long addClientToExistingBooking(Booking existingBooking, Client client, LocalDateTime dateTime) {
        Booking booking = existingBooking;
        int reservedSlots = countClientsCountByDateAndTime(dateTime.toLocalDate(), dateTime.toLocalTime());

        if (reservedSlots < config.getMaxSlots()) {
            // Проверяем, записан ли уже клиент на это время
            if (!booking.getClients().contains(client)) {
                booking.getClients().add(client);
                return bookingRepository.save(booking).getId();
            } else {
                throw new IllegalStateException("Client already booked for this time.");
            }
        } else {
            // Если слоты заняты, выбрасываем исключение
            throw new IllegalStateException("No available slots for this time.");
        }
    }
    
}
