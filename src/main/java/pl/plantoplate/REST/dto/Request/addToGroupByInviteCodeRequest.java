package pl.plantoplate.REST.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class addToGroupByInviteCodeRequest {

    private int code;
    private String email;
}
