package project.restaurantmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.restaurantmanagement.dto.*;
import project.restaurantmanagement.entity.ManagerEntity;
import project.restaurantmanagement.entity.ReservationEntity;
import project.restaurantmanagement.entity.RestaurantEntity;
import project.restaurantmanagement.exception.impl.AlreadyExistUserException;
import project.restaurantmanagement.exception.impl.IncorrectPasswordException;
import project.restaurantmanagement.model.Constants.ReservationStatus;
import project.restaurantmanagement.repository.ManagerRepository;
import project.restaurantmanagement.repository.ReservationRepository;
import project.restaurantmanagement.repository.RestaurantRepository;
import project.restaurantmanagement.security.TokenProvider;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService implements UserDetailsService {

    private final ManagerRepository managerRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final ReservationRepository reservationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return managerRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        "No such email -> " + username));
    }

    @Transactional
    public SignUpDto.Response register(SignUpDto.Request signUpRequest) {
        if (managerRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AlreadyExistUserException();
        }

        log.info("register user -> {}", signUpRequest.getEmail());
        signUpRequest.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        log.info("password -> {}", signUpRequest.getPassword());

        ManagerEntity saved = managerRepository.save(ManagerEntity.from(signUpRequest));

        return SignUpDto.Response.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .name(saved.getName())
                .build();
    }

    @Transactional
    public SignInDto.Response authenticate(SignInDto.Request signInRequest) {
        ManagerEntity managerEntity = managerRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + signInRequest.getEmail()));

        if (!passwordEncoder.matches(signInRequest.getPassword(), managerEntity.getPassword())) {
            throw new IncorrectPasswordException();
        }

        String token = tokenProvider.generateToken(managerEntity.getId(), managerEntity.getEmail(), managerEntity.getUserType());

        return SignInDto.Response.builder()
                .email(managerEntity.getEmail())
                .userType(managerEntity.getUserType())
                .token(token)
                .build();
    }

    @Transactional
    public RestaurantDto createRestaurant(RegisterRestaurantDto registerRestaurantDto, String header) {

        String token = tokenProvider.getToken(header);
        String email = tokenProvider.getUsername(token);

        ManagerEntity manager = managerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found: " + email));

        RestaurantEntity savedRestaurant = restaurantRepository.save(RestaurantEntity.of(registerRestaurantDto, manager));

        return RestaurantDto.from(savedRestaurant);
    }

    @Transactional
    public List<ReservationDto> viewReservations(String header, Long restaurantId) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found: " + managerId));

        RestaurantEntity restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new UsernameNotFoundException("Restaurant not found: " + restaurantId));

        if (!manager.getRestaurants().contains(restaurant)) {
            throw new UsernameNotFoundException("Restaurant not found: " + restaurantId);
        }

        return ReservationDto.from(reservationRepository.findReservationEntitiesByManagerEntity(manager));
    }

    @Transactional
    public void acceptReservation(String header, Long reservationId) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found: " + managerId));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new UsernameNotFoundException("Reservation not found: " + reservationId));

        if (!reservation.getRestaurantEntity().getManagerEntity().equals(manager)) {
            throw new SecurityException("Unauthorized access: Manager does not manage this restaurant");
        }

        reservation.setStatus(ReservationStatus.ACCEPTED);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void cancelReservation(String header, Long reservationId) {

        String token = tokenProvider.getToken(header);
        Long managerId = tokenProvider.getId(token);

        ManagerEntity manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found: " + managerId));

        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new UsernameNotFoundException("Reservation not found: " + reservationId));

        if (!reservation.getRestaurantEntity().getManagerEntity().equals(manager)) {
            throw new SecurityException("Unauthorized access: Manager does not manage this restaurant");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }
}
