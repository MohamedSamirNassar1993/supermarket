package com.supermarket.modules.users.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateUserRequest {

    @Email
    private String email;

    @Size(min = 3, max = 100)
    private String username;

    private String firstName;

    private String lastName;

    private String phone;

    private UUID organizationId;

    private Set<UUID> roleIds;

    private Set<UUID> branchIds;

    private UUID primaryBranchId;

    private Boolean active;
}
