package project.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.restaurantmanagement.dto.ReservationDto;
import project.restaurantmanagement.entity.ManagerEntity;
import project.restaurantmanagement.entity.ReservationEntity;
import project.restaurantmanagement.entity.RestaurantEntity;
import project.restaurantmanagement.exception.GlobalException;
import project.restaurantmanagement.model.Type.AcceptStatus;
import project.restaurantmanagement.repository.ManagerRepository;
import project.restaurantmanagement.repository.ReservationRepository;
import project.restaurantmanagement.repository.RestaurantRepository;
import project.restaurantmanagement.security.TokenProvider;

import java.util.List;

import static project.restaurantmanagement.exception.ErrorCode.*;
import static project.restaurantmanagement.model.Type.ReservationStatus.*;
import static project.restaurantmanagement.model.Type.ReservationStatus.ACCEPTED;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ManagerRepository managerRepository;
    private final ReservationRepository reservationRepository;
    private final TokenProvider tokenProvider;
    private final RestaurantRepository restaurantRepository;


    /**
     * 매니저 별 예약 정보 조회
     */
    @Transactional
    public List<ReservationDto> viewReservations(String header, Long restaurantId) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new GlobalException(RESTAURANT_NOT_EXIST));

        if (!manager.getRestaurants().contains(restaurant)) {
            throw new GlobalException(SHOP_MANAGER_NOT_EXIST);
        }

        return ReservationDto.from(reservationRepository.findReservationEntitiesByManagerEntity(manager));
    }

    /**
     * 예약 정보를 이용하여 예약 승인/거절 결정
     */
    @Transactional
    public String acceptOrRefuseReservation(String header, Long reservationId, AcceptStatus acceptStatus) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new GlobalException(MANAGER_NOT_EXIST));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new GlobalException(RESERVATION_NOT_EXIST));

        checkReservation(reservation);

        reservation.setStatus(acceptStatus.getStatus() ? ACCEPTED : CANCELLED);

        reservationRepository.save(reservation);

        return "예약 번호 " + reservationId + "에 대한 " + (acceptStatus.getStatus() ? "승인" : "거절") + " 처리가 완료되었습니다.";
    }

    /**
     * 예약 검증
     */
    private void checkReservation(ReservationEntity reservation) {
        // 이미 취소된 예약인 경우
        if (reservation.getStatus() == CANCELLED) {
            throw new GlobalException(RESERVATION_ALREADY_CANCELED);
        }

        // 이미 완료 처리된 예약인 경우
        if (reservation.getStatus() == COMPLETED) {
            throw new GlobalException(RESERVATION_ALREADY_VISITED);
        }

        // 이미 확인(승인)한 예약인 경우
        if (reservation.getStatus() == ACCEPTED) {
            throw new GlobalException(RESERVATION_ALREADY_PROCESSED);
        }
    }
}
