package pl.plantoplate.REST.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddToExistingGroupDto {

    private String email;
    private int groupCode;
}
