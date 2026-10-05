package io.nebula.market.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.UUID;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
@Comment("회원")
public class Account extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Comment("ID")
    private UUID id;

    @Column(unique = true)
    @Comment("이메일")
    private String email;

    @Comment("이름")
    private String name;

    public static Account create(String name, String email, String picture) {
        return Account.builder()
                .name(name)
                .email(email)
                .build();
    }

    public Account update(Account account) {
        this.name = account.name != null ? account.name : this.name;
        return this;
    }
}