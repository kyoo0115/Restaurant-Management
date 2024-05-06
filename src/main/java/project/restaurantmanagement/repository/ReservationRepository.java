package project.restaurantmanagement.repository;

import org.springframework.stereotype.Repository;
import project.restaurantmanagement.entity.ManagerEntity;
import project.restaurantmanagement.entity.ReservationEntity;
import project.restaurantmanagement.model.Type.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends BaseRepository<ReservationEntity, Long> {

    List<ReservationEntity> findReservationEntitiesByManagerEntity(ManagerEntity manager);

    List<ReservationEntity> findByStatusAndReservationTimeLessThanEqual(ReservationStatus reservationStatus, LocalDateTime localDateTime);
}
