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

/**
 * 예약 시간을 확인하고 관리하는 스케줄러 컴포넌트입니다.
 * 예약 시간이 지났는지 주기적으로 확인하고, 지난 예약을 취소 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class ReservationSchedule {

    private final ReservationRepository reservationRepository;

    /**
     * 매 분마다 실행되어, 승인된 예약 중 현재 시간 이후 10분 이내에 시작되는 예약을 확인하고,
     * 이미 시작 시간이 지난 예약은 자동으로 취소 상태로 변경합니다.
     */
    @Scheduled(cron = "0 * * * * *") // 매 분마다 실행
    @Transactional
    public void verifyReservationTimings() {
        LocalDateTime now = LocalDateTime.now();
        // 현재 시간부터 10분 이내에 시작하는 예약을 조회
        List<ReservationEntity> reservations = reservationRepository.findByStatusAndReservationTimeLessThanEqual(
                ReservationStatus.ACCEPTED, now.plusMinutes(10));

        for (ReservationEntity reservation : reservations) {
            if (reservation.getReservationTime().isBefore(now)) {
                reservation.setStatus(ReservationStatus.CANCELLED); // 예약 시간이 지났으면 예약 상태를 취소로 변경
                reservationRepository.save(reservation);
            }
        }
    }
}
