package dev.steampunkuser.domain;

import dev.steampunkuser.common.converter.AES256ToStringConverter;
import dev.steampunkuser.common.entity.BaseTimeEntity;
import dev.steampunkuser.dto.request.UserAddRequest;
import dev.steampunkuser.dto.request.UserPasswordUpdateRequest;
import dev.steampunkuser.dto.request.UserPhoneNumberUpdateRequest;
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

    private User(String email, String password, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
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
}
