package pl.plantoplate.REST.controller.dto.response;

import lombok.*;
import pl.plantoplate.REST.entity.auth.User;

@Getter
@Setter
@NoArgsConstructor
public class UsernameRoleEmailResponse {

    private String username;
    private String email;
    private String role;

    public UsernameRoleEmailResponse(User user){
        this.email = user.getEmail();
        this.role = user.getRole().name();
        this.username = user.getUsername();
    }
}
