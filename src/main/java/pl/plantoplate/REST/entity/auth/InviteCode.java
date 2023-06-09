/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */

package pl.plantoplate.REST.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity that represents invite code that generated to invite new member to group
 */
@Entity
@Table(name = "invite_code")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InviteCode {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column
    private int code;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne()
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    public InviteCode(int code, Group group, Role role, LocalDateTime expiredTime) {
        this.code = code;
        this.group = group;
        this.role = role;
        this.expiredTime = expiredTime;
    }


    @Override
    public String toString() {
        return "InviteCode{" +
                "id=" + id +
                ", code=" + code +
                ", group=" + group +
                '}';
    }
}
