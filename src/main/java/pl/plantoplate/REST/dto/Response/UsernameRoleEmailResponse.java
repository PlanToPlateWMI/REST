package pl.plantoplate.REST.dto.Response;

import lombok.*;
import pl.plantoplate.REST.entity.auth.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
