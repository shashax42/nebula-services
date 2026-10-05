package io.nebula.market.account.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.io.Serializable;
import java.util.UUID;

@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
@Comment("회원")
public class Account extends BaseTimeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Comment("ID")
    private UUID id;

    @Column(unique = true)
    @Comment("이메일")
    private String email;

    @Comment("이름")
    private String name;

    public Account update(Account account) {
        this.name = account.name != null ? account.name : this.name;
        return this;
    }
}