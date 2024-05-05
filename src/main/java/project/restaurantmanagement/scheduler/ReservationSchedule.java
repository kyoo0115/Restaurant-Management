package project.restaurantmanagement.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import project.restaurantmanagement.entity.ReservationEntity;
import project.restaurantmanagement.model.Type.ReservationStatus;
import project.restaurantmanagement.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReservationSchedule {

    private final ReservationRepository reservationRepository;

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void verifyReservationTimings() {
        LocalDateTime now = LocalDateTime.now();
        List<ReservationEntity> reservations = reservationRepository.findByStatusAndReservationTimeLessThanEqual(ReservationStatus.ACCEPTED, now.plusMinutes(10));

        for (ReservationEntity reservation : reservations) {
            if (reservation.getReservationTime().isBefore(now)) {
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepository.save(reservation);
            }
        }
    }
}
