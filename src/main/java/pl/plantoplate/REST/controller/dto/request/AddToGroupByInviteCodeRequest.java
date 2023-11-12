package pl.plantoplate.REST.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddToGroupByInviteCodeRequest {

    private int code;
    private String email;
    private String password;
}
