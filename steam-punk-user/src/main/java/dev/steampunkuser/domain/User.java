package dev.steampunkuser.domain;

import dev.steampunkuser.common.converter.AES256ToStringConverter;
import dev.steampunkuser.common.entity.BaseTimeEntity;
import dev.steampunkuser.common.enumtype.ErrorCode;
import dev.steampunkuser.common.exception.ApiException;
import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.request.UserPasswordUpdateRequest;
import dev.steampunkuser.dto.request.UserPhoneNumberUpdateRequest;
import dev.steampunkuser.dto.request.UserPointUpdateRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "`user`")
public class User extends BaseTimeEntity {

    private static final long MIN_POINT = 0L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Convert(converter = AES256ToStringConverter.class)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Convert(converter = AES256ToStringConverter.class)
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "point", nullable = false)
    private Long point;

    private User(String email, String password, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.point = MIN_POINT;
    }

    public static User of(UserAddRequest request, Function<String, String> encodedFunction) {
        return new User(
                request.email(),
                encodedFunction.apply(request.password()),
                request.phoneNumber()
        );
    }

    public void updatePhoneNumber(UserPhoneNumberUpdateRequest request) {
        this.phoneNumber = request.phoneNumber();
    }

    public void updatePassword(UserPasswordUpdateRequest request, Function<String, String> encodedFunction) {
        this.password = encodedFunction.apply(request.password());
    }

    public void decreasePoint(UserPointUpdateRequest request) {
        Long requestPoint = request.point();
        if (requestPoint < MIN_POINT) {
            throw new IllegalArgumentException("음수는 허용되지 않습니다.");
        }
        if (requestPoint > this.point) {
            throw new ApiException(ErrorCode.NOT_ENOUGH_POINT);
        }
        this.point -= requestPoint;
    }

    public void increasePoint(UserPointUpdateRequest request) {
        Long requestPoint = request.point();
        if (requestPoint < MIN_POINT) {
            throw new IllegalArgumentException("음수는 허용되지 않습니다.");
        }
        this.point += requestPoint;
    }
}
