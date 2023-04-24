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

package pl.plantoplate.REST.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalTime;

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

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expired_time", columnDefinition = "varchar(8)")
    private LocalTime expiredTime;


    @Override
    public String toString() {
        return "InviteCode{" +
                "id=" + id +
                ", code=" + code +
                ", group=" + user +
                '}';
    }
}
